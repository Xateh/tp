package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code DeleteCommand}s.
 */
public class DeleteCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";

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
        String param0 = Validation.validateParameter(bareCommand, 0, ParameterKind.NORMAL).getValue();
        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(param0));
        } catch (NumberFormatException e) {
            // only thrown by Integer::parseInt
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, param0));
        } catch (IndexOutOfBoundsException e) {
            // only thrown by Index::fromOneBased
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, param0));
        }

        return new DeleteCommand(index);
    }
}
