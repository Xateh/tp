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

public class InfoViewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoViewCommand infoViewCommand = new InfoViewCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoViewCommand.MESSAGE_INFO_VIEW_SUCCESS,
                Messages.format(personToView),
                personToView.getInfo().value.isEmpty() ? "No information available." : personToView.getInfo().value);

        CommandResult expectedResult = new CommandResult(expectedMessage);

        assertCommandSuccess(infoViewCommand, model, expectedResult, model);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        InfoViewCommand infoViewCommand = new InfoViewCommand(outOfBoundIndex);

        assertCommandFailure(infoViewCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_personWithInfo_showsInfo() {
        // Create a person with information
        Person personWithInfo = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person updatedPerson = new Person(
                personWithInfo.getName(),
                personWithInfo.getPhone(),
                personWithInfo.getEmail(),
                personWithInfo.getAddress(),
                personWithInfo.getTags(),
                new Info("Test information content"));

        model.setPerson(personWithInfo, updatedPerson);

        InfoViewCommand infoViewCommand = new InfoViewCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoViewCommand.MESSAGE_INFO_VIEW_SUCCESS,
                Messages.format(updatedPerson), "Test information content");

        CommandResult expectedResult = new CommandResult(expectedMessage);

        assertCommandSuccess(infoViewCommand, model, expectedResult, model);
    }

    @Test
    public void execute_personWithEmptyInfo_showsNoInformationMessage() {
        Person personWithEmptyInfo = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoViewCommand infoViewCommand = new InfoViewCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoViewCommand.MESSAGE_INFO_VIEW_SUCCESS,
                Messages.format(personWithEmptyInfo), "No information available.");

        CommandResult expectedResult = new CommandResult(expectedMessage);

        assertCommandSuccess(infoViewCommand, model, expectedResult, model);
    }

    @Test
    public void equals() {
        InfoViewCommand infoViewFirstCommand = new InfoViewCommand(INDEX_FIRST_PERSON);
        InfoViewCommand infoViewSecondCommand = new InfoViewCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(infoViewFirstCommand.equals(infoViewFirstCommand));

        // same values -> returns true
        InfoViewCommand infoViewFirstCommandCopy = new InfoViewCommand(INDEX_FIRST_PERSON);
        assertTrue(infoViewFirstCommand.equals(infoViewFirstCommandCopy));

        // different types -> returns false
        assertFalse(infoViewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(infoViewFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(infoViewFirstCommand.equals(infoViewSecondCommand));
    }
}
