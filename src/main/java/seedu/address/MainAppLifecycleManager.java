package seedu.address;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.session.SessionData;
import seedu.address.storage.CommandHistoryStorage;
import seedu.address.storage.JsonCommandHistoryStorage;
import seedu.address.storage.JsonSessionStorage;
import seedu.address.storage.SessionStorage;
import seedu.address.storage.Storage;

/**
 * Encapsulates the lifecycle-specific behaviour that {@link MainApp} cannot easily test in isolation.
 */
public class MainAppLifecycleManager {

    private final Logger logger;

    /**
     * Constructs a lifecycle manager which delegates lifecycle-related responsibilities.
     *
     * @param logger logger to use for diagnostic messages
     */
    public MainAppLifecycleManager(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    /**
     * Creates a {@link CommandHistoryStorage} instance that will persist the command history
     * to the given {@code commandHistoryPath}.
     *
     * @param commandHistoryPath path to the command history file
     * @return a new {@link CommandHistoryStorage}
     */
    public CommandHistoryStorage createCommandHistoryStorage(Path commandHistoryPath) {
        requireNonNull(commandHistoryPath);
        return new JsonCommandHistoryStorage(commandHistoryPath);
    }

    /**
     * Creates a {@link SessionStorage} instance using a session directory derived from
     * the provided {@code addressBookPath}.
     *
     * @param addressBookPath path to the address book file used to derive the sessions folder
     * @return a new {@link SessionStorage}
     */
    public SessionStorage createSessionStorage(Path addressBookPath) {
        requireNonNull(addressBookPath);
        return new JsonSessionStorage(deriveSessionDirectory(addressBookPath));
    }

    /**
     * Derives the session directory for the provided address book path. If the address book
     * has no parent directory, a `sessions` directory in the current working directory is
     * returned.
     *
     * @param addressBookPath path to the address book file
     * @return path to the sessions directory
     */
    Path deriveSessionDirectory(Path addressBookPath) {
        Path parent = addressBookPath.getParent();
        if (parent == null) {
            return Path.of("sessions");
        }
        return parent.resolve("sessions");
    }

    /**
     * Attempts to load the most recent session snapshot from {@code storage}. Any
     * {@link DataLoadingException} is caught and logged and an empty optional is returned.
     *
     * @param storage storage instance to read session data from
     * @return optional session data if present and readable
     */
    public Optional<SessionData> loadSession(Storage storage) {
        requireNonNull(storage);
        try {
            return storage.readSession();
        } catch (DataLoadingException e) {
            logger.warning("Session files could not be read. Starting without restoring session. "
                    + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Initializes the {@link Model} using either the provided {@code restoredSession} snapshot
     * (preferred) or by reading from {@code storage}. Any data loading exceptions are logged
     * and an empty address book will be used as a fallback.
     *
     * @param storage storage layer to read persisted data from
     * @param userPrefs user preferences to seed the model
     * @param restoredSession optional restored session to initialize model state from
     * @return initialized {@link Model}
     */
    public Model initModel(Storage storage, ReadOnlyUserPrefs userPrefs, Optional<SessionData> restoredSession) {
        requireNonNull(storage);
        requireNonNull(userPrefs);
        requireNonNull(restoredSession);

        logger.info("Using data file : " + storage.getAddressBookFilePath());

        ReadOnlyAddressBook initialData;
        if (restoredSession.isPresent()) {
            SessionData sessionData = restoredSession.get();
            logger.info("Restoring AddressBook from session snapshot saved at " + sessionData.getSavedAt());
            initialData = sessionData.getAddressBook();
        } else {
            initialData = loadAddressBookFromStorage(storage);
        }

        return new ModelManager(initialData, userPrefs);
    }

    private ReadOnlyAddressBook loadAddressBookFromStorage(Storage storage) {
        Optional<ReadOnlyAddressBook> addressBookOptional;
        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            return addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            return new AddressBook();
        }
    }

    /**
     * Persists command history and the current session snapshot (if dirty) to {@code storage}.
    * If saving the command history fails the method will still attempt to persist the session
    * snapshot. Any IO errors are logged but not thrown.
    *
    * Note: Previously this method wrote session snapshots only when the address book had
    * changed. It now uses {@link Logic#getSessionSnapshotIfAnyDirty()} so snapshots will also be
    * written when session metadata (search filter or GUI settings) have changed. Actual
    * persistence still only occurs on shutdown via this method.
     *
     * @param storage storage to persist data to
     * @param logic logic component providing snapshots
     */
    public void persistOnStop(Storage storage, Logic logic) {
        requireNonNull(storage);
        requireNonNull(logic);

        try {
            storage.saveCommandHistory(logic.getCommandHistorySnapshot());
        } catch (IOException e) {
            logger.severe("Failed to save command history " + StringUtil.getDetails(e));
        }

        Optional<SessionData> sessionSnapshot = logic.getSessionSnapshotIfAnyDirty();
        if (sessionSnapshot.isEmpty()) {
            return;
        }

        try {
            storage.saveSession(sessionSnapshot.get());
            logic.markSessionSnapshotPersisted();
        } catch (IOException e) {
            logger.severe("Failed to save session " + StringUtil.getDetails(e));
        }
    }
}
