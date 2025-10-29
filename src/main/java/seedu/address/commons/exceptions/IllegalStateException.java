package seedu.address.commons.exceptions;

/**
 * Signals that some given state to be stored does not fulfill some constraints.
 */
public class IllegalStateException extends RuntimeException {
    /**
      * @param message should contain relevant information on the reason for invalid state to save
      */
    public IllegalStateException(String message) {
        super(message);
    }
}
