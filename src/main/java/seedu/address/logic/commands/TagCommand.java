package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds tag to existing person in address book.
 */
public class TagCommand extends Command {

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds tags to person identified "
            + "by the index number used in the displayed person list. "
            + "New tags will be added on top of existing tags.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[TAG NAME]...\n"
            + "Example: " + COMMAND_WORD + " 1 beast friend";

    public static final String MESSAGE_ADD_TAGS_SUCCESS = "Added tag to person: %1$s %2$s";
    public static final String MESSAGE_NO_TAGS_PROVIDED = "At least one tag must be provided.";
    public static final String MESSAGE_DUPLICATE_TAGS = "Some tags already exist for this person: %1$s";
    public static final String MESSAGE_NO_NEW_TAGS = "No new tags were added to person: %1$s";

    private final Index index;
    private final Set<Tag> tagsToAdd;
    private final java.util.List<Warning> initialWarnings;

    /**
     * @param index of the person in the filtered person list to add tags
     * @param tagsToAdd tags to add to the person
     */
    public TagCommand(Index index, Set<Tag> tagsToAdd) {
        this(index, tagsToAdd, java.util.List.of());
    }

    /**
     * Creates a TagCommand with optional initial warnings (e.g. from parsing).
     */
    public TagCommand(Index index, Set<Tag> tagsToAdd, java.util.List<Warning> initialWarnings) {
        requireNonNull(index);
        requireNonNull(tagsToAdd);
        requireNonNull(initialWarnings);
        this.index = index;
        this.tagsToAdd = new HashSet<>(tagsToAdd);
        this.initialWarnings = java.util.List.copyOf(initialWarnings);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());

        Set<Tag> duplicateTags = tagsToAdd.stream()
                .filter(tag -> personToEdit.getTags().contains(tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Tag> uniqueTags = tagsToAdd.stream()
                .filter(tag -> !personToEdit.getTags().contains(tag))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Person editedPerson = personToEdit;
        if (!uniqueTags.isEmpty()) {
            editedPerson = createPersonWithAddedTags(personToEdit, uniqueTags);
            model.setPerson(personToEdit, editedPerson);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }

        String feedbackMessage;
        if (uniqueTags.isEmpty()) {
            feedbackMessage = String.format(MESSAGE_NO_NEW_TAGS, Messages.format(editedPerson));
        } else {
            String tagsString = formatTags(uniqueTags);
            feedbackMessage = String.format(MESSAGE_ADD_TAGS_SUCCESS,
                    Messages.format(editedPerson), tagsString);
        }

        java.util.List<Warning> warnings = new java.util.ArrayList<>(initialWarnings == null
                ? java.util.List.of()
                : initialWarnings);
        if (!duplicateTags.isEmpty()) {
            warnings.add(Warning.duplicateInputIgnored(
                    String.format(MESSAGE_DUPLICATE_TAGS, formatTags(duplicateTags))));
        }

        if (warnings.isEmpty()) {
            return new CommandResult(feedbackMessage);
        }
        return new CommandResult(feedbackMessage, warnings);
    }

    /**
     * Creates and returns a Person with tags added.
     */
    private static Person createPersonWithAddedTags(Person personToEdit, Set<Tag> tagsToAdd) {
        assert personToEdit != null;

        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
        updatedTags.addAll(tagsToAdd);

        return new Person(personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(), updatedTags);
    }

    private static String formatTags(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagCommand)) {
            return false;
        }

        TagCommand otherTagCommand = (TagCommand) other;
        return index.equals(otherTagCommand.index)
                && tagsToAdd.equals(otherTagCommand.tagsToAdd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, tagsToAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("tagsToAdd", tagsToAdd)
                .toString();
    }
}
