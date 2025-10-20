package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
