package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Info;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.ui.UiManager;

public class InfoCommandTest {

    private Model model;
    private TestUiManager testUiManager;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        testUiManager = new TestUiManager();
        InfoCommand.setUiManager(testUiManager);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new InfoCommand(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoCommand infoCommand = new InfoCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(personToEdit));

        assertCommandSuccess(infoCommand, model, expectedMessage, model);

        assertTrue(testUiManager.showInfoEditorCalled.get());
        assertEquals(personToEdit, testUiManager.lastPersonEdited.get());
        assertEquals(INDEX_FIRST_PERSON.getZeroBased(), testUiManager.lastPersonIndex.get());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        InfoCommand infoCommand = new InfoCommand(outOfBoundIndex);

        assertCommandFailure(infoCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        // Get the person before filtering
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Filter the list to show only the first person
        model.updateFilteredPersonList(person -> person.equals(personToEdit));

        InfoCommand infoCommand = new InfoCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(personToEdit));

        assertCommandSuccess(infoCommand, model, expectedMessage, model);

        assertTrue(testUiManager.showInfoEditorCalled.get());
        assertEquals(personToEdit, testUiManager.lastPersonEdited.get());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        // Get the first person before filtering
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Filter the list to show only the first person
        model.updateFilteredPersonList(person -> person.equals(firstPerson));

        // Now INDEX_SECOND_PERSON should be out of bounds since filtered list only has 1 person
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        InfoCommand infoCommand = new InfoCommand(outOfBoundIndex);

