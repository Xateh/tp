package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void extract_historyWithParameters_throwsValidationException() throws LexerException,
            ParserException {
        assertThrows(ValidationException.class, () ->
            HistoryCommandExtractor.extract(BareCommand.parse("history extra")));
    }

    @Test
    public void extract_historyWithOptions_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative(HistoryCommand.COMMAND_WORD);
        builder.setOption("limit", "5");
        BareCommand bareCommand = builder.build();

        assertThrows(ValidationException.class, () -> HistoryCommandExtractor.extract(bareCommand));
    }

    @Test
    public void extract_wrongImperative_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("tag");
        BareCommand bareCommand = builder.build();

        assertThrows(ValidationException.class, () -> HistoryCommandExtractor.extract(bareCommand));
    }
}
