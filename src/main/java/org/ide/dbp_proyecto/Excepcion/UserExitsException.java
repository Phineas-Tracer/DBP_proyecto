package org.ide.dbp_proyecto.Excepcion;

public class UserExitsException extends RuntimeException {
    public UserExitsException(String message) {
        super(message);
    }
}
