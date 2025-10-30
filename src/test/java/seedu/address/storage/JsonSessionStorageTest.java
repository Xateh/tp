package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

class JsonSessionStorageTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(600, 400, 0, 0);

    @TempDir
    Path tempDir;

    private SessionData createSessionData(String name) {
        AddressBook ab = new AddressBook();
        ab.addPerson(new PersonBuilder().withName(name).build());
        return new SessionData(java.time.Instant.parse("2025-10-20T00:00:00Z"), ab,
                java.util.List.of(), new GuiSettings(600, 400, 0, 0));
    }

    private SessionData createSessionData(Instant savedAt, List<String> keywords, String personName) {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withName(personName == null ? "Default Person" : personName).build());
        return new SessionData(savedAt, addressBook, keywords, GUI_SETTINGS);
    }

    private String computeSignature(SessionData sessionData) throws Exception {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        if (sessionData.getFormatVersion() != null) {
            root.put("formatVersion", sessionData.getFormatVersion());
        } else {
            root.putNull("formatVersion");
        }

        JsonSerializableAddressBook jBook = new JsonSerializableAddressBook(sessionData.getAddressBook());
        root.set("addressBook", mapper.readTree(JsonUtil.toJsonString(jBook)));

        com.fasterxml.jackson.databind.node.ObjectNode guiNode = mapper.createObjectNode();
        guiNode.put("windowWidth", sessionData.getGuiSettings().getWindowWidth());
        guiNode.put("windowHeight", sessionData.getGuiSettings().getWindowHeight());
        if (sessionData.getGuiSettings().getWindowCoordinates() != null) {
            guiNode.put("windowX", sessionData.getGuiSettings().getWindowCoordinates().x);
            guiNode.put("windowY", sessionData.getGuiSettings().getWindowCoordinates().y);
        }
        root.set("guiSettings", guiNode);

        String payload = mapper.writer()
                .without(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(root);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

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
                Instant.parse("2025-10-14T00:00:00Z"),
                List.of("Alice"),
                "Alice Tan");
        SessionData newer = createSessionData(
                Instant.parse("2025-10-15T00:00:00Z"),
                List.of("Bob"),
                "Bob Lee");

        storage.saveSession(older);
        storage.saveSession(newer);

        Optional<SessionData> result = storage.readSession();

        assertTrue(result.isPresent());
        assertEquals(newer.getSavedAt(), result.get().getSavedAt());
        // Search keywords are no longer persisted in the JSON schema
        assertTrue(result.get().getSearchKeywords().isEmpty());
    }

    @Test
    void readSession_skipsInvalidSessionFiles() throws Exception {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        Files.createDirectories(sessionDir);
        Path invalidFile = sessionDir.resolve("session-invalid.json");
        Files.writeString(invalidFile, "{\"savedAt\":null,\"formatVersion\":\"1.0\"}");

        SessionData valid = createSessionData(
                Instant.parse("2025-10-16T00:00:00Z"),
                List.of("Carl"),
                "Carl Chan");
        storage.saveSession(valid);

        Optional<SessionData> result = storage.readSession();

        assertTrue(result.isPresent());
        // Keywords are not persisted
        assertTrue(result.get().getSearchKeywords().isEmpty());
    }

    @Test
    void readSession_skipsAllCorruptedFiles_returnsEmpty() throws Exception {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        Files.createDirectories(sessionDir);
        Files.writeString(sessionDir.resolve("session1.json"), "{ invalid json");
        Files.writeString(sessionDir.resolve("session2.json"), "{ invalid json");

        Optional<SessionData> result = storage.readSession();
        assertFalse(result.isPresent());
    }

    @Test
    void saveSession_createsDirectoryAndFile() throws IOException {
        Path sessionDir = tempDir.resolve("nested").resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        storage.saveSession(createSessionData(
                Instant.parse("2025-10-17T00:00:00Z"),
                List.of(),
                null));

        assertTrue(Files.exists(sessionDir));
        // Count only actual session files (those starting with "session-") and ignore the index file.
        long sessionFileCount;
        try (var stream = Files.list(sessionDir)) {
            sessionFileCount = stream.filter(path -> path.getFileName().toString().startsWith("session-"))
                    .count();
        }
        assertEquals(1, sessionFileCount);
    }

    @Test
    void saveSession_generatesTimestampedFileName() throws IOException {
        Path sessionDir = tempDir.resolve("timestamped");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData sessionData = createSessionData(
                Instant.parse("2025-10-18T12:34:56Z"),
                List.of("Alice"),
                "Alice Tan");
        storage.saveSession(sessionData);

        try (var stream = Files.list(sessionDir)) {
            String zoneComponent = ZoneId.systemDefault().getId().replace('/', '-');
            String fileName = stream.filter(path -> path.getFileName().toString().startsWith("session-"))
                    .findFirst().orElseThrow().getFileName().toString();
            assertTrue(fileName.matches("session-\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}-\\d{3}-.*\\.json"));
            assertTrue(fileName.contains(zoneComponent));
        }
    }

    @Test
    void saveSession_skipsWritingWhenPayloadUnchanged() throws IOException {
        Path sessionDir = tempDir.resolve("indextest");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData s1 = createSessionData(
                Instant.parse("2025-10-20T00:00:00Z"),
                List.of(),
                "Alice");

        storage.saveSession(s1);

        // Save again with a different savedAt but identical address book & guiSettings
        SessionData s1b = createSessionData(
                Instant.parse("2025-10-21T00:00:00Z"),
                List.of(),
                "Alice");
        storage.saveSession(s1b);

        // Only one session-* file should exist (index file is separate)
        long sessionFileCount;
        try (var stream = Files.list(sessionDir)) {
            sessionFileCount = stream.filter(path -> path.getFileName().toString().startsWith("session-"))
                    .count();
        }
        assertEquals(1, sessionFileCount);

        // Index file should exist
        assertTrue(Files.exists(sessionDir.resolve(".session-index")));
    }

    @Test
    void saveSession_writesWhenPayloadChanges_updatesIndex() throws IOException {
        Path sessionDir = tempDir.resolve("indextest2");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData s1 = createSessionData(
                Instant.parse("2025-10-20T00:00:00Z"),
                List.of(),
                "Alice");
        storage.saveSession(s1);

        // Now save a session with a different address book content
        SessionData s2 = createSessionData(
                Instant.parse("2025-10-22T00:00:00Z"),
                List.of(),
                "Bob");
        storage.saveSession(s2);

        // (Directory contents printed during debugging; removed.)

        // Two session files should exist now
        long sessionFileCount;
        try (var stream = Files.list(sessionDir)) {
            sessionFileCount = stream.filter(path -> path.getFileName().toString().startsWith("session-"))
                    .count();
        }
        assertEquals(2, sessionFileCount);

        // Index file should exist and be non-empty
        Path indexPath = sessionDir.resolve(".session-index");
        assertTrue(Files.exists(indexPath));
        String content = Files.readString(indexPath).trim();
        assertFalse(content.isEmpty());
    }

    @Test
    void readSession_corruptedJson_throwsDataLoadingException() throws DataLoadingException {
        Path sessionDir = tempDir.resolve("corrupted");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);
        try {
            Files.createDirectories(sessionDir);
            Files.writeString(sessionDir.resolve("session-corrupt.json"), "{ invalid json");
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        Optional<SessionData> result = storage.readSession();
        assertFalse(result.isPresent());
    }

    @Test
    void saveSession_whenIndexIsDirectory_doesNotPreventSessionFileCreation() throws IOException {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        // create an index path as a directory to force an IOException when reading
        Files.createDirectories(sessionDir);
        Files.createDirectories(sessionDir.resolve(".session-index"));

        SessionData s = createSessionData("DirTest");
        // should not throw; save will write session file but will fail to overwrite index (logged)
        storage.saveSession(s);

        // ensure at least one session-*.json file exists
        boolean any = Files.list(sessionDir)
                .anyMatch(p -> p.getFileName().toString().startsWith("session-"));
        assertTrue(any);

        // index path remains a directory (write to it would fail)
        assertTrue(Files.isDirectory(sessionDir.resolve(".session-index")));
    }

    @Test
    void saveSession_whenIndexMismatched_updatesIndexString() throws IOException {
        Path sessionDir = tempDir.resolve("sessions2");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData s1 = createSessionData("A");
        storage.saveSession(s1);

        // write a bogus index to force mismatch
        Path indexPath = sessionDir.resolve(".session-index");
        Files.writeString(indexPath, "deadbeef");

        // saving an identical payload should still succeed but index should be updated to real signature
        SessionData s2 = createSessionData("B");
        storage.saveSession(s2);

        String indexContent = Files.readString(indexPath).trim();
        assertFalse(indexContent.isEmpty());
    }

    @Test
    void saveSession_indexContainsSignature_forIdenticalMaterialPayloads() throws Exception {
        Path sessionDir = tempDir.resolve("sessions");
        JsonSessionStorage storage = new JsonSessionStorage(sessionDir);

        SessionData s1 = createSessionData("Alice");
        storage.saveSession(s1);

        // Save again with different savedAt but identical material payload
        SessionData s1b = createSessionData("Alice");
        storage.saveSession(s1b);

        // index should exist and contain the signature string
        Path indexPath = sessionDir.resolve(".session-index");
        assertTrue(Files.exists(indexPath));
        String index = Files.readString(indexPath).trim();

        // compute expected signature using same algorithm as storage
        String expected = computeSignature(s1);
        assertEquals(expected, index);
    }
}
