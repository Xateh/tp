package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code ClearCommand}s.
 */
public final class ClearCommandExtractor {
    private ClearCommandExtractor() {
    }

    /**
     * Blank extractor for {@code ClearCommand}s, which take no parameters.
     *
     * @param bareCommand Command to extract parameters from.
     * @return ClearCommand that can be executed.
     */
    public static ClearCommand extract(BareCommand bareCommand) {
        return new ClearCommand();
    }
}
