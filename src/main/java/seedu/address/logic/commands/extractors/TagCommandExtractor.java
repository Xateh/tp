package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.tag.Tag;

/**
 * Extractor that builds {@code TagCommand}s.
 */
public class TagCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_TAGS_UNSPECIFIED = "At least one tag must be specified.";

    private TagCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return TagCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static TagCommand extract(BareCommand bareCommand) throws ValidationException {
        // extract index
        Index index = Validation.validateIndex(bareCommand, 0);

        // extract tags
        Set<Tag> tags = new HashSet<>();
        List<Parameter> varParams;
        try {
            varParams = Validation.validateVariableParametersWithMinimumMultiplicity(
                    bareCommand, 1, 1, ParameterKind.NORMAL);

            java.util.List<String> rawValues = new java.util.ArrayList<>();
            for (Parameter varParam : varParams) {
                rawValues.add(varParam.getValue());
                tags.add(new Tag(varParam.getValue()));
            }

            // detect duplicate input tokens (same tag provided multiple times)
            java.util.Set<String> unique = new java.util.HashSet<>(rawValues);
            java.util.List<seedu.address.logic.commands.Warning> warnings = new java.util.ArrayList<>();
            if (rawValues.size() != unique.size()) {
                warnings.add(seedu.address.logic.commands.Warning.duplicateInputIgnored(
                        "Some duplicate tag inputs were ignored."));
            }

            return new TagCommand(index, tags, warnings);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage() + "\n" + MESSAGE_TAGS_UNSPECIFIED);
        }
    }
}
