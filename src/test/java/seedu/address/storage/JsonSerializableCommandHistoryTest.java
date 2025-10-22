package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.history.CommandHistory;

class JsonSerializableCommandHistoryTest {

    @Test
    void toModelType_nullCommands_returnsEmptyHistory() {
        JsonSerializableCommandHistory jsonHistory = new JsonSerializableCommandHistory((List<String>) null);
        CommandHistory history = jsonHistory.toModelType();
        assertTrue(history.isEmpty());
    }

    @Test
    void toModelType_filtersInvalidEntries() {
        JsonSerializableCommandHistory jsonHistory =
                new JsonSerializableCommandHistory(Arrays.asList(" list ", " ", null, "add n/Bob"));
        CommandHistory history = jsonHistory.toModelType();
        assertEquals(List.of("list", "add n/Bob"), history.getEntries());
    }

    @Test
    void constructor_fromCommandHistory_preservesEntries() {
        CommandHistory original = new CommandHistory();
        original.add("list");
        original.add("add n/Amy");

        JsonSerializableCommandHistory jsonHistory = new JsonSerializableCommandHistory(original);
        CommandHistory restored = jsonHistory.toModelType();

        assertEquals(original, restored);
    }
}
