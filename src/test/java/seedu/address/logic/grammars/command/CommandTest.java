package seedu.address.logic.grammars.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandTest {
    @Test
    public void parse_simpleCommand_success() {
        String cmdString = "test";

        Command cmd = assertDoesNotThrow(() -> Command.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
    }

    @Test
    public void parse_simpleCommandWithParameters_success() {
        String cmdString = "test param0 param1";

        Command cmd = assertDoesNotThrow(() -> Command.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(2, cmd.getAllParameters().length);
        assertEquals("param0", cmd.getParameter(0));
        assertEquals("param1", cmd.getParameter(1));
    }

    @Test
    public void parse_commandWithOptions_success() {
        String cmdString = "test /opt1:\"long value\" /opt2:\"single\"";

        Command cmd = assertDoesNotThrow(() -> Command.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
        assertEquals("long value", cmd.getOptionValue("opt1"));
        assertEquals("single", cmd.getOptionValue("opt2"));
    }

    @Test
    public void parse_complexCommand_success() {
        String cmdString = "complex param0 param1 /opt1:\"long value\" /opt2:\"single\"/ opt3";

        Command cmd = assertDoesNotThrow(() -> Command.parse(cmdString));

        assertEquals("complex", cmd.getImperative());
        assertEquals(2, cmd.getAllParameters().length);
        assertEquals("param0", cmd.getParameter(0));
        assertEquals("param1", cmd.getParameter(1));
        assertEquals("long value", cmd.getOptionValue("opt1"));
        assertEquals("single", cmd.getOptionValue("opt2"));
        assertTrue(cmd.hasOption("opt3"));
        assertFalse(cmd.hasOption("opt4"));
    }
}
