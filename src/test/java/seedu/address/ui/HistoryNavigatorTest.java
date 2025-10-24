package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
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

    @Test
    void previous_whenPointerGreaterThanSize_clampsAndReturnsLast() throws Exception {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of("one", "two", "three"));

        // set pointer to an invalid large value via reflection
        Field pointerField = HistoryNavigator.class.getDeclaredField("pointer");
        pointerField.setAccessible(true);
        pointerField.setInt(nav, 10);

        // previous should clamp pointer to size and then to last index -> return "three"
        assertEquals("three", nav.previous().orElseThrow());
        // pointer should now point to index 2
        assertEquals(2, nav.getPointer());
    }

    @Test
    void next_whenPointerNegative_incrementsAndReturnsFirst() throws Exception {
        HistoryNavigator nav = new HistoryNavigator();
        nav.reset(List.of("alpha", "beta"));

        Field pointerField = HistoryNavigator.class.getDeclaredField("pointer");
        pointerField.setAccessible(true);
        pointerField.setInt(nav, -1);

        // next should increment from -1 to 0 and return the first element
        assertEquals("alpha", nav.next().orElseThrow());
        assertEquals(0, nav.getPointer());
    }

}
