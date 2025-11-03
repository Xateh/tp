package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void execute_cascade_removesLinkFromSameHolder() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person holder = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // holder has link to victim
        Person holderWithLinks = new PersonBuilder(holder)
                .withLinks(Set.of(new Link(holder, personToDelete, "mentor")
                ))
                .build();
        model.setPerson(holder, holderWithLinks);

        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));


        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        Person holderInExpected = expectedModel.getAddressBook().getPersonList().stream()
                .filter(p -> p.isSamePerson(holder))
                .findFirst().orElseThrow();
        Person victimInExpected = expectedModel.getAddressBook().getPersonList().stream()
                .filter(p -> p.isSamePerson(personToDelete))
                .findFirst().orElseThrow();

        // Clean the holder's links (remove link to victim), then delete victim
        Person expectedHolder = new PersonBuilder(holderInExpected)
                .withLinks(new HashSet<>())
                .build();
        expectedModel.setPerson(holderInExpected, expectedHolder);
        expectedModel.deletePerson(victimInExpected);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noReferences_noOtherChanges() {
        // Ensure nobody has links at all
        for (Person p : new ArrayList<>(model.getAddressBook().getPersonList())) {
            model.setPerson(p, new PersonBuilder(p).withLinks(new HashSet<>()).build());
        }

        Person victim = model.getFilteredPersonList().get(0);
        int idxVictim = 0;

        DeleteCommand cmd = new DeleteCommand(Index.fromZeroBased(idxVictim));
        String msg = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(victim));

        Model expected = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        for (Person p : new ArrayList<>(expected.getAddressBook().getPersonList())) {
            expected.setPerson(p, new PersonBuilder(p).withLinks(new HashSet<>()).build());
        }
        expected.deletePerson(expected.getFilteredPersonList().get(0));

        assertCommandSuccess(cmd, model, msg, expected);
    }


    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
