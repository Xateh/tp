package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.session.SessionData;

/**
 * Stores session data in the hard disk as a JSON file.
 */
public class JsonSessionStorage implements SessionStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonSessionStorage.class);

    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");

    private static final String INDEX_FILE_NAME = ".session-index";
    private final Path sessionDirectory;

    public JsonSessionStorage(Path sessionDirectory) {
        this.sessionDirectory = requireNonNull(sessionDirectory);
    }

    @Override
    public Path getSessionDirectory() {
        return sessionDirectory;
    }

    @Override
    public Optional<SessionData> readSession() throws DataLoadingException {
        if (!Files.exists(sessionDirectory) || !Files.isDirectory(sessionDirectory)) {
            return Optional.empty();
        }

        List<Path> sessionFiles;
        try (Stream<Path> stream = Files.list(sessionDirectory)) {
            sessionFiles = stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DataLoadingException(e);
        }

        SessionData latestSession = null;
        for (Path file : sessionFiles) {
            Optional<JsonSerializableSession> jsonSession = Optional.empty();
            try {
                jsonSession = JsonUtil.readJsonFile(file, JsonSerializableSession.class);
            } catch (DataLoadingException e) {
                logger.warning("Skipping corrupted session file (unreadable JSON) " + file + ": " + e.getMessage());
                continue;
            }
            if (!jsonSession.isPresent()) {
                continue;
            }
            try {
                SessionData candidate = jsonSession.get().toModelType();
                if (latestSession == null || candidate.getSavedAt().isAfter(latestSession.getSavedAt())) {
                    latestSession = candidate;
                }
            } catch (Exception e) {
                logger.warning("Skipping invalid session file (bad data) " + file + ": " + e.getMessage());
            }
        }

        return Optional.ofNullable(latestSession);
    }

    @Override
    public void saveSession(SessionData sessionData) throws IOException {
        requireNonNull(sessionData);
        Files.createDirectories(sessionDirectory);
        // Build a canonical signature of the material persisted payload (formatVersion, addressBook, guiSettings).
        String candidateSignature = computeMaterialSignature(sessionData);

        Path indexPath = sessionDirectory.resolve(INDEX_FILE_NAME);

        // If an index exists, compare signatures and skip write when identical.
        if (Files.exists(indexPath) && Files.isRegularFile(indexPath)) {
            try {
                String existingSignature = Files.readString(indexPath).trim();
                if (!existingSignature.isEmpty() && existingSignature.equals(candidateSignature)) {
                    logger.info("Skipping session save: payload unchanged (index match)");
                    return;
                }
            } catch (IOException e) {
                logger.warning("Could not read session index for dedup comparison: " + e.getMessage());
                // fall through and write
            }
        }

        // Not a no-op: write session and update index.
        JsonSerializableSession candidate = new JsonSerializableSession(sessionData);
        Path target = sessionDirectory.resolve(createFileName(sessionData));
        JsonUtil.saveJsonFile(candidate, target);

        try {
            Files.writeString(indexPath, candidateSignature);
        } catch (IOException e) {
            logger.warning("Failed to update session index: " + e.getMessage());
        }
    }

    private String computeMaterialSignature(SessionData sessionData) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();

            com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
            if (sessionData.getFormatVersion() != null) {
                root.put("formatVersion", sessionData.getFormatVersion());
            } else {
                root.putNull("formatVersion");
            }

            // Use the existing JsonSerializableAddressBook to get a stable addressbook JSON shape
            JsonSerializableAddressBook jBook = new JsonSerializableAddressBook(sessionData.getAddressBook());
            // Use JsonUtil to serialize the address book in a consistent way and parse back to a JsonNode
            root.set("addressBook", mapper.readTree(JsonUtil.toJsonString(jBook)));

            // Build guiSettings node deterministically
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

            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // In case of any failure, fall back to a best-effort string so dedup won't erroneously skip
            logger.warning("Failed to compute session signature: " + e.getMessage());
            return String.valueOf(sessionData.hashCode());
        }
    }

    private String createFileName(SessionData sessionData) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zoned = sessionData.getSavedAt().atZone(zoneId);
        String zoneIdComponent = zoneId.getId().replace('/', '-');
        String timestampComponent = zoned.format(FILE_NAME_FORMATTER);
        return String.format("session-%s-%s.json", timestampComponent, zoneIdComponent);
    }
}
