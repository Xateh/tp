package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.history.CommandHistory;

/**
 * Represents a storage for {@link CommandHistory}.
 */
public interface CommandHistoryStorage {

    /**
     * Returns the file path of the command history data file.
     */
    Path getCommandHistoryFilePath();

    /**
     * Returns the command history stored on disk, or {@code Optional.empty()} if the file does not exist.
     *
     * @throws DataLoadingException if the data on disk could not be converted into a {@code CommandHistory}.
     */
    Optional<CommandHistory> readCommandHistory() throws DataLoadingException;

    /**
     * Saves the given {@code CommandHistory} to the configured file path.
     *
     * @throws IOException if the data could not be written to disk.
     */
    void saveCommandHistory(CommandHistory commandHistory) throws IOException;
}
