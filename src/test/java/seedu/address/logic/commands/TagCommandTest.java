package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Contains unit tests for TagCommand.
 */
public class TagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexAndTags_success() throws CommandException {
        Set<Tag> tagsToAdd = Set.of(new Tag("newTag"), new Tag("anotherTag"));

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd);
        CommandResult result = tagCommand.execute(model);

        // Verify the person was updated with new tags
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getTags().contains(new Tag("newTag")));
        assertTrue(editedPerson.getTags().contains(new Tag("anotherTag")));

        // Verify success message
        assertTrue(result.getFeedbackToUser().contains("Added tag to person"));
        assertTrue(result.getFeedbackToUser().contains("newTag"));
        assertTrue(result.getFeedbackToUser().contains("anotherTag"));
    }

    @Test
    public void execute_singleTag_success() throws CommandException {
        Set<Tag> tagsToAdd = Set.of(new Tag("singleTag"));

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd);
        CommandResult result = tagCommand.execute(model);

        // Verify the person was updated
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getTags().contains(new Tag("singleTag")));

        // Verify success message contains the tag
        assertTrue(result.getFeedbackToUser().contains("singleTag"));
    }

    @Test
    public void execute_addToExistingTags_success() throws CommandException {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Set<Tag> originalTags = new HashSet<>(personToEdit.getTags());
        Set<Tag> tagsToAdd = Set.of(new Tag("additionalTag"));

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd);
        tagCommand.execute(model);

        // Verify original tags are preserved and new tag is added
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getTags().containsAll(originalTags));
        assertTrue(editedPerson.getTags().contains(new Tag("additionalTag")));
        assertEquals(originalTags.size() + 1, editedPerson.getTags().size());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        Set<Tag> tagsToAdd = Set.of(new Tag("tag"));
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd);

        assertThrows(NullPointerException.class, () -> tagCommand.execute(null));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Set<Tag> tagsToAdd = Set.of(new Tag("tag"));
        TagCommand tagCommand = new TagCommand(outOfBoundIndex, tagsToAdd);

        CommandException exception = assertThrows(CommandException.class, () -> tagCommand.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void equals() {
        Set<Tag> tagsFirst = Set.of(new Tag("first"));
        Set<Tag> tagsSecond = Set.of(new Tag("second"));

        TagCommand tagFirstCommand = new TagCommand(INDEX_FIRST_PERSON, tagsFirst);
        TagCommand tagSecondCommand = new TagCommand(INDEX_SECOND_PERSON, tagsSecond);

        // same object -> returns true
        assertTrue(tagFirstCommand.equals(tagFirstCommand));

        // same values -> returns true
        TagCommand tagFirstCommandCopy = new TagCommand(INDEX_FIRST_PERSON, tagsFirst);
        assertTrue(tagFirstCommand.equals(tagFirstCommandCopy));

        // different types -> returns false
        assertFalse(tagFirstCommand.equals(1));

        // null -> returns false
        assertNotEquals(null, tagFirstCommand);

        // different person index -> returns false
        assertFalse(tagFirstCommand.equals(tagSecondCommand));

        // different tags -> returns false
        TagCommand tagFirstDifferentTags = new TagCommand(INDEX_FIRST_PERSON, tagsSecond);
        assertFalse(tagFirstCommand.equals(tagFirstDifferentTags));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Set<Tag> tags = Set.of(new Tag("tag1"));
        TagCommand tagCommand = new TagCommand(targetIndex, tags);
        String expected = TagCommand.class.getCanonicalName() + "{index=" + targetIndex + ", tagsToAdd=" + tags + "}";
        assertEquals(expected, tagCommand.toString());
    }

    @Test
    public void constructor_validInputs_success() {
        Set<Tag> tags = Set.of(new Tag("friend"), new Tag("colleague"));
        TagCommand command = new TagCommand(INDEX_FIRST_PERSON, tags);

        // Command should be created successfully without throwing exceptions
        assertTrue(command instanceof TagCommand);
    }

    @Test
    public void equals_sameTagsDifferentOrder_returnsTrue() {
        Set<Tag> tags1 = Set.of(new Tag("friend"), new Tag("colleague"));
        Set<Tag> tags2 = Set.of(new Tag("colleague"), new Tag("friend"));

        TagCommand command1 = new TagCommand(INDEX_FIRST_PERSON, tags1);
        TagCommand command2 = new TagCommand(INDEX_FIRST_PERSON, tags2);

        // Sets with same elements in different order should be equal
        assertTrue(command1.equals(command2));
    }

    @Test
    public void hashCode_sameObjects_returnsSameHashCode() {
        Set<Tag> tags = Set.of(new Tag("friend"));
        TagCommand command1 = new TagCommand(INDEX_FIRST_PERSON, tags);
        TagCommand command2 = new TagCommand(INDEX_FIRST_PERSON, tags);

        assertEquals(command1.hashCode(), command2.hashCode());
    }
}
