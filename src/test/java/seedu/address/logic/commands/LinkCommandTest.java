package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

public class LinkCommandTest {

    // ---- Constructor guard rails -------------------------------------------------------------

    @Test
    public void constructor_nullLinkerIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new LinkCommand(null, "mentor", Index.fromOneBased(2)));
    }

    @Test
    public void constructor_nullLinkName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new LinkCommand(Index.fromOneBased(1), null, Index.fromOneBased(2)));
    }

    @Test
    public void constructor_nullLinkeeIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                        -> new LinkCommand(Index.fromOneBased(1), "mentor", null));
    }

    // ---- equals / hashCode ------------------------------------------------------------------

    @Test
    public void equals_sameValues_returnsTrue() {
        LinkCommand a = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        LinkCommand b = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        assertEquals(a, b);
    }

    @Test
    public void equals_differentLinkerIndex_returnsFalse() {
        LinkCommand a = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        LinkCommand b = new LinkCommand(Index.fromOneBased(3), "mentor", Index.fromOneBased(2));
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentLinkName_returnsFalse() {
        LinkCommand a = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        LinkCommand b = new LinkCommand(Index.fromOneBased(1), "advisor", Index.fromOneBased(2));
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentLinkeeIndex_returnsFalse() {
        LinkCommand a = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        LinkCommand b = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(5));
        assertNotEquals(a, b);
    }
}

