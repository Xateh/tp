package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

public class DeleteCommandExtractorTest {
    @Test
    public void parse_validArgs_returnsTagCommand() throws LexerException, ParserException, ValidationException {
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON),
                DeleteCommandExtractor.extract(BareCommand.parse("delete 1")));
    }

    @Test
    public void parse_invalidArgsInvalidIndex_throwsException() {
        assertThrows(ValidationException.class, () ->
                DeleteCommandExtractor.extract(BareCommand.parse("delete")));
        assertThrows(ValidationException.class, () ->
                DeleteCommandExtractor.extract(BareCommand.parse("delete a")));
        assertThrows(ValidationException.class, () ->
                DeleteCommandExtractor.extract(BareCommand.parse("delete 0")));
    }
}
