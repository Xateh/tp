package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");
        Warning warning = Warning.duplicateInputIgnored("duplicate input");
        CommandResult commandResultWithWarning = new CommandResult("feedback", List.of(warning));

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));
        assertTrue(commandResultWithWarning.equals(new CommandResult("feedback", List.of(warning))));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));
        assertFalse(commandResultWithWarning.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));
        assertFalse(commandResultWithWarning.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different warnings -> returns false
        assertFalse(commandResult.equals(commandResultWithWarning));
        assertFalse(commandResultWithWarning.equals(new CommandResult("feedback", List.of(
                Warning.duplicateInputIgnored("another duplicate")))));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");
        Warning warning = Warning.duplicateInputIgnored("duplicate input");
        CommandResult commandResultWithWarning = new CommandResult("feedback", List.of(warning));

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());
        assertEquals(commandResultWithWarning.hashCode(),
                new CommandResult("feedback", List.of(warning)).hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // different warnings -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), commandResultWithWarning.hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", warnings=" + commandResult.getWarnings()
                + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + "}";
        assertEquals(expected, commandResult.toString());
    }

    @Test
    public void constructor_withWarnings_formatsFeedback() {
        Warning warning = Warning.duplicateInputIgnored("duplicate input detected");
        CommandResult commandResult = new CommandResult("completed", List.of(warning));

        String expectedFeedback = "completed"
                + System.lineSeparator() + System.lineSeparator() + "Warnings:"
                + System.lineSeparator() + "1. " + warning.formatForDisplay();
        assertEquals(expectedFeedback, commandResult.getFeedbackToUser());
        assertEquals(List.of(warning), commandResult.getWarnings());
    }

    @Test
    public void getWarnings_unmodifiable() {
        Warning warning = Warning.duplicateInputIgnored("duplicate");
        CommandResult commandResult = new CommandResult("done", List.of(warning));

        assertThrows(UnsupportedOperationException.class, () -> commandResult.getWarnings().add(warning));
    }

    @Test
    public void constructor_withEmptyWarnings_preservesFeedback() {
        CommandResult commandResult = new CommandResult("feedback", List.of());
        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertTrue(commandResult.getWarnings().isEmpty());
    }
}
