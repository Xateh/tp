package seedu.address.logic.commands.extractors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InfoEditCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code InfoEditCommand}s.
 */
public class InfoEditCommandExtractor {
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_TOO_MANY_PARAMETERS = "Too many parameters provided for infoedit command.";

    private InfoEditCommandExtractor() {}

    /**
     * Extracts command parameters from the given Command object.
     *
     * @param bareCommand Command to extract parameters from.
     * @return InfoEditCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static InfoEditCommand extract(BareCommand bareCommand) throws ValidationException {
        String[] params = bareCommand.getAllParameters();

        if (params.length == 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }

        if (params.length > 1) {
            throw new ValidationException(MESSAGE_TOO_MANY_PARAMETERS);
        }

        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        } catch (IndexOutOfBoundsException e) {
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, params[0]));
        }

        return new InfoEditCommand(index);
    }
}
