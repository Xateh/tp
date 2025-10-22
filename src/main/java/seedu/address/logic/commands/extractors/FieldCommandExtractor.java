package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;

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
     * @throws ValidationException if the command parameters fail validation.
     */
    public static FieldCommand extract(BareCommand bareCommand) throws ValidationException {
        requireNonNull(bareCommand);

        if (!FieldCommand.COMMAND_WORD.equalsIgnoreCase(bareCommand.getImperative())) {
            throw new ValidationException(MESSAGE_WRONG_IMPERATIVE);
        }

        String[] params = bareCommand.getAllParameters();
        if (params.length == 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }
        Index index = Validation.validateIndex(params[0]);

        Map<String, List<String>> options = bareCommand.getAllOptions();
        if (options.isEmpty()) {
            throw new ValidationException(FieldCommand.MESSAGE_AT_LEAST_ONE_PAIR);
        }

        Map<String, String> pairs = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            String normalizedKey = normalize(entry.getKey());
            List<String> values = entry.getValue();
            String firstValue = (values == null || values.isEmpty()) ? "" : values.get(0);
            String normalizedValue = normalize(firstValue);

            if (normalizedKey.isEmpty()) {
                throw new ValidationException(FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK);
            }
            if (normalizedValue.isEmpty()) {
                throw new ValidationException(FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK);
            }
            pairs.put(normalizedKey, normalizedValue);
        }

        return new FieldCommand(index, pairs);
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}
