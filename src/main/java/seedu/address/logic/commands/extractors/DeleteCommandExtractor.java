package seedu.address.logic.commands.extractors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code DeleteCommand}s.
 */
public final class DeleteCommandExtractor {
    private DeleteCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return DeleteCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static DeleteCommand extract(BareCommand bareCommand) throws ValidationException {

        Index index;
        try {
            index = Validation.validateIndex(bareCommand, 0);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage() + "\n" + "Expected an index (must be a positive integer).");
        }

        return new DeleteCommand(index);
    }
}
