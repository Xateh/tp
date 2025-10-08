package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Removes a tag from an existing person in the address book.
 */
public class UntagCommand extends Command {

    public static final String COMMAND_WORD = "untag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a tag from the person identified "
            + "by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) t/TAG\n"
            + "Example: " + COMMAND_WORD + " 1 t/friends";

    public static final String MESSAGE_REMOVE_TAG_SUCCESS = "Removed tag %1$s from %2$s";
    public static final String MESSAGE_TAG_NOT_FOUND = "%1$s does not contain tag %2$s.";

    private final Index index;
    private final Tag tagToRemove;

    /**
     * @param index of the person in the filtered person list whose tag is to be removed
     * @param tagToRemove tag that will be removed from the person
     */
    public UntagCommand(Index index, Tag tagToRemove) {
        requireNonNull(index);
        requireNonNull(tagToRemove);
        this.index = index;
        this.tagToRemove = tagToRemove;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        if (!personToEdit.getTags().contains(tagToRemove)) {
            throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND,
                    Messages.format(personToEdit), tagToRemove));
        }

        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
        updatedTags.remove(tagToRemove);

        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), updatedTags);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_REMOVE_TAG_SUCCESS, tagToRemove,
                Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UntagCommand)) {
            return false;
        }

        UntagCommand otherCommand = (UntagCommand) other;
        return index.equals(otherCommand.index) && tagToRemove.equals(otherCommand.tagToRemove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, tagToRemove);
    }
}
