package uk.nhs.digital.nhsconnect.nhais.exceptions;

public class NhaisBaseRuntimeException extends RuntimeException {

    public NhaisBaseRuntimeException(String message) {
        super(message);
    }

    public NhaisBaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NhaisBaseRuntimeException(Throwable cause) {
        super(cause);
    }
}