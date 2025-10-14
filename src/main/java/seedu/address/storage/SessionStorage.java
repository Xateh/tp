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

    Path getSessionFilePath();

    Optional<SessionData> readSession() throws DataLoadingException;

    void saveSession(SessionData sessionData) throws IOException;
}
