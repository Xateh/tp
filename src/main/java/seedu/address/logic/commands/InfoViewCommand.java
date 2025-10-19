package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Views the information of a person identified by the index number used in the displayed person list.
 */
public class InfoViewCommand extends Command {

    public static final String COMMAND_WORD = "infoview";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays the info of the person identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_INFO_VIEW_SUCCESS = "Viewing info for Person: %1$s\n---\n%2$s";

    private final Index targetIndex;

    public InfoViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person person = lastShownList.get(targetIndex.getZeroBased());
        String infoText = person.getInfo().value.isEmpty() ? "No information available." : person.getInfo().value;

        return new CommandResult(String.format(MESSAGE_INFO_VIEW_SUCCESS, Messages.format(person), infoText));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof InfoViewCommand
                && targetIndex.equals(((InfoViewCommand) other).targetIndex));
    }
}
