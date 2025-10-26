package seedu.address.logic.commands.exceptions;

import seedu.address.logic.commands.Command;
import seedu.address.logic.exceptions.AssemblyException;

/**
 * Represents an error which occurs during the resolution of a specific {@link Command} to execute.
 */
public final class ResolutionException extends AssemblyException {
    public ResolutionException(String message) {
        super(message);
    }
}
