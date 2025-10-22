package seedu.address.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class SessionCommandTest {

    private static final Instant TIMESTAMP_1 = Instant.parse("2024-01-01T10:00:00Z");
    private static final Instant TIMESTAMP_2 = Instant.parse("2024-01-01T11:00:00Z");
    private static final String COMMAND_TEXT_1 = "add n/John Doe";
    private static final String COMMAND_TEXT_2 = "list";

    @Test
    public void constructor_nullTimestamp_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionCommand(null, COMMAND_TEXT_1));
    }

    @Test
    public void constructor_nullCommandText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionCommand(TIMESTAMP_1, null));
    }

    @Test
    public void constructor_validInputs_success() {
        SessionCommand command = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertEquals(TIMESTAMP_1, command.getTimestamp());
        assertEquals(COMMAND_TEXT_1, command.getCommandText());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        SessionCommand command = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_null_returnsFalse() {
        SessionCommand command = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertFalse(command.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        SessionCommand command = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertFalse(command.equals("string"));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        SessionCommand command1 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        SessionCommand command2 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentTimestamp_returnsFalse() {
        SessionCommand command1 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        SessionCommand command2 = new SessionCommand(TIMESTAMP_2, COMMAND_TEXT_1);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentCommandText_returnsFalse() {
        SessionCommand command1 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        SessionCommand command2 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_2);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        SessionCommand command1 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        SessionCommand command2 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_differentValues_returnsDifferentHashCode() {
        SessionCommand command1 = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        SessionCommand command2 = new SessionCommand(TIMESTAMP_2, COMMAND_TEXT_2);
        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void toString_validCommand_containsAllFields() {
        SessionCommand command = new SessionCommand(TIMESTAMP_1, COMMAND_TEXT_1);
        String result = command.toString();
        assertTrue(result.contains("SessionCommand"));
        assertTrue(result.contains(TIMESTAMP_1.toString()));
        assertTrue(result.contains(COMMAND_TEXT_1));
    }
}

