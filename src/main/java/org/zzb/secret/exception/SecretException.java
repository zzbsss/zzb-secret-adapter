package org.zzb.secret.exception;

public class SecretException extends RuntimeException {

    private static final long serialVersionUID = -3232706417844019328L;

    private int code;



    public SecretException() {
    }

    public SecretException(int code, String message) {
        super(message);
        this.code = code;
    }


    public SecretException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


    public SecretException(String message) {
        super(message);
    }


    public SecretException(String message, Throwable cause) {
        super(message, cause);
    }


    public SecretException(Throwable cause) {
        super(cause);
    }
}
