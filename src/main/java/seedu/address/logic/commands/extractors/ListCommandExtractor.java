package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Extractor that builds {@code ListCommand}s.
 */
public final class ListCommandExtractor {
    private ListCommandExtractor() {
    }

    /**
     * Blank extractor for {@code ListCommand}s, which take no parameters.
     *
     * @param bareCommand Command to extract parameters from.
     * @return ListCommand that can be executed.
     */
    public static ListCommand extract(BareCommand bareCommand) {
        return new ListCommand();
    }
}
