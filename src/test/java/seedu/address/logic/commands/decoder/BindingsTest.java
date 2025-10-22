package seedu.address.logic.commands.decoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.commands.extractors.CommandExtractor;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

/**
 * Only negative tests here.
 */
public class BindingsTest {
    @Test
    public void bindings_resolveExactAmbiguous_throwsResolutionException() {
        assertThrows(ResolutionException.class, () -> Bindings.resolveExactBinding((s) -> true));
    }

    @Test
    public void bindings_resolveExactNoMatches_throwsResolutionException() {
        assertThrows(ResolutionException.class, () -> Bindings.resolveExactBinding((s) -> false));
    }

    @Test
    public void bindings_resolveExactHistory_returnsHistoryCommandExtractor()
            throws ResolutionException, LexerException, ParserException, ValidationException {
        CommandExtractor<? extends Command> extractor = Bindings.resolveExactBinding("history"::equals);
        HistoryCommand command = (HistoryCommand) extractor.extract(BareCommand.parse("history"));

        assertNotNull(command);
    }

    @Test
    public void bindings_resolveBindings_filtersImperatives()
            throws LexerException, ParserException, ValidationException {
        CommandExtractor<?>[] extractors = Bindings.resolveBindings(s -> s.startsWith("hist"));

        assertEquals(1, extractors.length);
        assertEquals(HistoryCommand.class, extractors[0].extract(BareCommand.parse("history")).getClass());
    }
}
