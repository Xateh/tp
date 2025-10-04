package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object.
 */
public class TagCommandParser implements Parser<TagCommand> {
    /**
     * Parse given {@code String} of arguments in the context of TagCommand
     * and returns a TagCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public TagCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        String[] splitArgs = trimmedArgs.split("\\s+");
        if (splitArgs.length < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        }

        Index index;
        try {
            index = ParserUtil.parseIndex(splitArgs[0]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE), pe);
        }

        String[] tagNames = Arrays.copyOfRange(splitArgs, 1, splitArgs.length);
        if (tagNames.length == 0) {
            throw new ParseException(TagCommand.MESSAGE_NO_TAGS_PROVIDED);
        }

        List<String> tagNamesList = Arrays.asList(tagNames);
        Set<Tag> tags = ParserUtil.parseTags(tagNamesList);

        if (tags.isEmpty()) {
            throw new ParseException(TagCommand.MESSAGE_NO_TAGS_PROVIDED);
        }

        return new TagCommand(index, tags);
    }
}
