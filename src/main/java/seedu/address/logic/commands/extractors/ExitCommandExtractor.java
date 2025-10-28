package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code ExitCommand}s.
 */
public final class ExitCommandExtractor {
    private ExitCommandExtractor() {
    }

    /**
     * Blank extractor for {@code ExitCommand}s, which take no parameters.
     *
     * @param bareCommand Command to extract parameters from.
     * @return ExitCommand that can be executed.
     */
    public static ExitCommand extract(BareCommand bareCommand) {
        return new ExitCommand();
    }
}
