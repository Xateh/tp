package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InfoEditCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code InfoEditCommand}s.
 */
public class InfoEditCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_TOO_MANY_PARAMETERS = "Too many parameters provided. Expected only index.";

    private InfoEditCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return InfoEditCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static InfoEditCommand extract(BareCommand bareCommand) throws ValidationException {
        // First, check parameter count manually
        List<BareCommand.Parameter> allParams = bareCommand.getAllParameters();

        // Count only NORMAL parameters (not flags)
        long normalParamCount = allParams.stream()
                .filter(param -> param.getKind() == ParameterKind.NORMAL)
                .count();

        if (normalParamCount > 1) {
            throw new ValidationException(MESSAGE_TOO_MANY_PARAMETERS);
        }

        // Extract and validate the index using the standard validation
        Index index = Validation.validateIndex(bareCommand, 0);

        return new InfoEditCommand(index);
    }
}
