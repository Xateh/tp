package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code UntagCommand}.
 */
public class UntagCommandTest {

    private static final Tag TAG_FRIEND = new Tag("friends");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_removeExistingTag_success() {
        Person personWithTag = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
    UntagCommand untagCommand = new UntagCommand(INDEX_SECOND_PERSON, TAG_FRIEND);

        Person expectedPerson = new PersonBuilder(personWithTag).withTags("owesMoney").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personWithTag, expectedPerson);
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

        String expectedMessage = String.format(UntagCommand.MESSAGE_REMOVE_TAG_SUCCESS,
                TAG_FRIEND, Messages.format(expectedPerson));

        assertCommandSuccess(untagCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_tagNotPresent_throwsCommandException() {
        Person personWithoutTag = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Tag unusedTag = new Tag("colleague");
        UntagCommand untagCommand = new UntagCommand(INDEX_FIRST_PERSON, unusedTag);

        String expectedMessage = String.format(UntagCommand.MESSAGE_TAG_NOT_FOUND,
                Messages.format(personWithoutTag), unusedTag);

        assertCommandFailure(untagCommand, model, expectedMessage);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UntagCommand untagCommand = new UntagCommand(outOfBoundsIndex, TAG_FRIEND);

        assertCommandFailure(untagCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UntagCommand firstCommand = new UntagCommand(INDEX_FIRST_PERSON, TAG_FRIEND);
        UntagCommand secondCommand = new UntagCommand(INDEX_SECOND_PERSON, TAG_FRIEND);

        // same object -> returns true
        org.junit.jupiter.api.Assertions.assertTrue(firstCommand.equals(firstCommand));

        // same values -> returns true
    UntagCommand firstCommandCopy = new UntagCommand(INDEX_FIRST_PERSON, TAG_FRIEND);
        org.junit.jupiter.api.Assertions.assertTrue(firstCommand.equals(firstCommandCopy));

        // different index -> returns false
        org.junit.jupiter.api.Assertions.assertFalse(firstCommand.equals(secondCommand));

        // null -> returns false
        org.junit.jupiter.api.Assertions.assertFalse(firstCommand.equals(null));

        // different type -> returns false
        org.junit.jupiter.api.Assertions.assertFalse(firstCommand.equals(new HelpCommand()));
    }
}
