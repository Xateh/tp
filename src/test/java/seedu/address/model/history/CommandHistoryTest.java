package seedu.address.model.history;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class CommandHistoryTest {

    @Test
    void constructor_invalidMaxEntries_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CommandHistory(List.of(), 0));
    }

    @Test
    void add_nullCommand_throwsNullPointerException() {
        CommandHistory history = new CommandHistory();
        assertThrows(NullPointerException.class, () -> history.add(null));
    }

    @Test
    void add_blankCommand_ignored() {
        CommandHistory history = new CommandHistory();
        history.add("   ");
        assertTrue(history.isEmpty());
    }

    @Test
    void add_trimsAndRetainsOrder() {
        CommandHistory history = new CommandHistory();
        history.add(" list ");
        history.add("add n/Amy");
        assertEquals(List.of("list", "add n/Amy"), history.getEntries());
    }

    @Test
    void add_respectsMaximumEntries() {
        CommandHistory history = new CommandHistory(List.of(), 3);
        history.add("first");
        history.add("second");
        history.add("third");
        history.add("fourth");
        assertEquals(List.of("second", "third", "fourth"), history.getEntries());
    }

    @Test
    void reset_nullEntries_throwsNullPointerException() {
        CommandHistory history = new CommandHistory();
        assertThrows(NullPointerException.class, () -> history.reset(null));
    }

    @Test
    void reset_filtersInvalidEntriesAndAppliesLimit() {
        CommandHistory history = new CommandHistory(List.of("placeholder"), 3);
        history.reset(Arrays.asList(" first ", null, " ", "second", "third", "fourth"));
        assertEquals(List.of("second", "third", "fourth"), history.getEntries());
    }

    @Test
    void getEntries_returnsUnmodifiableList() {
        CommandHistory history = new CommandHistory(List.of("list"));
        List<String> entries = history.getEntries();
        assertThrows(UnsupportedOperationException.class, () -> entries.add("another"));
    }

    @Test
    void equals_sameEntriesAndLimit_returnsTrue() {
        CommandHistory first = new CommandHistory(List.of("list", "add n/Amy"), 5);
        CommandHistory second = new CommandHistory(List.of("list", "add n/Amy"), 5);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equals_differentLimits_returnsFalse() {
        CommandHistory limited = new CommandHistory(List.of("list"), 3);
        CommandHistory moreLimited = new CommandHistory(List.of("list"), 2);
        assertFalse(limited.equals(moreLimited));
    }
}
