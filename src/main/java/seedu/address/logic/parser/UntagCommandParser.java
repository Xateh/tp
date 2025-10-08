package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new {@code UntagCommand} object.
 */
public class UntagCommandParser implements Parser<UntagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code UntagCommand}
     * and returns a {@code UntagCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UntagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);

        if (argMultimap.getPreamble().isEmpty() || !argMultimap.getValue(PREFIX_TAG).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UntagCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TAG);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    UntagCommand.MESSAGE_USAGE), pe);
        }

        Tag tagToRemove = ParserUtil.parseTag(argMultimap.getValue(PREFIX_TAG).get());
        return new UntagCommand(index, tagToRemove);
    }
}
