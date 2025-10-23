package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.InfoEditCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

public class InfoEditCommandExtractorTest {

    @Test
    public void parse_validArgs_returnsInfoEditCommand() throws LexerException, ParserException, ValidationException {
        // valid index
        assertEquals(new InfoEditCommand(INDEX_FIRST_PERSON),
                InfoEditCommandExtractor.extract(BareCommand.parse("info 1")));

        assertEquals(new InfoEditCommand(INDEX_SECOND_PERSON),
                InfoEditCommandExtractor.extract(BareCommand.parse("info 2")));

        // larger index
        assertEquals(new InfoEditCommand(INDEX_FIRST_PERSON.fromOneBased(10)),
                InfoEditCommandExtractor.extract(BareCommand.parse("info 10")));
    }

    @Test
    public void parse_invalidArgs_throwsValidationException() throws LexerException, ParserException {
        // no parameters
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info")));

        // invalid index
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info 0")));
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info -1")));
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info a")));
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info abc")));

        // too many parameters
        assertThrows(ValidationException.class, () ->
                InfoEditCommandExtractor.extract(BareCommand.parse("info 1 extra")));
    }

    @Test
    public void parse_leadingTrailingWhitespace_success() throws LexerException, ParserException, ValidationException {
        // should handle whitespace gracefully
        assertEquals(new InfoEditCommand(INDEX_FIRST_PERSON),
                InfoEditCommandExtractor.extract(BareCommand.parse("info   1   ")));
    }
}
