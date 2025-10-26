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
public final class TagCommandExtractor {
    // Messages for extraction
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
        Set<Tag> addTags = new HashSet<>();
        Set<Tag> subTags = new HashSet<>();
        try {
            List<Parameter> varParams = Validation.validateVariableParametersWithMinimumMultiplicity(
                    bareCommand, 1, 1, ParameterKind.ADDITIVE, ParameterKind.SUBTRACTIVE);
            for (Parameter param : varParams) {
                if (param.isAdditive()) {
                    addTags.add(new Tag(param.getValue()));
                } else if (param.isSubtractive()) {
                    subTags.add(new Tag(param.getValue()));
                }
            }
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage() + "\n" + MESSAGE_TAGS_UNSPECIFIED);
        }

        return new TagCommand(index, addTags, subTags);
    }
}
