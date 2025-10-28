package seedu.address.logic.commands.extractors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InfoCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code InfoEditCommand}s.
 */
public class InfoCommandExtractor {
    public static final String MESSAGE_TOO_MANY_PARAMETERS = "Too many parameters provided. Expected only index.";

    private InfoCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return InfoEditCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static InfoCommand extract(BareCommand bareCommand) throws ValidationException {
        // Extract and validate the index using the standard validation
        Index index = Validation.validateIndex(bareCommand, 0);

        // Check we don't have too many parameters
        if (bareCommand.parameterCount() > 1) {
            throw new ValidationException(MESSAGE_TOO_MANY_PARAMETERS);
        }

        return new InfoCommand(index);
    }
}
