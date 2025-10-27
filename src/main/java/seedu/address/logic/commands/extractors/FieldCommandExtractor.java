package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private static final List<String> DISALLOWED_CUSTOM_FIELD_NAMES = List.of("name", "email", "phone",
                                    "address", "tag", "field", "link", "find");

    private FieldCommandExtractor() {}

    /**
     * Extracts the parameters required to build a {@link FieldCommand}.
     *
     * @param bareCommand command parsed by the grammar system.
     * @return a {@link FieldCommand} that can be executed.
     */
    public static FieldCommand extract(BareCommand bareCommand) throws ValidationException {
        Index index = Validation.validateIndex(bareCommand, 0);

        List<String> optionKeys = bareCommand.getVariableOptionKeys();
        if (optionKeys.isEmpty()) {
            throw new ValidationException(FieldCommand.MESSAGE_AT_LEAST_ONE_PAIR);
        }

        Map<String, String> updates = new LinkedHashMap<>();
        List<String> removals = new ArrayList<>();

        for (String rawKey : optionKeys) {
            String key = validateCustomFieldName(rawKey,
                    FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK,
                    FieldCommand.MESSAGE_DISALLOWED_FIELD_NAME);

            Optional<List<String>> allValues = bareCommand.getOptionAllValues(rawKey);
            Optional<String> optionValue = Optional.empty();
            if (allValues.isPresent()) {
                List<String> values = allValues.get();
                if (!values.isEmpty() && values.get(0) != null) {
                    optionValue = bareCommand.getOptionValue(rawKey);
                }
            }

            if (optionValue.isPresent()) {
                String value = normalize(optionValue.get());
                if (!value.isEmpty()) {
                    updates.put(key, value);
                    continue;
                }
            }

            removals.add(key);
        }
        return new FieldCommand(index, updates, removals);
    }

    private static String validateCustomFieldName(String rawKey, String emptyMessage,
            String disallowedMessageFormat) throws ValidationException {
        requireNonNull(rawKey);
        requireNonNull(emptyMessage);
        requireNonNull(disallowedMessageFormat);

        String trimmed = rawKey.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(emptyMessage);
        }

        if (DISALLOWED_CUSTOM_FIELD_NAMES.contains(trimmed.toLowerCase())) {
            throw new ValidationException(String.format(disallowedMessageFormat, trimmed));
        }

        return trimmed;
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}
