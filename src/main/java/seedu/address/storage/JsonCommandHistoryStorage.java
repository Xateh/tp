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
import seedu.address.model.history.CommandHistory;

/**
 * A class to access command history stored as a JSON file on the hard disk.
 */
public class JsonCommandHistoryStorage implements CommandHistoryStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonCommandHistoryStorage.class);

    private final Path filePath;

    public JsonCommandHistoryStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getCommandHistoryFilePath() {
        return filePath;
    }

    @Override
    public Optional<CommandHistory> readCommandHistory() throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableCommandHistory> jsonHistory = JsonUtil.readJsonFile(
                filePath, JsonSerializableCommandHistory.class);
        if (jsonHistory.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonHistory.get().toModelType());
        } catch (IllegalArgumentException e) {
            logger.info("Illegal values found in " + filePath + ": " + e.getMessage());
            throw new DataLoadingException(e);
        }
    }

    @Override
    public void saveCommandHistory(CommandHistory commandHistory) throws IOException {
        requireNonNull(commandHistory);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableCommandHistory(commandHistory), filePath);
    }
}
