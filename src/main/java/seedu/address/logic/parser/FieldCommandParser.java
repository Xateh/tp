package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@link FieldCommand} object.
 */
public class FieldCommandParser implements Parser<FieldCommand> {

    @Override
    public FieldCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        String commandString = trimmedArgs.isEmpty()
                ? FieldCommand.COMMAND_WORD
                : FieldCommand.COMMAND_WORD + " " + trimmedArgs;
        try {
            BareCommand bare = BareCommand.parse(commandString);
            return new FieldCommand(bare);
        } catch (LexerException | ParserException ex) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FieldCommand.MESSAGE_USAGE), ex);
        } catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), ex);
        }
    }
}

