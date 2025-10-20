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
import seedu.address.model.person.Person;

public class InfoEditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoEditCommand infoEditCommand = new InfoEditCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoEditCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(personToEdit));

        CommandResult expectedResult = new CommandResult(expectedMessage, personToEdit);

        assertCommandSuccess(infoEditCommand, model, expectedResult, model);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        InfoEditCommand infoEditCommand = new InfoEditCommand(outOfBoundIndex);

        assertCommandFailure(infoEditCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoEditCommand infoEditCommand = new InfoEditCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(InfoEditCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(personToEdit));

        CommandResult expectedResult = new CommandResult(expectedMessage, personToEdit);

        assertCommandSuccess(infoEditCommand, model, expectedResult, model);
    }

    @Test
    public void equals() {
        InfoEditCommand infoEditFirstCommand = new InfoEditCommand(INDEX_FIRST_PERSON);
        InfoEditCommand infoEditSecondCommand = new InfoEditCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(infoEditFirstCommand.equals(infoEditFirstCommand));

        // same values -> returns true
        InfoEditCommand infoEditFirstCommandCopy = new InfoEditCommand(INDEX_FIRST_PERSON);
        assertTrue(infoEditFirstCommand.equals(infoEditFirstCommandCopy));

        // different types -> returns false
        assertFalse(infoEditFirstCommand.equals(1));

        // null -> returns false
        assertFalse(infoEditFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(infoEditFirstCommand.equals(infoEditSecondCommand));
    }
}
