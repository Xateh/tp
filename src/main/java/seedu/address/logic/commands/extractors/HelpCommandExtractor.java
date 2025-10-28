package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code HelpCommand}s.
 */
public final class HelpCommandExtractor {
    private HelpCommandExtractor() {
    }

    /**
     * Blank extractor for {@code HelpCommand}s, which take no parameters.
     *
     * @param bareCommand Command to extract parameters from.
     * @return HelpCommand that can be executed.
     */
    public static HelpCommand extract(BareCommand bareCommand) {
        return new HelpCommand();
    }
}
