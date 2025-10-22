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
        String param0 = Validation.validateParameter(bareCommand, 0, ParameterKind.NORMAL).getValue();
        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(param0));
        } catch (NumberFormatException e) {
            // only thrown by Integer::parseInt
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, param0));
        } catch (IndexOutOfBoundsException e) {
            // only thrown by Index::fromOneBased
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, param0));
        }

        // extract tags
        Set<Tag> tags = new HashSet<>();
        List<Parameter> varParams;
        try {
            varParams = Validation.validateVariableParametersWithMinimumMultiplicity(
                    bareCommand, 1, 1, ParameterKind.NORMAL);
            for (Parameter varParam : varParams) {
                tags.add(new Tag(varParam.getValue()));
            }
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage() + "\n" + MESSAGE_TAGS_UNSPECIFIED);
        }

        return new TagCommand(index, tags);
    }
}
