package util;

/** Checked exception for every failed business rule. */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) { super(message); }

    public ValidationException(String message, Throwable cause) { super(message, cause); }
}