package seedu.address.app;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.Config;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.UserPrefsStorage;

/**
 * Loads user preferences, creating or repairing the underlying file when necessary.
 */
public class MainAppUserPrefsLoader {

    private final Logger logger;

    /**
     * Creates a loader that will emit diagnostic messages via the provided {@code logger}.
     */
    public MainAppUserPrefsLoader(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    /**
     * Loads {@link UserPrefs} using the path configured in {@code config}. When the backing file is missing or
     * malformed the default preferences are returned and persisted.
     */
    public UserPrefs load(Config config) {
        requireNonNull(config);

        UserPrefsStorage storage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }
}
