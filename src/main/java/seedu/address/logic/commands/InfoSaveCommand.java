package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Info;
import seedu.address.model.person.Person;

/**
 * Saves the edited information for a person.
 * This command is created directly by the UI, not through command parsing.
 */
public class InfoSaveCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Saved info for Person: %1$s";

    private final Index index;
    private final Info info;

    /**
     * Constructor for direct injection from UI
     * @param index Index of contact
     * @param info The information to save
     */
    public InfoSaveCommand(Index index, Info info) {
        this.index = index;
        this.info = info;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getAddress(),
                personToEdit.getTags(),
                info);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof InfoSaveCommand)) {
            return false;
        }

        InfoSaveCommand otherCommand = (InfoSaveCommand) other;
        return index.equals(otherCommand.index)
                && info.equals(otherCommand.info);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(index, info);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("info", info)
                .toString();
    }
}
