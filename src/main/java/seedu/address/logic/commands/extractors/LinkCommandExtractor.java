package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.Link;

/**
 * Extractor that builds {@code LinkCommand}s.
 */
public class LinkCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_USAGE = LinkCommand.MESSAGE_USAGE;
    public static final String MESSAGE_EXPECTED_3_PARAMS =
            "Expected 3 positional parameters: INDEX-FROM LINK-NAME INDEX-TO.";
    public static final String MESSAGE_LINK_NAME_REQUIRED =
            "Link name must be provided as the second positional parameter.";
    public static final String MESSAGE_LINK_NAME_INVALID = Link.MESSAGE_CONSTRAINTS;
    public static final String MESSAGE_SAME_PERSON = "Cannot link a person to themselves.";

    private LinkCommandExtractor() {
    }

    /**
     * Extracts and validates parameters for {@code LinkCommand}.
     *
     * @param bareCommand parsed bare command (must be for 'link').
     * @return a validated {@code LinkCommand}.
     * @throws ValidationException if validation fails.
     */
    public static LinkCommand extract(BareCommand bareCommand) throws ValidationException {
        // checks if number of parameters passed in is correct
        if (bareCommand.parameterCount() < 3) {
            throw new ValidationException(MESSAGE_EXPECTED_3_PARAMS + "\n" + MESSAGE_USAGE);
        }

        // parse indices and validate kind (pos 0 and 2)
        Index linkerIndex = Validation.validateIndex(bareCommand, 0);
        Index linkeeIndex = Validation.validateIndex(bareCommand, 2);


        Parameter linkNameParam = Validation.validateParameter(bareCommand, 1, Parameter.ParameterKind.NORMAL);
        String linkName = linkNameParam.getValue();

        if (linkName == null || linkName.isBlank()) {
            throw new ValidationException(MESSAGE_LINK_NAME_REQUIRED + "\n" + MESSAGE_USAGE);
        }

        // validate link name (pos 1)
        if (!Link.isValidLinkName(linkName)) {
            throw new ValidationException(MESSAGE_LINK_NAME_INVALID);
        }

        // validate not linking same person to itself
        if (linkerIndex.equals(linkeeIndex)) {
            throw new ValidationException(MESSAGE_SAME_PERSON);
        }

        return new LinkCommand(linkerIndex, linkName, linkeeIndex);
    }
}
