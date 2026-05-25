package org.ide.dbp_proyecto.event;

import org.ide.dbp_proyecto.entity.LugarColeccionado;
import org.springframework.context.ApplicationEvent;

public class CheckinRealizadoEvent extends ApplicationEvent {

    private final LugarColeccionado lugarColeccionado;

    public CheckinRealizadoEvent(Object source, LugarColeccionado lugarColeccionado) {
        super(source);
        this.lugarColeccionado = lugarColeccionado;
    }

    public LugarColeccionado getLugarColeccionado() {
        return lugarColeccionado;
    }
}