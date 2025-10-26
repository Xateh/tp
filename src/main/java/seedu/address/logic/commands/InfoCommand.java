package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Info;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;
import seedu.address.ui.UiManager;

/**
 * Edits the info of an existing person in the address book.
 */
public class InfoCommand extends Command {

    public static final String COMMAND_WORD = "info";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the info of the person identified "
            + "by the index number used in the displayed person list. "
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_INFO_EDIT_SUCCESS = "Opened info editor for Person: %1$s";
    public static final String MESSAGE_SUCCESS = "Updated info for Person: %1$s";

    private static UiManager uiManager;

    private final Index targetIndex;
    private final Info updatedInfo; // null for edit, non-null for save

    /**
     * Creates an InfoEditCommand to edit the info of the person at the specified {@code Index}
     */
    public InfoCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.updatedInfo = null;
    }

    /**
     * Creates an InfoEditCommand to save the info of the person at the specified {@code Index}
     * This constructor is used internally when saving info changes.
     */
    private InfoCommand(Index targetIndex, Info updatedInfo) {
        requireNonNull(targetIndex);
        requireNonNull(updatedInfo);
        this.targetIndex = targetIndex;
        this.updatedInfo = updatedInfo;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        // If updatedInfo is null, we're opening the editor
        if (updatedInfo == null) {
            if (uiManager == null) {
                throw new CommandException("UI Manager not initialized. Cannot open info editor.");
            }
            uiManager.showInfoEditor(personToEdit, targetIndex.getZeroBased());
            return new CommandResult(String.format(MESSAGE_INFO_EDIT_SUCCESS, Messages.format(personToEdit)));
        } else {
            Person editedPerson = new PersonBuilder(personToEdit).withInfo(updatedInfo).build();

            model.setPerson(personToEdit, editedPerson);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);

            return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(editedPerson)));
        }
    }

    /**
     * Sets the UiManager for this command.
     * This is called during application initialization.
     */
    public static void setUiManager(UiManager uiManager) {
        InfoCommand.uiManager = uiManager;
    }

    /**
     * Saves info for the person at the specified index.
     * This method is called directly from the UI when saving changes.
     */
    public static CommandResult saveInfo(Model model, Index index, Info info) throws CommandException {
        InfoCommand saveCommand = new InfoCommand(index, info);
        return saveCommand.execute(model);
    }

    public Index getIndex() {
        return targetIndex;
    }

    public Info getInfo() {
        return updatedInfo;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof InfoCommand)) {
            return false;
        }

        InfoCommand otherCommand = (InfoCommand) other;
        return targetIndex.equals(otherCommand.targetIndex)
                && Objects.equals(updatedInfo, otherCommand.updatedInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, updatedInfo);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("updatedInfo", updatedInfo)
                .toString();
    }
}
