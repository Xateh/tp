package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.session.SessionData;

/**
 * Defines the API for persisting session information.
 */
public interface SessionStorage {

    /** Returns the directory where session snapshots are stored. */
    Path getSessionDirectory();

    /** Reads the session snapshot with the latest timestamp, if any. */
    Optional<SessionData> readSession() throws DataLoadingException;

    /** Persists a new session snapshot. */
    void saveSession(SessionData sessionData) throws IOException;
}
