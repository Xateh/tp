package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

/**
 * Tests for {@link HistoryCommandExtractor}.
 */
public class HistoryCommandExtractorTest {
    @Test
    public void extract_validHistoryCommand_returnsHistoryCommand() throws LexerException,
            ParserException, ValidationException {
        HistoryCommand command = HistoryCommandExtractor.extract(BareCommand.parse("history"));
        assertEquals(HistoryCommand.class, command.getClass());
    }

    @Test
    public void extract_historyWithParameters_returnsHistoryCommand() throws LexerException,
            ParserException, ValidationException {
        HistoryCommand command = HistoryCommandExtractor.extract(BareCommand.parse("history extra"));
        assertEquals(HistoryCommand.class, command.getClass());
    }

    @Test
    public void extract_historyWithOptions_returnsHistoryCommand() throws ValidationException {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative(HistoryCommand.COMMAND_WORD);
        builder.setOption("limit", "5");
        BareCommand bareCommand = builder.build();

        HistoryCommand command = HistoryCommandExtractor.extract(bareCommand);
        assertEquals(HistoryCommand.class, command.getClass());
    }
}