        assertCommandFailure(infoCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullUiManager_throwsCommandException() {
        InfoCommand.setUiManager(null);
        InfoCommand infoCommand = new InfoCommand(INDEX_FIRST_PERSON);

        CommandException exception = assertThrows(CommandException.class, () -> infoCommand.execute(model));
        assertEquals("UI Manager not initialized. Cannot open info editor.", exception.getMessage());

        // Restore for other tests
        InfoCommand.setUiManager(testUiManager);
    }

    @Test
    public void saveInfo_validIndexAndInfo_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info newInfo = new Info("Updated information content");

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                personToEdit.getCustomFields(),
                newInfo);
        expectedModel.setPerson(personToEdit, editedPerson);

        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, newInfo);

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());

        // Verify the person was updated in the model
        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(newInfo, updatedPerson.getInfo());
        assertEquals(expectedModel, model);
    }

    @Test
    public void saveInfo_invalidIndex_throwsCommandException() {
        Info info = new Info("Test info");
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertThrows(CommandException.class, () -> InfoCommand.saveInfo(model, invalidIndex, info));
    }

    @Test
    public void saveInfo_emptyInfo_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info emptyInfo = new Info("");

        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, emptyInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                personToEdit.getCustomFields(),
                emptyInfo);

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(emptyInfo, updatedPerson.getInfo());
    }

    @Test
    public void saveInfo_multilineInfo_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info multilineInfo = new Info("Line 1\nLine 2\nLine 3");

        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, multilineInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                personToEdit.getCustomFields(),
                multilineInfo);

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void saveInfo_specialCharacters_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info specialInfo = new Info("Special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./ \n\t");

        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, specialInfo);

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS,
                Messages.format(new Person(
                        personToEdit.getName(),
                        personToEdit.getPhone(),
                        personToEdit.getEmail(),
                        personToEdit.getAddress(),
                        personToEdit.getTags(),
                        personToEdit.getCustomFields(),
                        specialInfo)));
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void getIndex_returnsCorrectIndex() {
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);
        assertEquals(INDEX_FIRST_PERSON, command.getIndex());
    }

    @Test
    public void getInfo_editCommand_returnsNull() {
        InfoCommand editCommand = new InfoCommand(INDEX_FIRST_PERSON);
        assertNull(editCommand.getInfo());
    }

    @Test
    public void setUiManager_validUiManager_success() {
        UiManager newUiManager = new TestUiManager();
        InfoCommand.setUiManager(newUiManager);

        // Test that the UI manager was set by trying to execute a command
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);
        try {
            command.execute(model);
            // If no exception is thrown, the UI manager was set correctly
            assertTrue(true);
        } catch (CommandException e) {
            // Should not reach here if UI manager is set correctly
            assertTrue(false, "UI Manager was not set correctly");
        }
    }

    @Test
    public void setUiManager_nullUiManager_allowsNull() {
        InfoCommand.setUiManager(null);
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);

        assertThrows(CommandException.class, () -> command.execute(model));

        // Restore for other tests
        InfoCommand.setUiManager(testUiManager);
    }

    @Test
    public void equals() {
        InfoCommand infoFirstCommand = new InfoCommand(INDEX_FIRST_PERSON);
        InfoCommand infoSecondCommand = new InfoCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(infoFirstCommand.equals(infoFirstCommand));

        // same values -> returns true
        InfoCommand infoFirstCommandCopy = new InfoCommand(INDEX_FIRST_PERSON);
        assertTrue(infoFirstCommand.equals(infoFirstCommandCopy));

        // different types -> returns false
        assertFalse(infoFirstCommand.equals(1));

        // null -> returns false
        assertFalse(infoFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(infoFirstCommand.equals(infoSecondCommand));
    }

    @Test
    public void hashCode_sameValues_sameHashCode() {
        InfoCommand command1 = new InfoCommand(INDEX_FIRST_PERSON);
        InfoCommand command2 = new InfoCommand(INDEX_FIRST_PERSON);

        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_differentValues_differentHashCode() {
        InfoCommand command1 = new InfoCommand(INDEX_FIRST_PERSON);
        InfoCommand command2 = new InfoCommand(INDEX_SECOND_PERSON);

        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void hashCode_consistent_multipleCallsSameResult() {
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);

        int firstHashCode = command.hashCode();
        int secondHashCode = command.hashCode();
        int thirdHashCode = command.hashCode();

        assertEquals(firstHashCode, secondHashCode);
        assertEquals(secondHashCode, thirdHashCode);
    }

    @Test
    public void toString_validCommand_correctFormat() {
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);
        String result = command.toString();

        assertTrue(result.contains("InfoCommand"));
        assertTrue(result.contains("targetIndex"));
        assertTrue(result.contains(INDEX_FIRST_PERSON.toString()));
        assertTrue(result.contains("updatedInfo"));
    }

    @Test
    public void toString_differentCommands_differentStrings() {
        InfoCommand command1 = new InfoCommand(INDEX_FIRST_PERSON);
        InfoCommand command2 = new InfoCommand(INDEX_SECOND_PERSON);

        assertNotEquals(command1.toString(), command2.toString());
    }

    @Test
    public void toString_consistent_multipleCallsSameResult() {
        InfoCommand command = new InfoCommand(INDEX_FIRST_PERSON);

        String firstToString = command.toString();
        String secondToString = command.toString();
        String thirdToString = command.toString();

        assertEquals(firstToString, secondToString);
        assertEquals(secondToString, thirdToString);
    }

    @Test
    public void execute_largeValidIndex_success() throws Exception {
        // Test with the last person in the list
        int lastIndex = model.getFilteredPersonList().size();
        Index lastValidIndex = Index.fromOneBased(lastIndex);
        Person lastPerson = model.getFilteredPersonList().get(lastValidIndex.getZeroBased());

        InfoCommand infoCommand = new InfoCommand(lastValidIndex);
        String expectedMessage = String.format(InfoCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(lastPerson));

        assertCommandSuccess(infoCommand, model, expectedMessage, model);

        assertTrue(testUiManager.showInfoEditorCalled.get());
        assertEquals(lastPerson, testUiManager.lastPersonEdited.get());
        assertEquals(lastValidIndex.getZeroBased(), testUiManager.lastPersonIndex.get());
    }

    @Test
    public void saveInfo_veryLongInfo_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is line ").append(i).append(" of a very long info field. ");
        }
        Info veryLongInfo = new Info(longText.toString());

        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, veryLongInfo);

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                personToEdit.getCustomFields(),
                veryLongInfo);

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS, Messages.format(editedPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void execute_modelWithDifferentPersons_success() throws Exception {
        Person newPerson = new PersonBuilder().withName("Test Person").build();
        model.addPerson(newPerson);

        Index newPersonIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        InfoCommand command = new InfoCommand(newPersonIndex);

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Test Person"));
        assertTrue(testUiManager.showInfoEditorCalled.get());
    }

    @Test
    public void saveInfo_replaceExistingInfo_success() throws Exception {
        // First save some info
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Info initialInfo = new Info("Initial info");
        InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, initialInfo);

        // Then replace it with new info
        Info newInfo = new Info("Replaced info");
        CommandResult result = InfoCommand.saveInfo(model, INDEX_FIRST_PERSON, newInfo);

        Person updatedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(newInfo, updatedPerson.getInfo());

        String expectedMessage = String.format(InfoCommand.MESSAGE_SUCCESS, Messages.format(updatedPerson));
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    /**
     * Test UiManager that tracks method calls
     */
    private static class TestUiManager extends UiManager {
        private AtomicBoolean showInfoEditorCalled = new AtomicBoolean(false);
        private AtomicReference<Person> lastPersonEdited = new AtomicReference<>();
        private AtomicReference<Integer> lastPersonIndex = new AtomicReference<>();

        public TestUiManager() {
            super(null);
        }

        @Override
        public void showInfoEditor(Person person, int personIndex) {
            showInfoEditorCalled.set(true);
            lastPersonEdited.set(person);
            lastPersonIndex.set(personIndex);
        }

        @Override
        public void start(javafx.stage.Stage primaryStage) {
            // Do nothing for tests
        }
    }
}
