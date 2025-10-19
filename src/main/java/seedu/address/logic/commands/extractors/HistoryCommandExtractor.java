package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code HistoryCommand}s.
 */
public final class HistoryCommandExtractor {

    private HistoryCommandExtractor() {
        // Utility class
    }

    /**
     * Extracts a {@link HistoryCommand} from the provided {@link BareCommand}.
     *
     * @throws ValidationException if unexpected parameters or options are supplied.
     */
    public static HistoryCommand extract(BareCommand bareCommand) throws ValidationException {
        requireNonNull(bareCommand);
        return new HistoryCommand();
    }
}
