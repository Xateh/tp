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
        Path target = sessionDirectory.resolve(createFileName(sessionData));
        JsonUtil.saveJsonFile(new JsonSerializableSession(sessionData), target);
    }

    private String createFileName(SessionData sessionData) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zoned = sessionData.getSavedAt().atZone(zoneId);
        String zoneIdComponent = zoneId.getId().replace('/', '-');
        String timestampComponent = zoned.format(FILE_NAME_FORMATTER);
        return String.format("session-%s-%s.json", timestampComponent, zoneIdComponent);
    }
}
