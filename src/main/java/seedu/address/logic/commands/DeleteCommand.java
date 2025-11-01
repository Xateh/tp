package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";

    private final Index targetIndex;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());

        // Iterate through all persons in full list
        List<Person> currentList = model.getAddressBook().getPersonList();
        for (Person p : currentList) {
            if (p.isSamePerson(personToDelete)) {
                continue;
            }

            Set<Link> originalLinks = p.getLinks(); // Current links of person being checked
            Set<Link> cleanedLinks = originalLinks.stream() // keep all links which does not reference to deleted person
                    .filter(link -> !link.getLinker().isSamePerson(personToDelete)
                            && !link.getLinkee().isSamePerson(personToDelete))
                    .collect(java.util.stream.Collectors.toCollection(HashSet::new));

            // Some links were removed, need update ui list and person
            if (cleanedLinks.size() != originalLinks.size()) {
                // Rebuild person with updated links
                Person updated = new PersonBuilder(p).withLinks(cleanedLinks).build();
                model.setPerson(p, updated);
            }
        }
        model.deletePerson(personToDelete);

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
