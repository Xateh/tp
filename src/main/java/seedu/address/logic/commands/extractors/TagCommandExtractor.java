package seedu.address.logic.commands.extractors;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.tag.Tag;

/**
 * Extractor that builds {@code TagCommand}s.
 */
public class TagCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected integer, got %1$s";
    public static final String MESSAGE_TAGS_UNSPECIFIED = "At least one tag must be specified.";

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return TagCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static TagCommand extract(BareCommand bareCommand) throws ValidationException {
        String[] params = bareCommand.getAllParameters();

        // extract index
        if (params.length <= 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }
        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        }

        // extract tags
        if (params.length <= 1) {
            throw new ValidationException(MESSAGE_TAGS_UNSPECIFIED);
        }
        Set<Tag> tags = new HashSet<>();
        for (int i = 1; i < params.length; i++) {
            tags.add(new Tag(params[i]));
        }

        return new TagCommand(index, tags);
    }
}
