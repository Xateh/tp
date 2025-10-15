package seedu.address.logic.commands.exceptions;

/**
 * Represents an error which occurs during the validation of a {@link Command}'s parameters and options.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ValidationException} with the specified detail {@code message} and {@code cause}.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
