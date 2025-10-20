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
 */
public class InfoSaveCommand extends Command {

    public static final String COMMAND_WORD = "infosave";
    public static final String MESSAGE_SUCCESS = "Saved info for Person: %1$s";

    private final Index index;
    private final Info info;

    /**
     * Constructor for internal save command
     * @param index is Index of contact
     * @param info is the specified information associated to the contact
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

    public Index getIndex() {
        return index;
    }

    public Info getInfo() {
        return info;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
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
