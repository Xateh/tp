package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.session.SessionData;

/**
 * Stores session data in the hard disk as a JSON file.
 */
public class JsonSessionStorage implements SessionStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonSessionStorage.class);

    private final Path sessionFilePath;

    public JsonSessionStorage(Path sessionFilePath) {
        this.sessionFilePath = requireNonNull(sessionFilePath);
    }

    @Override
    public Path getSessionFilePath() {
        return sessionFilePath;
    }

    @Override
    public Optional<SessionData> readSession() throws DataLoadingException {
        return readSession(sessionFilePath);
    }

    public Optional<SessionData> readSession(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableSession> jsonSession =
                JsonUtil.readJsonFile(filePath, JsonSerializableSession.class);
        if (!jsonSession.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonSession.get().toModelType());
        } catch (Exception e) {
            logger.warning("Failed to convert session file to model: " + e.getMessage());
            throw new DataLoadingException(e);
        }
    }

    @Override
    public void saveSession(SessionData sessionData) throws IOException {
        saveSession(sessionData, sessionFilePath);
    }

    public void saveSession(SessionData sessionData, Path filePath) throws IOException {
        requireNonNull(sessionData);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableSession(sessionData), filePath);
    }
}
