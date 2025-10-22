package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.grammars.command.BareCommand;
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
    private final int oneBasedIndex;
    private final Map<String, String> pairs;

    /**
     * Creates a FieldCommand from a parsed {@link BareCommand}.
     * @throws IllegalArgumentException if parameters/options are invalid.
     */
    public FieldCommand(BareCommand c) {
        requireNonNull(c);
        if (!"field".equalsIgnoreCase(c.getImperative())) {
            throw new IllegalArgumentException("Wrong imperative for FieldCommand");
        }
        // Parse index (1-based)
        try {
            this.oneBasedIndex = Integer.parseInt(c.getParameter(0));
        } catch (ArrayIndexOutOfBoundsException ex0) {
            throw new IllegalArgumentException("Missing index. Usage: field <index> /key:value ...", ex0);
        } catch (NumberFormatException ex1) {
            throw new IllegalArgumentException("Invalid index. Must be a positive integer.", ex1);
        }
        // Collect /key:value options
        Map<String, String> tmp = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : c.getAllOptions().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue().get(0);
            if (v != null) {
                tmp.put(k, v);
            }
        }
        if (tmp.isEmpty()) {
            throw new IllegalArgumentException(
                "Provide at least one /key:value pair. Usage: field <index> /key:value ...");
        }
        this.pairs = tmp;
    }

    /**
     * Convenience constructor used by tests.
     */
    public FieldCommand(int oneBasedIndex, Map<String, String> pairs) {
        if (oneBasedIndex <= 0 || pairs == null || pairs.isEmpty()) {
            throw new IllegalArgumentException("Index must be > 0 and at least one /key:value pair provided.");
        }
        this.oneBasedIndex = oneBasedIndex;
        this.pairs = new LinkedHashMap<>(pairs);
    }

    /**
     * Executes the command: updates the selected person's custom fields and returns a user message.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> list = model.getFilteredPersonList();
        int zero = oneBasedIndex - 1;
        if (zero < 0 || zero >= list.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person target = list.get(zero);

        // Merge strategy: overwrite existing keys with new values, keep others.
        Map<String, String> merged = new LinkedHashMap<>(target.getCustomFields());
        try {
            for (Map.Entry<String, String> e : pairs.entrySet()) {
                String k = normalizeKey(e.getKey());
                String v = normalizeValue(e.getValue());
                validate(k, v);
                merged.put(k, v);
            }
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage(), e);
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
        return new CommandResult(sb.toString());
    }

    private static String normalizeKey(String key) {
        return key == null ? "" : key.trim();
    }

    private static String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private static void validate(String key, String value) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be blank.");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Field value cannot be blank.");
        }
        // Optional: add length constraints if CheckStyle/enforcer requires.
    }
}

