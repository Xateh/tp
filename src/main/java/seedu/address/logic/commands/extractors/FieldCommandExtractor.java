package seedu.address.logic.commands.extractors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code FieldCommand}s from {@link BareCommand}s.
 */
public final class FieldCommandExtractor {
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_WRONG_IMPERATIVE = "Wrong imperative for FieldCommand";

    private FieldCommandExtractor() {}

    /**
     * Extracts the parameters required to build a {@link FieldCommand}.
     *
     * @param bareCommand command parsed by the grammar system.
     * @return a {@link FieldCommand} that can be executed.
     */
    public static FieldCommand extract(BareCommand bareCommand) throws ValidationException {
        Index index = Validation.validateIndex(bareCommand, 0);

        Map<String, List<String>> options = bareCommand.getAllOptions();
        if (options.isEmpty()) {
            throw new ValidationException(FieldCommand.MESSAGE_AT_LEAST_ONE_PAIR);
        }

        Map<String, String> pairs = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            String key = normalize(entry.getKey());
            if (key.isEmpty()) {
                throw new ValidationException(FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK);
            }
            List<String> values = entry.getValue();
            String rawValue = (values == null || values.isEmpty()) ? "" : values.get(0);
            String value = normalize(rawValue);
            if (value.isEmpty()) {
                throw new ValidationException(FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK);
            }
            pairs.put(key, value);
        }
        try {
            return new FieldCommand(index, pairs);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}
