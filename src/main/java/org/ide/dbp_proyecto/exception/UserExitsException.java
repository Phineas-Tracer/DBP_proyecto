package org.ide.dbp_proyecto.exception;

public class UserExitsException extends RuntimeException {
    public UserExitsException(String message) {
        super(message);
    }
}
