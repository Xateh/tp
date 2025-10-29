package seedu.address.logic.commands.exceptions;

import seedu.address.logic.commands.Command;
import seedu.address.logic.exceptions.AssemblyException;

/**
 * Represents an error which occurs during the validation of a {@link Command}'s parameters and options.
 */
public final class ValidationException extends AssemblyException {
    public ValidationException(String message) {
        super(message);
    }
}
