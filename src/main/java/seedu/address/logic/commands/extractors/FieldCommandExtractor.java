package seedu.address.logic.commands.extractors;

import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.decoder.Decoder;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Extractor that builds {@code FieldCommand}s from {@link BareCommand}s.
 */
public final class FieldCommandExtractor {
    private FieldCommandExtractor() {}

    /**
     * Parses {@code userInput} with the grammar system and decodes the resulting {@link BareCommand}
     * into an executable {@link Command}.
     *
     * @param userInput the raw user input string.
     * @return the decoded command.
     * @throws ValidationException if validation fails during decoding.
     * @throws ParseException if the user input cannot be parsed by the grammar system.
     */
    public static Command decode(String userInput) throws ValidationException, ParseException {
        try {
            return Decoder.decode(BareCommand.parse(userInput));
        } catch (LexerException | ParserException | ResolutionException e) {
            System.out.println(e.getMessage());
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND, e);
        }
    }

    /**
     * Extracts the parameters required to build a {@link FieldCommand}.
     *
     * @param bareCommand command parsed by the grammar system.
     * @return a {@link FieldCommand} that can be executed.
     * @throws ValidationException if the command parameters fail validation.
     */
    public static FieldCommand extract(BareCommand bareCommand) throws ValidationException {
        try {
            return new FieldCommand(bareCommand);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage(), ex);
        }
    }
}
