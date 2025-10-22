package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import seedu.address.model.Model;
import seedu.address.model.history.CommandHistory;

/**
 * Displays the list of previously executed commands.
 */
public class HistoryCommand extends Command {
    public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays the list of commands previously entered.";
    public static final String MESSAGE_EMPTY_HISTORY = "Command history is empty.";
    public static final String MESSAGE_SUCCESS = "Command history:\n%s";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        CommandHistory commandHistory = requireNonNull(model.getCommandHistory());
        List<String> entries = commandHistory.getEntries();
        if (entries.isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_HISTORY);
        }

        String formatted = IntStream.range(0, entries.size())
                .mapToObj(index -> String.format("%d. %s", index + 1, entries.get(index)))
                .collect(Collectors.joining(System.lineSeparator()));

        return new CommandResult(String.format(MESSAGE_SUCCESS, formatted));
    }
}
