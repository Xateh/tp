package seedu.address.logic.exceptions;

/**
 * Superclass of all command assembly exceptions. Represents all exceptions that occur in the construction of the
 * executable command object.
 */
public class AssemblyException extends Exception {
    public AssemblyException(String message) {
        super(message);
    }
}
