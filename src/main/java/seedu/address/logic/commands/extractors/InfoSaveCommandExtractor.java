package seedu.address.logic.commands.extractors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InfoSaveCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.Info;

/**
 * Extractor that builds {@code InfoSaveCommand}s.
 */
public class InfoSaveCommandExtractor {
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified for infosave command.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INFO_UNSPECIFIED = "Information not specified for infosave command.";

    private InfoSaveCommandExtractor() {}

    /**
     * Extracts command parameters from the given Command object.
     */
    public static InfoSaveCommand extract(BareCommand bareCommand) throws ValidationException {
        String[] params = bareCommand.getAllParameters();

        if (params.length == 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }

        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        }

        if (params.length < 2) {
            throw new ValidationException(MESSAGE_INFO_UNSPECIFIED);
        }

        // Reconstruct the info string from all remaining parameters
        StringBuilder infoBuilder = new StringBuilder();
        for (int i = 1; i < params.length; i++) {
            if (i > 1) {
                infoBuilder.append(" ");
            }
            infoBuilder.append(params[i]);
        }

        String infoString = infoBuilder.toString();

        // Remove surrounding quotes if present
        if (infoString.startsWith("\"") && infoString.endsWith("\"") && infoString.length() > 1) {
            infoString = infoString.substring(1, infoString.length() - 1);
        }

        // Handle escaped quotes
        infoString = infoString.replace("\\\"", "\"");

        Info info = new Info(infoString);
        return new InfoSaveCommand(index, info);
    }
}
