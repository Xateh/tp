package seedu.address.logic.commands.decoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.ResolutionException;

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
}
