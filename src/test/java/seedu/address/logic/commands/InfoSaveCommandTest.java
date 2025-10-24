package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Info;
import seedu.address.model.person.Person;

public class InfoSaveCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexAndInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info newInfo = new Info("New information content");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, newInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                newInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Info info = new Info("Test info");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(outOfBoundIndex, info);

        assertCommandFailure(infoSaveCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info emptyInfo = new Info("");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, emptyInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                emptyInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multilineInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info multilineInfo = new Info("Line 1\nLine 2\nLine 3");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, multilineInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                multilineInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Info info1 = new Info("Info 1");
        Info info2 = new Info("Info 2");
        InfoSaveCommand infoSaveFirstCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, info1);
        InfoSaveCommand infoSaveSecondCommand = new InfoSaveCommand(INDEX_SECOND_PERSON, info1);
        InfoSaveCommand infoSaveThirdCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, info2);

        // same object -> returns true
        assertTrue(infoSaveFirstCommand.equals(infoSaveFirstCommand));

        // same values -> returns true
        InfoSaveCommand infoSaveFirstCommandCopy = new InfoSaveCommand(INDEX_FIRST_PERSON, info1);
        assertTrue(infoSaveFirstCommand.equals(infoSaveFirstCommandCopy));

        // different types -> returns false
        assertFalse(infoSaveFirstCommand.equals(1));

        // null -> returns false
        assertFalse(infoSaveFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(infoSaveFirstCommand.equals(infoSaveSecondCommand));

        // different info -> returns false
        assertFalse(infoSaveFirstCommand.equals(infoSaveThirdCommand));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        Info info = new Info("Test info");
        InfoSaveCommand command1 = new InfoSaveCommand(INDEX_FIRST_PERSON, info);
        InfoSaveCommand command2 = new InfoSaveCommand(INDEX_FIRST_PERSON, info);

        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_differentIndex_differentHashCode() {
        Info info = new Info("Test info");
        InfoSaveCommand command1 = new InfoSaveCommand(INDEX_FIRST_PERSON, info);
        InfoSaveCommand command2 = new InfoSaveCommand(INDEX_SECOND_PERSON, info);

        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_differentInfo_differentHashCode() {
        Info info1 = new Info("Info 1");
        Info info2 = new Info("Info 2");
        InfoSaveCommand command1 = new InfoSaveCommand(INDEX_FIRST_PERSON, info1);
        InfoSaveCommand command2 = new InfoSaveCommand(INDEX_FIRST_PERSON, info2);

        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_bothDifferent_differentHashCode() {
        Info info1 = new Info("Info 1");
        Info info2 = new Info("Info 2");
        InfoSaveCommand command1 = new InfoSaveCommand(INDEX_FIRST_PERSON, info1);
        InfoSaveCommand command2 = new InfoSaveCommand(INDEX_SECOND_PERSON, info2);

        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void toString_validCommand_correctString() {
        Info info = new Info("Test info");
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, info);
        String result = command.toString();

        assertTrue(result.contains("InfoSaveCommand"));
        assertTrue(result.contains(INDEX_FIRST_PERSON.toString()));
        assertTrue(result.contains(info.toString()));
    }

    @Test
    public void toString_differentCommands_differentStrings() {
        Info info1 = new Info("Info 1");
        Info info2 = new Info("Info 2");
        InfoSaveCommand command1 = new InfoSaveCommand(INDEX_FIRST_PERSON, info1);
        InfoSaveCommand command2 = new InfoSaveCommand(INDEX_SECOND_PERSON, info2);

        assertNotEquals(command1.toString(), command2.toString());
    }

    @Test
    public void toString_emptyInfo_handlesCorrectly() {
        Info emptyInfo = new Info("");
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, emptyInfo);
        String result = command.toString();

        assertTrue(result.contains("InfoSaveCommand"));
        assertTrue(result.contains(INDEX_FIRST_PERSON.toString()));
        // Should still contain the Info object's toString even if content is empty
        assertTrue(result.contains(emptyInfo.toString()));
    }

    @Test
    public void toString_multilineInfo_handlesCorrectly() {
        Info multilineInfo = new Info("Line 1\nLine 2\nLine 3");
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, multilineInfo);
        String result = command.toString();

        assertTrue(result.contains("InfoSaveCommand"));
        assertTrue(result.contains(INDEX_FIRST_PERSON.toString()));
        assertTrue(result.contains(multilineInfo.toString()));
    }

    @Test
    public void toString_longInfo_handlesCorrectly() {
        String longText = "This is a very long piece of information that spans multiple lines and contains "
                + "various details about the person. It includes personal details, work history, "
                + "educational background, and other relevant information that might be quite lengthy.";
        Info longInfo = new Info(longText);
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, longInfo);
        String result = command.toString();

        assertTrue(result.contains("InfoSaveCommand"));
        assertTrue(result.contains(INDEX_FIRST_PERSON.toString()));
        assertTrue(result.contains(longInfo.toString()));
    }

    @Test
    public void execute_largeIndex_success() {
        // Test with a large but valid index
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info newInfo = new Info("Large index test");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, newInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                newInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_specialCharactersInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info specialInfo = new Info("Special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./ \n\t");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, specialInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                specialInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unicodeInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info unicodeInfo = new Info("Unicode: 你好 こんにちは 🌟 ñoël café");
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, unicodeInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                unicodeInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_veryLongInfo_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is line ").append(i).append(" of a very long info field. ");
        }
        Info veryLongInfo = new Info(longText.toString());
        InfoSaveCommand infoSaveCommand = new InfoSaveCommand(INDEX_FIRST_PERSON, veryLongInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                veryLongInfo);

        String expectedMessage = String.format(InfoSaveCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(infoSaveCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void hashCode_consistent_multipleCallsSameResult() {
        Info info = new Info("Consistency test");
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, info);

        int firstHashCode = command.hashCode();
        int secondHashCode = command.hashCode();
        int thirdHashCode = command.hashCode();

        assertEquals(firstHashCode, secondHashCode);
        assertEquals(secondHashCode, thirdHashCode);
    }

    @Test
    public void toString_consistent_multipleCallsSameResult() {
        Info info = new Info("Consistency test");
        InfoSaveCommand command = new InfoSaveCommand(INDEX_FIRST_PERSON, info);

        String firstToString = command.toString();
        String secondToString = command.toString();
        String thirdToString = command.toString();

        assertEquals(firstToString, secondToString);
        assertEquals(secondToString, thirdToString);
    }
}
