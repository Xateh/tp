package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.List;

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
    public static final String MESSAGE_USAGE =
            "link: Creates a directed link between two persons.\n"
                    + "Parameters: INDEX1 LINK_NAME INDEX2\n"
                    + "Example: link 1 lawyer 2";
    public static final String MESSAGE_EXPECTED_3_PARAMS =
            "Expected exactly 3 positional parameters: INDEX1 LINK_NAME INDEX2.";
    public static final String MESSAGE_LINK_NAME_REQUIRED =
            "Link name must be provided as the second positional parameter.";
    public static final String MESSAGE_LINK_NAME_INVALID = Link.MESSAGE_CONSTRAINTS;
    public static final String MESSAGE_PARAM_KIND =
            "Only normal positional parameters are allowed for 'link' (no + / - kinds).";

    private LinkCommandExtractor() {
    }

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return LinkCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    /**
     * Extracts and validates parameters for {@code LinkCommand}.
     *
     * @param bareCommand parsed bare command (must be for 'link').
     * @return a validated {@code LinkCommand}.
     * @throws ValidationException if validation fails.
     */
    public static LinkCommand extract(BareCommand bareCommand) throws ValidationException {
        // checks if number of parameters passed in is correct
        if (bareCommand.parameterCount() != 3) {
            throw new ValidationException(MESSAGE_EXPECTED_3_PARAMS + "\n" + MESSAGE_USAGE);
        }

        // parameter kind checks (all must be NORMAL)
        for (int i = 0; i < 3; i++) {
            Parameter p = bareCommand.getParameter(i);
            if (!p.isNormal()) {
                throw new ValidationException(MESSAGE_PARAM_KIND + "\n" + MESSAGE_USAGE);
            }
        }

        // parse indices (pos 0 and 2)
        Index linkerIndex = Validation.validateIndex(bareCommand, 0);
        Index linkeeIndex = Validation.validateIndex(bareCommand, 2);

        // validate link name (pos 1)
        String linkName = bareCommand.getParameter(1).getValue();
        if (linkName == null || linkName.isBlank()) {
            throw new ValidationException(MESSAGE_LINK_NAME_REQUIRED + "\n" + MESSAGE_USAGE);
        }
        if (!Link.isValidLinkName(linkName)) {
            throw new ValidationException(MESSAGE_LINK_NAME_INVALID);
        }

        return new LinkCommand(linkerIndex, linkName, linkeeIndex);
    }
}