package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

class JsonSessionStorageTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(600, 400, 0, 0);

    @TempDir
    Path tempDir;

    @Test
    void readSession_directoryMissing_returnsEmptyOptional() throws DataLoadingException {
        Path sessionDir = tempDir.resolve("missing");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        Optional<SessionData> result = storage.readSession();

        assertFalse(result.isPresent());
    }

    @Test
    void readSession_returnsLatestSession() throws Exception {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData older = createSessionData(
                Instant.parse("2025-10-14T00:00:00Z"), List.of("Alice"), "find Alice");
        SessionData newer = createSessionData(
                Instant.parse("2025-10-15T00:00:00Z"), List.of("Bob"), "find Bob");

        storage.saveSession(older);
        storage.saveSession(newer);

        Optional<SessionData> result = storage.readSession();

        assertTrue(result.isPresent());
        assertEquals(newer.getSavedAt(), result.get().getSavedAt());
        assertEquals(List.of("Bob"), result.get().getSearchKeywords());
    }

    @Test
    void readSession_skipsInvalidSessionFiles() throws Exception {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        Files.createDirectories(sessionDir);
        Path invalidFile = sessionDir.resolve("session-invalid.json");
        Files.writeString(invalidFile, "{\"savedAt\":null,\"formatVersion\":\"1.0\"}");

        SessionData valid = createSessionData(
                Instant.parse("2025-10-16T00:00:00Z"), List.of("Carl"), "find Carl");
        storage.saveSession(valid);

        Optional<SessionData> result = storage.readSession();

        assertTrue(result.isPresent());
        assertEquals(List.of("Carl"), result.get().getSearchKeywords());
    }

    @Test
    void saveSession_createsDirectoryAndFile() throws IOException {
        Path sessionDir = tempDir.resolve("nested").resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        storage.saveSession(createSessionData(
                Instant.parse("2025-10-17T00:00:00Z"), List.of(), "list"));

        assertTrue(Files.exists(sessionDir));
        long fileCount;
        try (var stream = Files.list(sessionDir)) {
            fileCount = stream.count();
        }
        assertEquals(1, fileCount);
    }

    @Test
    void saveSession_generatesTimestampedFileName() throws IOException {
        Path sessionDir = tempDir.resolve("timestamped");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData sessionData = createSessionData(
                Instant.parse("2025-10-18T12:34:56Z"), List.of("Alice"), "find Alice");
        storage.saveSession(sessionData);

        try (var stream = Files.list(sessionDir)) {
            String zoneComponent = ZoneId.systemDefault().getId().replace('/', '-');
            String fileName = stream.findFirst().orElseThrow().getFileName().toString();
            assertTrue(fileName.matches("session-\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}-\\d{3}-.*\\.json"));
            assertTrue(fileName.contains(zoneComponent));
        }
    }

    @Test
    void readSession_corruptedJson_throwsDataLoadingException() throws IOException {
        Path sessionDir = tempDir.resolve("corrupted");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        Files.createDirectories(sessionDir);
        Files.writeString(sessionDir.resolve("session-corrupt.json"), "{ invalid json");

        assertThrows(DataLoadingException.class, storage::readSession);
    }

    private SessionData createSessionData(Instant savedAt, List<String> keywords, String commandText) {
        Path addressBookPath = tempDir.resolve("addressBook.json");
        List<SessionCommand> commands = commandText == null
                ? List.of()
                : List.of(new SessionCommand(savedAt, commandText));
        return new SessionData(savedAt, addressBookPath, keywords, commands, GUI_SETTINGS);
    }
}
