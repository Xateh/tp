package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds or updates custom key→value fields on a person.
 * Usage: {@code field <index> /<key>:<value> ...}
 */
public class FieldCommand extends Command {

    public static final String COMMAND_WORD = "field";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds, updates, or removes custom fields for the person identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer) /KEY[:VALUE]...\n"
            + "Example: " + COMMAND_WORD + " 1 /company:\"Goldman Sachs\" /nickname";
    public static final String MESSAGE_NAME_CANNOT_BE_BLANK = "Field name cannot be blank.";
    public static final String MESSAGE_AT_LEAST_ONE_PAIR =
            "Provide at least one /key or /key:value option. Usage: field <index> /key[:value] ...";
    private final Index index;
    private final Map<String, String> updates;
    private final List<String> removals;

    /**
     * Creates a FieldCommand.
     */
    public FieldCommand(Index index, Map<String, String> updates, List<String> removals) {
        requireNonNull(index);
        requireNonNull(updates);
        requireNonNull(removals);

        if (updates.isEmpty() && removals.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_AT_LEAST_ONE_PAIR);
        }
        this.index = index;
        this.updates = new LinkedHashMap<>(updates);
        this.removals = List.copyOf(removals);
    }

    /**
     * Executes the command: updates the selected person's custom fields and returns a user message.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> list = model.getFilteredPersonList();
        int zero = index.getZeroBased();
        if (zero < 0 || zero >= list.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person target = list.get(zero);

        // Merge strategy: overwrite existing keys with new values, keep others.
        Map<String, String> merged = new LinkedHashMap<>(target.getCustomFields());
        List<String> successfulRemovals = new ArrayList<>();
        for (String removal : removals) {
            if (merged.containsKey(removal)) {
                merged.remove(removal);
                successfulRemovals.add(removal);
            }
        }
        for (Map.Entry<String, String> e : updates.entrySet()) {
            merged.put(e.getKey(), e.getValue());
        }
        Person edited = target.withCustomFields(merged);
        model.setPerson(target, edited);
        // persistence handled by LogicManager or caller

        // Build feedback message
        StringBuilder sb = new StringBuilder();
        if (!updates.isEmpty()) {
            String joinedUpdates = updates.entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(", "));
            sb.append("Added/updated field(s): ").append(joinedUpdates);
        }
        if (!successfulRemovals.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            String joinedRemovals = String.join(", ", successfulRemovals);
            sb.append("Removed field(s): ").append(joinedRemovals);
        }
        if (sb.length() == 0) {
            sb.append("No field changes applied");
        }
        sb.append(" for ").append(edited.getName().fullName);
        return new CommandResult(sb.toString());
    }
}
