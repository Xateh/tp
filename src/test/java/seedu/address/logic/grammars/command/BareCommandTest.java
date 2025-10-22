package seedu.address.logic.grammars.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class BareCommandTest {
    @Test
    public void parse_simpleCommand_success() {
        String cmdString = "test";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
    }

    @Test
    public void parse_simpleCommandWithParameters_success() {
        String cmdString = "test param0 param1";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(2, cmd.getAllParameters().length);
        assertEquals("param0", cmd.getParameter(0));
        assertEquals("param1", cmd.getParameter(1));
    }

    @Test
    public void parseCommandWithBooleanOptions_success() {
        String cmdString = "test /opt1 /opt2";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
        assertTrue(cmd.hasOption("opt1"));
        assertEquals(0, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.hasOption("opt2"));
        assertEquals(0, cmd.getOptionMultiplicity("opt2"));
        assertEquals(-1, cmd.getOptionMultiplicity("opt3"));
    }

    @Test
    public void parse_commandWithKeyValueOptions_success() {
        String cmdString = "test /opt1:\"long value\" /opt2:\"single\"";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
        assertTrue(cmd.getOptionValue("opt1").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt1").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.getOptionValue("opt2").isPresent());
        assertEquals("single", cmd.getOptionValue("opt2").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt2"));
    }

    @Test
    public void parse_commandWithRepeatedKeyValueOptions_success() {
        String cmdString = "test /opt:\"long value\" /opt:\"single\"";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().length);
        assertTrue(cmd.getOptionValue("opt").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt").get());
        assertTrue(cmd.getOptionAllValues("opt").isPresent());
        List<String> expectedOptionValues = List.of("long value", "single");
        assertEquals(expectedOptionValues, cmd.getOptionAllValues("opt").get());
        assertEquals(2, cmd.getOptionMultiplicity("opt"));
    }

    @Test
    public void parse_commandWithMissingOptions_success() {
        String cmdString = "test";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertFalse(cmd.hasOption("opt"));
        assertFalse(cmd.getOptionValue("opt").isPresent());
        assertFalse(cmd.getOptionAllValues("opt").isPresent());
        assertEquals(-1, cmd.getOptionMultiplicity("opt"));
    }

    @Test
    public void parse_commandWithRepeatedBooleanOptions_success() {
        String cmdString = "complex /opt /opt";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertTrue(cmd.hasOption("opt"));
        assertTrue(cmd.getOptionValue("opt").isEmpty());
        assertEquals(0, cmd.getOptionMultiplicity("opt"));
        assertFalse(cmd.hasOption("nonopt"));
        assertTrue(cmd.getOptionValue("nonopt").isEmpty());
        assertEquals(-1, cmd.getOptionMultiplicity("nonopt"));
    }

    @Test
    public void parse_complexCommand_success() {
        String cmdString = "complex param0 param1 /opt1:\"long value\" /opt2:\"single\"/ opt3 /opt3 /opt2:another "
                + "/opt4 /opt4:value";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("complex", cmd.getImperative());
        assertEquals(2, cmd.getAllParameters().length);
        assertEquals("param0", cmd.getParameter(0));
        assertEquals("param1", cmd.getParameter(1));
        assertTrue(cmd.getOptionValue("opt1").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt1").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.getOptionValue("opt2").isPresent());
        assertEquals("single", cmd.getOptionValue("opt2").get());
        assertEquals(2, cmd.getOptionMultiplicity("opt2"));
        assertTrue(cmd.hasOption("opt3"));
        assertTrue(cmd.getOptionValue("opt3").isEmpty());
        assertEquals(0, cmd.getOptionMultiplicity("opt3"));
        assertTrue(cmd.hasOption("opt4"));
        assertTrue(cmd.getOptionValue("opt4").isPresent());
        assertEquals(1, cmd.getOptionMultiplicity("opt4"));
        assertFalse(cmd.hasOption("opt5"));
        assertTrue(cmd.getOptionValue("opt5").isEmpty());
        assertEquals(-1, cmd.getOptionMultiplicity("opt5"));
    }
}
