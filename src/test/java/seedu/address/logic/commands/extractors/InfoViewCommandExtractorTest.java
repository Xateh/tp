package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.InfoViewCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

public class InfoViewCommandExtractorTest {

    @Test
    public void parse_validArgs_returnsInfoViewCommand() throws LexerException, ParserException, ValidationException {
        // valid index
        assertEquals(new InfoViewCommand(INDEX_FIRST_PERSON),
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview 1")));

        assertEquals(new InfoViewCommand(INDEX_SECOND_PERSON),
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview 2")));

        // larger index
        assertEquals(new InfoViewCommand(INDEX_FIRST_PERSON.fromOneBased(10)),
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview 10")));
    }

    @Test
    public void parse_invalidArgs_throwsValidationException() throws LexerException, ParserException {
        // no parameters
        assertThrows(ValidationException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview")));

        // invalid index
        assertThrows(ValidationException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview 0")));
        assertThrows(LexerException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview -1")));
        assertThrows(ValidationException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview a")));
        assertThrows(ValidationException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview abc")));

        // too many parameters
        assertThrows(ValidationException.class, () ->
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview 1 extra")));
    }

    @Test
    public void parse_leadingTrailingWhitespace_success() throws LexerException, ParserException, ValidationException {
        // should handle whitespace gracefully
        assertEquals(new InfoViewCommand(INDEX_FIRST_PERSON),
                InfoViewCommandExtractor.extract(BareCommand.parse("infoview   1   ")));
    }
}
