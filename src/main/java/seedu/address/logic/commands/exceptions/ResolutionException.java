package seedu.address.logic.commands.exceptions;

/**
 * Represents an error which occurs during the resolution of a specific {@link Command} to execute.
 */
public class ResolutionException extends Exception {
    public ResolutionException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ValidationException} with the specified detail {@code message} and {@code cause}.
     */
    public ResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
