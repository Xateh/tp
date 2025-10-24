package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class HistoryNavigatorTest {

    @Test
    void previous_emptyHistory_returnsEmpty() {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of());
        assertTrue(nav.previous().isEmpty());
        assertEquals(0, nav.getPointer());
    }

    @Test
    void next_emptyHistory_staysAtEnd() {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of());
        assertTrue(nav.next().isEmpty());
        assertEquals(0, nav.getPointer());
    }

    @Test
    void previousAndNextCycleCorrectly() {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of("one", "two"));

        // pointer initially at end
        assertEquals(2, nav.getPointer());

        // previous -> last
        assertEquals("two", nav.previous().orElseThrow());
        assertEquals(1, nav.getPointer());

        // previous -> first
        assertEquals("one", nav.previous().orElseThrow());
        assertEquals(0, nav.getPointer());

        // previous again -> stays and returns empty
        assertTrue(nav.previous().isEmpty());
        assertEquals(0, nav.getPointer());

        // next -> moves to second
        assertEquals("two", nav.next().orElseThrow());
        assertEquals(1, nav.getPointer());

        // next -> moves to end and returns empty
        assertTrue(nav.next().isEmpty());
        assertEquals(2, nav.getPointer());

        // next again -> still empty
        assertTrue(nav.next().isEmpty());
        assertEquals(2, nav.getPointer());
    }

    @Test
    void resetReplacesEntriesAndPointerMovesToEnd() {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of("a", "b", "c"));
        assertEquals(3, nav.getPointer());
        assertEquals(List.of("a", "b", "c"), nav.getEntries());

        nav.previous();
        assertEquals(2, nav.getPointer());

        nav.reset(List.of("x"));
        assertEquals(1, nav.getPointer());
        assertEquals(List.of("x"), nav.getEntries());
    }

}
