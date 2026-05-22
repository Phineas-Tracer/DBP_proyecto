package org.ide.dbp_proyecto.Excepcion;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
