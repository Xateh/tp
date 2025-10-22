package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.ui.UiManager;

/**
 * Opens the information editor for a person identified by the index number used in the displayed person list.
 */
public class InfoEditCommand extends Command {

    public static final String COMMAND_WORD = "info";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the info of the person identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_INFO_EDIT_SUCCESS = "Opening info editor for Person: %1$s";

    private static UiManager uiManager; // Static reference to UiManager
    private final Index targetIndex;

    public InfoEditCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Sets the UiManager instance for this command to use.
     * This should be called during application initialization.
     */
    public static void setUiManager(UiManager uiManager) {
        InfoEditCommand.uiManager = uiManager;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());

        // Call UiManager directly to show the info editor
        if (uiManager != null) {
            uiManager.showInfoEditor(personToEdit, targetIndex.getZeroBased());
        } else {
            throw new CommandException("UI Manager not initialized. Cannot open info editor.");
        }

        return new CommandResult(String.format(MESSAGE_INFO_EDIT_SUCCESS, Messages.format(personToEdit)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof InfoEditCommand
                && targetIndex.equals(((InfoEditCommand) other).targetIndex));
    }
}
