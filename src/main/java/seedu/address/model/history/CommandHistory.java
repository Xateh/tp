package seedu.address.model.history;

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Represents a rolling record of commands executed by the user.
 */
public class CommandHistory {

    /** Default maximum number of entries to retain. */
    public static final int DEFAULT_MAX_ENTRIES = 100;

    private final Deque<String> entries;
    private final int maxEntries;

    /**
     * Constructs an empty {@code CommandHistory} that retains up to {@link #DEFAULT_MAX_ENTRIES} entries.
     */
    public CommandHistory() {
        this(Collections.emptyList(), DEFAULT_MAX_ENTRIES);
    }

    /**
     * Constructs a {@code CommandHistory} with the given initial entries and the default maximum size.
     *
     * @param initialEntries commands to seed this history with. Later entries appear later in the list.
     */
    public CommandHistory(List<String> initialEntries) {
        this(initialEntries, DEFAULT_MAX_ENTRIES);
    }

    /**
     * Constructs a {@code CommandHistory} with the given initial entries and maximum size.
     */
    public CommandHistory(List<String> initialEntries, int maxEntries) {
        requireNonNull(initialEntries);
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be greater than 0");
        }

        this.maxEntries = maxEntries;
        this.entries = new ArrayDeque<>(Math.min(initialEntries.size(), maxEntries));
        reset(initialEntries);
    }

    /**
     * Adds a new command to the history.
     * Empty commands (i.e. blank or whitespace-only) are ignored.
     */
    public void add(String commandText) {
        requireNonNull(commandText);
        String trimmed = commandText.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        appendEntry(trimmed);
    }

    /**
     * Replaces the current history contents with {@code newEntries}.
     */
    public void reset(List<String> newEntries) {
        requireNonNull(newEntries);
        entries.clear();
        newEntries.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .forEach(this::appendEntry);
    }

    /**
     * Returns the history entries from oldest to newest.
     */
    public List<String> asUnmodifiableList() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    /**
     * Returns {@code true} if the history has no entries.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns the number of entries tracked.
     */
    public int size() {
        return entries.size();
    }

    private void appendEntry(String entry) {
        if (entries.size() == maxEntries) {
            entries.removeFirst();
        }
        entries.addLast(entry);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof CommandHistory)) {
            return false;
        }

        CommandHistory otherHistory = (CommandHistory) other;
        return asUnmodifiableList().equals(otherHistory.asUnmodifiableList())
                && maxEntries == otherHistory.maxEntries;
    }

    @Override
    public int hashCode() {
        return Objects.hash(asUnmodifiableList(), maxEntries);
    }

    @Override
    public String toString() {
        return "CommandHistory{" + "entries=" + entries + ", maxEntries=" + maxEntries + '}';
    }
}
