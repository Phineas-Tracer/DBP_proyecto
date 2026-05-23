package org.ide.dbp_proyecto.exception;

public class GooglePlacesException extends RuntimeException {
    public GooglePlacesException(String message) {
        super(message);
    }
    public GooglePlacesException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
