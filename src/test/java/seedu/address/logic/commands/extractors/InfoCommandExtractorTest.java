package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.InfoCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

public class InfoCommandExtractorTest {

    @Test
    public void parse_validArgs_returnsInfoEditCommand() throws LexerException, ParserException, ValidationException {
        // valid index
        assertEquals(new InfoCommand(INDEX_FIRST_PERSON),
                InfoCommandExtractor.extract(BareCommand.parse("info 1")));

        assertEquals(new InfoCommand(INDEX_SECOND_PERSON),
                InfoCommandExtractor.extract(BareCommand.parse("info 2")));

        // larger index
        assertEquals(new InfoCommand(INDEX_FIRST_PERSON.fromOneBased(10)),
                InfoCommandExtractor.extract(BareCommand.parse("info 10")));
    }

    @Test
    public void parse_invalidArgs_throwsValidationException() throws LexerException, ParserException {
        // no parameters
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info")));

        // invalid index
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info 0")));
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info -1")));
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info a")));
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info abc")));

        // too many parameters
        assertThrows(ValidationException.class, () ->
                InfoCommandExtractor.extract(BareCommand.parse("info 1 extra")));
    }

    @Test
    public void parse_leadingTrailingWhitespace_success() throws LexerException, ParserException, ValidationException {
        // should handle whitespace gracefully
        assertEquals(new InfoCommand(INDEX_FIRST_PERSON),
                InfoCommandExtractor.extract(BareCommand.parse("info   1   ")));
    }
}
