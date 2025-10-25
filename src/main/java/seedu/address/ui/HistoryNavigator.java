package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Pure logic for navigating a list of command history entries.
 *
 * This class encapsulates the pointer behaviour used by {@link CommandBox} so it can be tested
 * without initializing JavaFX.
 */
public class HistoryNavigator {

    private List<String> history = new ArrayList<>();
    private int pointer = 0; // points to current index in history; pointer == history.size() means "after last" (empty)

    /**
     * Reset the navigator with a snapshot of history entries. The pointer moves to the end.
     */
    public void reset(List<String> entries) {
        Objects.requireNonNull(entries);
        this.history = new ArrayList<>(entries);
        this.pointer = this.history.size();
    }

    /**
     * Navigate to the previous (older) command. Returns the command to display, or empty if none.
     */
    public Optional<String> previous() {
        if (history.isEmpty()) {
            return Optional.empty();
        }

        if (pointer > history.size()) {
            pointer = history.size();
        }

        if (pointer == history.size()) {
            pointer = history.size() - 1;
        } else if (pointer > 0) {
            pointer--;
        } else {
            return Optional.empty();
        }

        return Optional.of(history.get(pointer));
    }

    /**
     * Navigate to the next (newer) command. Returns the command to display, or empty when the
     * navigator reaches the end (i.e. command box should be cleared).
     */
    public Optional<String> next() {
        if (pointer >= history.size()) {
            pointer = history.size();
            return Optional.empty();
        }

        if (pointer == history.size() - 1) {
            pointer = history.size();
            return Optional.empty();
        } else {
            pointer++;
            return Optional.of(history.get(pointer));
        }
    }

    /**
     * Returns an immutable snapshot of the current entries used by the navigator.
     */
    public List<String> getEntries() {
        return List.copyOf(history);
    }

    /**
     * Returns the current pointer position (for testing/inspection).
     */
    public int getPointer() {
        return pointer;
    }

}
