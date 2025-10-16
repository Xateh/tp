package seedu.address.logic.commands.decoder;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.commands.extractors.CommandExtractor;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Utility class for decoding given commands and dispatching to the relevant command(s).
 */
public class Decoder {
    private Decoder() {}

    /**
     * Decodes the relevant {@code Command} to be executed.
     * @param bareCommand Input bare command
     * @return Decoded command, ready for execution.
     * @throws ResolutionException When a command could not be definitively resolved.
     */
    public static Command decode(BareCommand bareCommand) throws ResolutionException, ValidationException {
        String currentImperative = bareCommand.getImperative();

        // use exact matching
        CommandExtractor<?> extractor =
                Bindings.resolveExactBinding(imperative -> imperative.equals(currentImperative));

        return extractor.extract(bareCommand);
    }
}
