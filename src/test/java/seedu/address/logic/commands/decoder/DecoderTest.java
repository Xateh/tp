package seedu.address.logic.commands.decoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.exceptions.AssemblyException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Test class for testing the decoder's command resolution. Note that tests here should not test behaviour of the
 * extractors; only test if the decoder correctly finds the right command.
 */
public class DecoderTest {
    @Nested
    class ExactMatchTest {
        @Test
        public void decode_addCommandExactMatch_success() throws
                AssemblyException {
            Command tagCommand = Decoder.decode(BareCommand.parse("clear"));
            assertEquals(ClearCommand.class, tagCommand.getClass());
        }

        @Test
        public void decode_clearCommandExactMatch_success() throws
                AssemblyException {
            Command tagCommand = Decoder.decode(BareCommand.parse("clear"));
            assertEquals(ClearCommand.class, tagCommand.getClass());
        }

        @Test
        public void decode_deleteCommandExactMatch_success() throws
                AssemblyException {
            Command deleteCommand = Decoder.decode(BareCommand.parse("delete 1"));
            assertEquals(DeleteCommand.class, deleteCommand.getClass());
        }

        @Test
        public void decode_editCommandExactMatch_success() throws
                AssemblyException {
            Command editCommand = Decoder.decode(BareCommand.parse("edit 1 /name:Test"));
            assertEquals(EditCommand.class, editCommand.getClass());
        }

        @Test
        public void decode_exitCommandExactMatch_success() throws
                AssemblyException {
            Command tagCommand = Decoder.decode(BareCommand.parse("exit"));
            assertEquals(ExitCommand.class, tagCommand.getClass());
        }

        @Test
        public void decode_fieldCommandExactMatch_success() throws
                AssemblyException {
            Command fieldCommand = Decoder.decode(BareCommand.parse("field 1 /k:v"));
            assertEquals(FieldCommand.class, fieldCommand.getClass());
        }

        @Test
        public void decode_findCommandExactMatch_success() throws
                AssemblyException {
            Command fieldCommand = Decoder.decode(BareCommand.parse("find Sally /name"));
            assertEquals(FindCommand.class, fieldCommand.getClass());
        }

        @Test
        public void decode_helpCommandExactMatch_success() throws
                AssemblyException {
            Command tagCommand = Decoder.decode(BareCommand.parse("help"));
            assertEquals(HelpCommand.class, tagCommand.getClass());
        }

        @Test
        public void decode_historyCommandExactMatch_success()
                throws AssemblyException {
            Command historyCommand = Decoder.decode(BareCommand.parse("history"));
            assertEquals(HistoryCommand.class, historyCommand.getClass());
        }

        @Test
        public void decode_listCommandExactMatch_success()
                throws AssemblyException {
            Command historyCommand = Decoder.decode(BareCommand.parse("list"));
            assertEquals(ListCommand.class, historyCommand.getClass());
        }

        @Test
        public void decode_tagCommandExactMatch_success() throws
                AssemblyException {
            Command tagCommand = Decoder.decode(BareCommand.parse("tag 1 +test1 -test2"));
            assertEquals(TagCommand.class, tagCommand.getClass());
        }
    }

    @Test
    public void decode_uniquePrefixExactMatch_success() throws AssemblyException {
        Command command = Decoder.decode(BareCommand.parse("del 1"));
        assertEquals(DeleteCommand.class, command.getClass());
    }

    @Test
    public void decode_uniquePrefixNoMatch_throwsException() {
        assertThrows(ResolutionException.class, () -> Decoder.decode(BareCommand.parse("random")));
    }

    @Test
    public void decode_uniquePrefixAmbiguousMatch_throwsException() {
        assertThrows(ResolutionException.class, () -> Decoder.decode(BareCommand.parse("e")));
    }

    @Test
    public void decode_linkCommandExactMatch_success() throws
            AssemblyException {
        Command linkCommand = Decoder.decode(BareCommand.parse("link 1 mentor 2"));
        assertEquals(LinkCommand.class, linkCommand.getClass());
    }
}

