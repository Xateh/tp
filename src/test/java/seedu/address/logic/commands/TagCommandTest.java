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
        Set<Tag> tagsToRemove = Set.of(new Tag("friends"));

        // Verify person has `friends` tag before removal
        Person beforePerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(beforePerson.getTags().contains(new Tag("friends")));

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToRemove);
        CommandResult result = tagCommand.execute(model);

        // Verify the person was updated with new tags
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getTags().contains(new Tag("newTag")));
        assertTrue(editedPerson.getTags().contains(new Tag("anotherTag")));
        assertFalse(editedPerson.getTags().contains(new Tag("friends")));

        // Verify success message
        assertTrue(result.getFeedbackToUser().contains("newTag"));
        assertTrue(result.getFeedbackToUser().contains("anotherTag"));
        assertTrue(result.getFeedbackToUser().contains("friends"));
    }

    @Test
    public void execute_addSingleTag_success() throws CommandException {
        Set<Tag> tagsToAdd = Set.of(new Tag("singleTag"));
        Set<Tag> tagsToRemove = Set.of();

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToRemove);
        CommandResult result = tagCommand.execute(model);

        // Verify the person was updated
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(editedPerson.getTags().contains(new Tag("singleTag")));

        // Verify success message contains the tag
        assertTrue(result.getFeedbackToUser().contains("singleTag"));
    }

    @Test
    public void execute_removeSingleTag_success() throws CommandException {
        Set<Tag> tagsToAdd = Set.of();
        Set<Tag> tagsToRemove = Set.of(new Tag("friends"));

        // Verify person has `friends` tag before removal
        Person beforePerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(beforePerson.getTags().contains(new Tag("friends")));

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToRemove);
        CommandResult result = tagCommand.execute(model);

        // Verify the person was updated
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertFalse(editedPerson.getTags().contains(new Tag("friends")));

        // Verify success message contains the tag
        assertTrue(result.getFeedbackToUser().contains("friends"));
    }

    @Test
    public void execute_addToExistingTags_success() throws CommandException {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Set<Tag> originalTags = new HashSet<>(personToEdit.getTags());
        Set<Tag> tagsToAdd = Set.of(new Tag("additionalTag"));
        Set<Tag> tagsToRemove = Set.of();

        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToRemove);
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
        Set<Tag> tagsToRemove = Set.of(new Tag("untag"));
        TagCommand tagCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAdd, tagsToRemove);

        assertThrows(NullPointerException.class, () -> tagCommand.execute(null));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Set<Tag> tagsToAdd = Set.of(new Tag("tag"));
        Set<Tag> tagsToRemove = Set.of(new Tag("untag"));
        TagCommand tagCommand = new TagCommand(outOfBoundIndex, tagsToAdd, tagsToRemove);

        CommandException exception = assertThrows(CommandException.class, () -> tagCommand.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void equals() {
        Set<Tag> tagsToAddFirst = Set.of(new Tag("firstadd"));
        Set<Tag> tagsToRemoveFirst = Set.of(new Tag("firstremove"));
        Set<Tag> tagsToAddSecond = Set.of(new Tag("secondadd"));
        Set<Tag> tagsToRemoveSecond = Set.of(new Tag("secondremove"));

        TagCommand tagFirstCommand = new TagCommand(INDEX_FIRST_PERSON, tagsToAddFirst, tagsToRemoveFirst);
        TagCommand tagSecondCommand = new TagCommand(INDEX_SECOND_PERSON, tagsToAddSecond, tagsToRemoveSecond);

        // same object -> returns true
        assertTrue(tagFirstCommand.equals(tagFirstCommand));

        // same values -> returns true
        TagCommand tagFirstCommandCopy = new TagCommand(INDEX_FIRST_PERSON, tagsToAddFirst, tagsToRemoveFirst);
        assertTrue(tagFirstCommand.equals(tagFirstCommandCopy));

        // different types -> returns false
        assertFalse(tagFirstCommand.equals(1));

        // null -> returns false
        assertNotEquals(null, tagFirstCommand);

        // different person index -> returns false
        assertFalse(tagFirstCommand.equals(tagSecondCommand));

        // different tags -> returns false
        TagCommand tagFirstDifferentAddTags = new TagCommand(INDEX_FIRST_PERSON, tagsToAddSecond, tagsToRemoveFirst);
        assertFalse(tagFirstCommand.equals(tagFirstDifferentAddTags));
        TagCommand tagFirstDifferentRemoveTags = new TagCommand(INDEX_FIRST_PERSON, tagsToAddFirst, tagsToRemoveSecond);
        assertFalse(tagFirstCommand.equals(tagFirstDifferentRemoveTags));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Set<Tag> addTags = Set.of(new Tag("tag1"));
        Set<Tag> subTags = Set.of(new Tag("tag2"));
        TagCommand tagCommand = new TagCommand(targetIndex, addTags, subTags);
        String expected = TagCommand.class.getCanonicalName() + "{index=" + targetIndex + ", addTags=" + addTags
                + ", subTags=" + subTags + "}";
        assertEquals(expected, tagCommand.toString());
    }

    @Test
    public void equals_sameTagsDifferentOrder_returnsTrue() {
        Set<Tag> addTags1 = Set.of(new Tag("friend"), new Tag("colleague"));
        Set<Tag> subTags1 = Set.of(new Tag("enemy"), new Tag("villain"));
        Set<Tag> addTags2 = Set.of(new Tag("colleague"), new Tag("friend"));
        Set<Tag> subTags2 = Set.of(new Tag("villain"), new Tag("enemy"));

        TagCommand command1 = new TagCommand(INDEX_FIRST_PERSON, addTags1, subTags1);
        TagCommand command2 = new TagCommand(INDEX_FIRST_PERSON, addTags2, subTags2);

        // Sets with same elements in different order should be equal
        assertTrue(command1.equals(command2));
    }

    @Test
    public void hashCode_sameObjects_returnsSameHashCode() {
        Set<Tag> addTags = Set.of(new Tag("friend"));
        Set<Tag> subTags = Set.of(new Tag("enemy"));
        TagCommand command1 = new TagCommand(INDEX_FIRST_PERSON, addTags, subTags);
        TagCommand command2 = new TagCommand(INDEX_FIRST_PERSON, addTags, subTags);

        assertEquals(command1.hashCode(), command2.hashCode());
    }
}
