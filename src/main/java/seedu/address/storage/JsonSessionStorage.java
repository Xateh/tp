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
        // Defensive dedup: avoid writing a new session file when the persisted payload
        // (excluding the savedAt timestamp) is identical to the latest persisted file.
        JsonSerializableSession candidate = new JsonSerializableSession(sessionData);

        // Find the most recently-modified JSON file in the session directory, if any.
        Optional<Path> latest = Optional.empty();
        try (Stream<Path> stream = Files.list(sessionDirectory)) {
            latest = stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .max((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b));
                        } catch (IOException e) {
                            return 0;
                        }
                    });
        }

        if (latest.isPresent()) {
            try {
                Optional<JsonSerializableSession> existingOpt = JsonUtil.readJsonFile(latest.get(),
                        JsonSerializableSession.class);
                if (existingOpt.isPresent()) {
                    JsonSerializableSession existing = existingOpt.get();

                    // Compare the material parts of the session (formatVersion, addressBook, guiSettings)
                    // and skip writing if they are identical to the latest persisted snapshot.
                    try {
                        SessionData existingSession = existing.toModelType();
                        String candidateFormat = sessionData.getFormatVersion();
                        String existingFormat = existingSession.getFormatVersion();
                        boolean sameFormat = (candidateFormat == null) ? (existingFormat == null)
                                : candidateFormat.equals(existingFormat);
                        if (sameFormat
                                && sessionData.getAddressBook().equals(existingSession.getAddressBook())
                                && sessionData.getGuiSettings().equals(existingSession.getGuiSettings())) {
                            logger.info("Skipping session save: payload unchanged (ignoring savedAt)");
                            return;
                        }
                    } catch (seedu.address.commons.exceptions.IllegalValueException ive) {
                        // If the existing file cannot be converted to model types, fall back to writing.
                        logger.warning("Existing session file invalid for comparison: " + ive.getMessage());
                    }
                }
            } catch (seedu.address.commons.exceptions.DataLoadingException e) {
                logger.warning("Could not read latest session file for dedup comparison: " + e.getMessage());
            } catch (Exception e) {
                logger.warning("Unexpected error when comparing session payloads: " + e.getMessage());
            }
        }

        Path target = sessionDirectory.resolve(createFileName(sessionData));
        JsonUtil.saveJsonFile(candidate, target);
    }

    private String createFileName(SessionData sessionData) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zoned = sessionData.getSavedAt().atZone(zoneId);
        String zoneIdComponent = zoneId.getId().replace('/', '-');
        String timestampComponent = zoned.format(FILE_NAME_FORMATTER);
        return String.format("session-%s-%s.json", timestampComponent, zoneIdComponent);
    }
}
