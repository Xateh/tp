package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code FieldCommand}s from {@link BareCommand}s.
 */
public final class FieldCommandExtractor {
    private FieldCommandExtractor() {}

    /**
     * Extracts the parameters required to build a {@link FieldCommand}.
     *
     * @param bareCommand command parsed by the grammar system.
     * @return a {@link FieldCommand} that can be executed.
     * @throws ValidationException if the command parameters fail validation.
     */
    public static FieldCommand extract(BareCommand bareCommand) throws ValidationException {
        try {
            return new FieldCommand(bareCommand);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage(), ex);
        }
    }
}
