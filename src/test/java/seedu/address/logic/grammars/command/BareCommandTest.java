package seedu.address.logic.grammars.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import org.junit.jupiter.api.Test;

public class BareCommandTest {
    @Test
    public void parameters_kindAndPredicateMatch_success() {
        Parameter normalParameter = new BareCommand.Parameter(ParameterKind.NORMAL, "value");
        assertEquals(ParameterKind.NORMAL, normalParameter.getKind());
        assertTrue(normalParameter.isNormal());

        Parameter additiveParameter = new BareCommand.Parameter(ParameterKind.ADDITIVE, "value");
        assertEquals(ParameterKind.ADDITIVE, additiveParameter.getKind());
        assertTrue(additiveParameter.isAdditive());

        Parameter subtractiveParameter = new BareCommand.Parameter(ParameterKind.SUBTRACTIVE, "value");
        assertEquals(ParameterKind.SUBTRACTIVE, subtractiveParameter.getKind());
        assertTrue(subtractiveParameter.isSubtractive());
    }

    @Test
    public void parse_simpleCommand_success() {
        String cmdString = "test";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
    }

    @Test
    public void parse_simpleCommandWithParameters_success() {
        String cmdString = "test param0 +param1 -param2";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(3, cmd.getAllParameters().size());
        assertTrue(cmd.getParameter(0).isNormal());
        assertEquals("param0", cmd.getParameter(0).getValue());
        assertTrue(cmd.getParameter(1).isAdditive());
        assertEquals("param1", cmd.getParameter(1).getValue());
        assertTrue(cmd.getParameter(2).isSubtractive());
        assertEquals("param2", cmd.getParameter(2).getValue());
    }

    @Test
    public void parse_commandWithOptions_success() {
        String cmdString = "test /opt1:\"long value\" /opt2:\"single\"";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
        assertEquals("long value", cmd.getOptionValue("opt1"));
        assertEquals("single", cmd.getOptionValue("opt2"));
    }

    @Test
    public void parse_complexCommand_success() {
        String cmdString = "complex param0 +param1 -param2 /opt1:\"long value\" /opt2:\"single\"/ opt3";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("complex", cmd.getImperative());
        assertEquals(3, cmd.getAllParameters().size());
        assertTrue(cmd.getParameter(0).isNormal());
        assertEquals("param0", cmd.getParameter(0).getValue());
        assertTrue(cmd.getParameter(1).isAdditive());
        assertEquals("param1", cmd.getParameter(1).getValue());
        assertTrue(cmd.getParameter(2).isSubtractive());
        assertEquals("param2", cmd.getParameter(2).getValue());
        assertEquals("long value", cmd.getOptionValue("opt1"));
        assertEquals("single", cmd.getOptionValue("opt2"));
        assertTrue(cmd.hasOption("opt3"));
        assertFalse(cmd.hasOption("opt4"));
    }
}
