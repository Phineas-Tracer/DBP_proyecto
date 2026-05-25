package org.ide.dbp_proyecto.event;

import org.ide.dbp_proyecto.entity.User;
import org.springframework.context.ApplicationEvent;

public class UsuarioRegistradoEvent extends ApplicationEvent {

    private final User user;

    public UsuarioRegistradoEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}