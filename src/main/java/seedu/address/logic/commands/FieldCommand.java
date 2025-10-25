package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            + ": Adds or updates custom fields for the person identified by the index number.\n"
            + "Parameters: INDEX (must be a positive integer) /KEY:VALUE...\n"
            + "Example: " + COMMAND_WORD + " 1 /company:\"Goldman Sachs\"";
    public static final String MESSAGE_VALUE_CANNOT_BE_BLANK = "Field value cannot be blank.";
    public static final String MESSAGE_NAME_CANNOT_BE_BLANK = "Field name cannot be blank.";
    public static final String MESSAGE_AT_LEAST_ONE_PAIR =
            "Provide at least one /key:value pair. Usage: field <index> /key:value ...";
    private final Index index;
    private final Map<String, String> pairs;
    private final java.util.List<Warning> initialWarnings;

    /**
     * Creates a FieldCommand.
     */
    public FieldCommand(Index index, Map<String, String> pairs) {
        requireNonNull(index);
        requireNonNull(pairs);
        if (pairs.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_AT_LEAST_ONE_PAIR);
        }
        Map<String, String> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String key = normalize(entry.getKey());
            String value = normalize(entry.getValue());
            if (key.isEmpty()) {
                throw new IllegalArgumentException(MESSAGE_NAME_CANNOT_BE_BLANK);
            }
            if (value.isEmpty()) {
                throw new IllegalArgumentException(MESSAGE_VALUE_CANNOT_BE_BLANK);
            }
            sanitized.put(key, value);
        }
        this.index = index;
        this.pairs = sanitized;
        this.initialWarnings = java.util.List.of();
    }

    /**
     * Creates a FieldCommand with parser-level warnings (e.g. duplicate option values ignored).
     */
    public FieldCommand(Index index, Map<String, String> pairs, java.util.List<Warning> initialWarnings) {
        requireNonNull(index);
        requireNonNull(pairs);
        requireNonNull(initialWarnings);
        if (pairs.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_AT_LEAST_ONE_PAIR);
        }
        Map<String, String> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String key = normalize(entry.getKey());
            String value = normalize(entry.getValue());
            if (key.isEmpty()) {
                throw new IllegalArgumentException(MESSAGE_NAME_CANNOT_BE_BLANK);
            }
            if (value.isEmpty()) {
                throw new IllegalArgumentException(MESSAGE_VALUE_CANNOT_BE_BLANK);
            }
            sanitized.put(key, value);
        }
        this.index = index;
        this.pairs = sanitized;
        this.initialWarnings = java.util.List.copyOf(initialWarnings);
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
        // detect overwritten keys
        Set<String> overwritten = new LinkedHashSet<>();
        for (Map.Entry<String, String> e : pairs.entrySet()) {
            if (merged.containsKey(e.getKey())) {
                overwritten.add(e.getKey());
            }
            merged.put(e.getKey(), e.getValue());
        }

        Person edited = target.withCustomFields(merged);
        model.setPerson(target, edited);
        // persistence handled by LogicManager or caller

        // Build feedback message
        StringBuilder sb = new StringBuilder("Added/updated field(s): ");
        boolean first = true;
        for (Map.Entry<String, String> e : pairs.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(e.getKey()).append(":").append(e.getValue());
        }
        sb.append(" for ").append(edited.getName().fullName);
        String feedback = sb.toString();

        java.util.List<Warning> warnings = new java.util.ArrayList<>(initialWarnings == null
                ? java.util.List.of()
                : initialWarnings);
        if (!overwritten.isEmpty()) {
            String keys = String.join(", ", overwritten);
            warnings.add(Warning.fieldOverwritten("Overwritten keys: " + keys));
        }

        if (warnings.isEmpty()) {
            return new CommandResult(feedback);
        }
        return new CommandResult(feedback, warnings);
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}

