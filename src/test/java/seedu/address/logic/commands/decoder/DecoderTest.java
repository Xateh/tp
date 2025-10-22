package seedu.address.logic.commands.decoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

/**
 * Test class for testing the decoder's command resolution. Note that tests here should not test behaviour of the
 * extractors; only test if the decoder correctly finds the right command.
 */
public class DecoderTest {
    @Test
    public void decode_tagCommandExactMatch_success() throws
            LexerException, ParserException, ResolutionException, ValidationException {
        Command tagCommand = Decoder.decode(BareCommand.parse("tag 1 test1 test2"));
        assertEquals(TagCommand.class, tagCommand.getClass());
    }

    @Test
    public void decode_fieldCommandExactMatch_success() throws
            LexerException, ParserException, ResolutionException, ValidationException {
        Command fieldCommand = Decoder.decode(BareCommand.parse("field 1 /k:v"));
        assertEquals(FieldCommand.class, fieldCommand.getClass());
    }

    @Test
    public void decode_findCommandExactMatch_success() throws
            LexerException, ParserException, ResolutionException, ValidationException {
        Command findCommand = Decoder.decode(BareCommand.parse("find test"));
        assertEquals(FindCommand.class, findCommand.getClass());
    }

    @Test
    public void decode_deleteCommandExactMatch_success() throws
            LexerException, ParserException, ResolutionException, ValidationException {
        Command deleteCommand = Decoder.decode(BareCommand.parse("delete 1"));
        assertEquals(DeleteCommand.class, deleteCommand.getClass());
    }

    @Test
    public void decode_editCommandExactMatch_success() throws
            LexerException, ParserException, ResolutionException, ValidationException {
        Command editCommand = Decoder.decode(BareCommand.parse("edit 1 /name:Test"));
        assertEquals(EditCommand.class, editCommand.getClass());
    }
}
