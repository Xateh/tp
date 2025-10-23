package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.history.CommandHistory;

class JsonCommandHistoryStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void readCommandHistory_missingFile_returnsEmptyOptional() throws DataLoadingException {
        Path filePath = tempDir.resolve("history.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);

        Optional<CommandHistory> result = storage.readCommandHistory();

        assertTrue(result.isEmpty());
    }

    @Test
    void readCommandHistory_invalidJson_throwsDataLoadingException() throws IOException {
        Path filePath = tempDir.resolve("history.json");
        Files.writeString(filePath, "not valid json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);

        assertThrows(DataLoadingException.class, storage::readCommandHistory);
    }

    @Test
    void readCommandHistory_validFile_returnsCommandHistory() throws Exception {
        Path filePath = tempDir.resolve("history.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);
        CommandHistory expected = createHistory("list", "add n/Amy");

        storage.saveCommandHistory(expected);
        Optional<CommandHistory> result = storage.readCommandHistory();

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void saveCommandHistory_nullHistory_throwsNullPointerException() {
        Path filePath = tempDir.resolve("history.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);

        assertThrows(NullPointerException.class, () -> storage.saveCommandHistory(null));
    }

    @Test
    void saveCommandHistory_createsMissingParents() throws Exception {
        Path filePath = tempDir.resolve("nested").resolve("history").resolve("commandHistory.json");
        JsonCommandHistoryStorage storage = new JsonCommandHistoryStorage(filePath);

        storage.saveCommandHistory(createHistory("list"));

        assertTrue(Files.exists(filePath));
    }

    private CommandHistory createHistory(String... commands) {
        CommandHistory history = new CommandHistory();
        for (String command : commands) {
            history.add(command);
        }
        return history;
    }
}
