package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // test with Person parameter
        Person person = new PersonBuilder().build();
        CommandResult commandResultWithPerson = new CommandResult("feedback", person);

        // same person -> returns true
        assertTrue(commandResultWithPerson.equals(new CommandResult("feedback", person)));

        // different person -> returns false
        Person differentPerson = new PersonBuilder().withName("Different Name").build();
        assertFalse(commandResultWithPerson.equals(new CommandResult("feedback", differentPerson)));

        // with person vs without person -> returns false
        assertFalse(commandResult.equals(commandResultWithPerson));
        assertFalse(commandResultWithPerson.equals(commandResult));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // test with Person parameter
        Person person = new PersonBuilder().build();
        CommandResult commandResultWithPerson = new CommandResult("feedback", person);

        // same person -> returns same hashcode
        assertEquals(commandResultWithPerson.hashCode(), new CommandResult("feedback", person).hashCode());

        // different person -> returns different hashcode
        Person differentPerson = new PersonBuilder().withName("Different Name").build();
        assertNotEquals(commandResultWithPerson.hashCode(), new CommandResult("feedback", differentPerson).hashCode());

        // with person vs without person -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), commandResultWithPerson.hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", showInfoEditor=" + commandResult.isShowInfoEditor()
                + ", personToEdit=null}";
        assertEquals(expected, commandResult.toString());
    }

    @Test
    public void constructor_basicConstructor_setsCorrectValues() {
        CommandResult commandResult = new CommandResult("feedback");
        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
        assertFalse(commandResult.isShowInfoEditor());
        assertEquals(Optional.empty(), commandResult.getPersonToEdit());
    }

    @Test
    public void constructor_withFlags_setsCorrectValues() {
        CommandResult commandResult = new CommandResult("feedback", true, true);
        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertTrue(commandResult.isShowHelp());
        assertTrue(commandResult.isExit());
        assertFalse(commandResult.isShowInfoEditor());
        assertEquals(Optional.empty(), commandResult.getPersonToEdit());
    }

    @Test
    public void constructor_withPerson_setsCorrectValues() {
        Person person = new PersonBuilder().build();
        CommandResult commandResult = new CommandResult("feedback", person);
        assertEquals("feedback", commandResult.getFeedbackToUser());
        assertFalse(commandResult.isShowHelp());
        assertFalse(commandResult.isExit());
        assertTrue(commandResult.isShowInfoEditor());
        assertEquals(Optional.of(person), commandResult.getPersonToEdit());
    }

    @Test
    public void isShowInfoEditor_withoutPerson_returnsFalse() {
        CommandResult commandResult = new CommandResult("feedback");
        assertFalse(commandResult.isShowInfoEditor());

        CommandResult commandResultWithFlags = new CommandResult("feedback", true, false);
        assertFalse(commandResultWithFlags.isShowInfoEditor());
    }

    @Test
    public void isShowInfoEditor_withPerson_returnsTrue() {
        Person person = new PersonBuilder().build();
        CommandResult commandResult = new CommandResult("feedback", person);
        assertTrue(commandResult.isShowInfoEditor());
    }

    @Test
    public void getPersonToEdit_withoutPerson_returnsEmpty() {
        CommandResult commandResult = new CommandResult("feedback");
        assertEquals(Optional.empty(), commandResult.getPersonToEdit());

        CommandResult commandResultWithFlags = new CommandResult("feedback", true, false);
        assertEquals(Optional.empty(), commandResultWithFlags.getPersonToEdit());
    }

    @Test
    public void getPersonToEdit_withPerson_returnsPerson() {
        Person person = new PersonBuilder().build();
        CommandResult commandResult = new CommandResult("feedback", person);
        assertEquals(Optional.of(person), commandResult.getPersonToEdit());
    }

    @Test
    public void equals_withPersonValues() {
        Person person1 = new PersonBuilder().withName("Alice").build();
        Person person2 = new PersonBuilder().withName("Bob").build();

        CommandResult result1 = new CommandResult("feedback", person1);
        CommandResult result2 = new CommandResult("feedback", person1);
        CommandResult result3 = new CommandResult("feedback", person2);
        CommandResult result4 = new CommandResult("different feedback", person1);

        // same person and feedback -> returns true
        assertTrue(result1.equals(result2));

        // different person -> returns false
        assertFalse(result1.equals(result3));

        // different feedback -> returns false
        assertFalse(result1.equals(result4));
    }
}
