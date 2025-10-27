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
import seedu.address.model.person.builder.PersonBuilder;

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
    public static final String MESSAGE_DISALLOWED_FIELD_NAME =
            "Field name '%s' is reserved and cannot be used.";
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

        Map<String, String> mergedFields = new LinkedHashMap<>(target.getCustomFields());
        List<String> successfulRemovals = applyRemovals(mergedFields);
        applyUpdates(mergedFields);

        Person edited = new PersonBuilder(target)
                .withCustomFields(mergedFields)
                .build();
        model.setPerson(target, edited);

        String feedback = buildFeedbackMessage(edited, successfulRemovals);
        return new CommandResult(feedback);
    }

    private List<String> applyRemovals(Map<String, String> fields) {
        List<String> successfulRemovals = new ArrayList<>();
        for (String removal : removals) {
            if (fields.remove(removal) != null) {
                successfulRemovals.add(removal);
            }
        }
        return successfulRemovals;
    }

    private void applyUpdates(Map<String, String> fields) {
        fields.putAll(updates);
    }

    private String buildFeedbackMessage(Person edited, List<String> successfulRemovals) {
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
        return sb.toString();
    }
}
