package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.tag.Tag;

/**
 * Contains unit tests for TagCommand.
 */
public class TagCommandTest {

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
