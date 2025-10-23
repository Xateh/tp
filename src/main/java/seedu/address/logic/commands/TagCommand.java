package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;
import seedu.address.model.tag.Tag;

/**
 * Adds tag to existing person in address book.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds tags to person identified "
            + "by the index number used in the displayed person list. "
            + "New tags will be added on top of existing tags.\n"
            + "Parameters: index (must be a positive integer) "
            + "[TAG NAME]...\n"
            + "Example: " + COMMAND_WORD + " 1 beast friend";

    public static final String MESSAGE_CHANGE_TAGS_SUCCESS = "Tags changed on person %1$s; added %2$s, removed %3$s";

    private final Index index;
    private final Set<Tag> addTags;
    private final Set<Tag> subTags;

    /**
     * @param index   of the person in the filtered person list to add tags
     * @param addTags tags to add to the person
     */
    public TagCommand(Index index, Set<Tag> addTags, Set<Tag> subTags) {
        requireNonNull(index);
        requireNonNull(addTags);
        requireNonNull(subTags);

        this.index = index;
        this.addTags = new HashSet<>(addTags);
        this.subTags = new HashSet<>(subTags);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createPersonWithModifiedTags(personToEdit, addTags, subTags);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        String addTagsString = addTags.stream()
                .map(tag -> tag.tagName)
                .reduce((t1, t2) -> t1 + ", " + t2)
                .orElse("");
        String removeTagsString = subTags.stream()
                .map(tag -> tag.tagName)
                .reduce((t1, t2) -> t1 + ", " + t2)
                .orElse("");

        return new CommandResult(String.format(MESSAGE_CHANGE_TAGS_SUCCESS,
                Messages.format(editedPerson), addTagsString, removeTagsString));
    }

    /**
     * Creates and returns a Person with tags added.
     */
    private static Person createPersonWithModifiedTags(Person personToEdit, Set<Tag> tagsToAdd, Set<Tag> tagsToRemove) {
        assert personToEdit != null;

        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
        updatedTags.addAll(tagsToAdd);
        updatedTags.removeAll(tagsToRemove);

        return new PersonBuilder(personToEdit).withTags(updatedTags).build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand otherTagCommand)) {
            return false;
        }

        return index.equals(otherTagCommand.index)
                && addTags.equals(otherTagCommand.addTags)
                && subTags.equals(otherTagCommand.subTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, addTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("addTags", addTags)
                .add("subTags", subTags)
                .toString();
    }
}
