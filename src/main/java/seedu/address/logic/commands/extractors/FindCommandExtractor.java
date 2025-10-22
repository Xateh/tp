package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.FieldContainsKeywordsPredicate;

/**
 * Extractor that builds {@code FindCommand}s.
 */
public final class FindCommandExtractor {
    private FindCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return FindCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static FindCommand extract(BareCommand bareCommand) throws ValidationException {
        // extract keywords from parameters
        List<String> keywords = Validation.validateVariableParametersWithMinimumMultiplicity(
                        bareCommand, 0, 1, ParameterKind.NORMAL)
                .stream().map(BareCommand.Parameter::getValue).toList();

        // Determine which fields to search using options provided
        boolean optName = bareCommand.hasOption("name");
        boolean optPhone = bareCommand.hasOption("phone");
        boolean optEmail = bareCommand.hasOption("email");
        boolean optAddress = bareCommand.hasOption("address");
        boolean optTag = bareCommand.hasOption("tag");
        List<String> keysToRemove = List.of("name", "phone", "email", "address", "tags", "tag");
        Map<String, String> map = bareCommand.getAllOptions();
        Set<String> customKeys = map.keySet().stream()
                .map(k -> k != null ? k.trim().toLowerCase() : "")
                .filter(k -> !keysToRemove.contains(k))
                .collect(Collectors.toSet());

        // check if user specified any options
        boolean anyFlag = optName || optPhone || optEmail || optAddress || optTag;

        FieldContainsKeywordsPredicate predicate;
        if (anyFlag || !customKeys.isEmpty()) {
            // Only the specified fields
            predicate = new FieldContainsKeywordsPredicate(
                    keywords,
                    optName,
                    optPhone,
                    optEmail,
                    optAddress,
                    optTag,
                    customKeys
            );
        } else {
            // No options provided, default to search all non custom fields
            predicate = new FieldContainsKeywordsPredicate(keywords);
        }

        return new FindCommand(predicate);
    }
}
