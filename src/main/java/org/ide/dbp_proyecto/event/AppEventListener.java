package org.ide.dbp_proyecto.event;

import lombok.RequiredArgsConstructor;
import org.ide.dbp_proyecto.entity.User;
import org.ide.dbp_proyecto.entity.LugarColeccionado;
import org.ide.dbp_proyecto.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AppEventListener {

    private static final Logger log = LoggerFactory.getLogger(AppEventListener.class);

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUsuarioRegistrado(UsuarioRegistradoEvent event) {
        User user = event.getUser();
        log.info("Evento: usuario registrado → {}", user.getEmail());
        emailService.enviarEmailBienvenida(user.getEmail(), user.getName());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCheckinRealizado(CheckinRealizadoEvent event) {
        LugarColeccionado lugar = event.getLugarColeccionado();
        log.info("Evento: check-in realizado → usuario={} lugar={}",
                lugar.getUsuario().getEmail(),
                lugar.getPuntoDeInteres().getNombre());

        String fechaFormateada = lugar.getFecha()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        emailService.enviarEmailCheckin(
                lugar.getUsuario().getEmail(),
                lugar.getPuntoDeInteres().getNombre(),
                fechaFormateada
        );
    }
}