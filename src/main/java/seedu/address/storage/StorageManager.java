package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.history.CommandHistory;
import seedu.address.session.SessionData;
import seedu.address.session.SessionDirectoryResolver;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private final AddressBookStorage addressBookStorage;
    private final UserPrefsStorage userPrefsStorage;
    private final CommandHistoryStorage commandHistoryStorage;
    private final SessionStorage sessionStorage;

    /**
     * Creates a {@code StorageManager} with the given storages.
     */
    public StorageManager(AddressBookStorage addressBookStorage, UserPrefsStorage userPrefsStorage,
            CommandHistoryStorage commandHistoryStorage, SessionStorage sessionStorage) {
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.commandHistoryStorage = commandHistoryStorage;
        this.sessionStorage = sessionStorage;
    }

    /**
     * Creates a {@code StorageManager} using {@code config} and {@code userPrefs} to configure storage paths.
     */
    public StorageManager(Config config, ReadOnlyUserPrefs userPrefs) {
        this(createAddressBookStorage(userPrefs),
                createUserPrefsStorage(config),
                createCommandHistoryStorage(userPrefs),
                createSessionStorage(userPrefs));
    }

    private static AddressBookStorage createAddressBookStorage(ReadOnlyUserPrefs userPrefs) {
        ReadOnlyUserPrefs prefs = requireNonNull(userPrefs);
        return new JsonAddressBookStorage(prefs.getAddressBookFilePath());
    }

    private static UserPrefsStorage createUserPrefsStorage(Config config) {
        Config cfg = requireNonNull(config);
        return new JsonUserPrefsStorage(cfg.getUserPrefsFilePath());
    }

    private static CommandHistoryStorage createCommandHistoryStorage(ReadOnlyUserPrefs userPrefs) {
        ReadOnlyUserPrefs prefs = requireNonNull(userPrefs);
        return new JsonCommandHistoryStorage(prefs.getCommandHistoryFilePath());
    }

    private static SessionStorage createSessionStorage(ReadOnlyUserPrefs userPrefs) {
        ReadOnlyUserPrefs prefs = requireNonNull(userPrefs);
        return new JsonSessionStorage(SessionDirectoryResolver.resolve(prefs.getAddressBookFilePath()));
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    // ================ CommandHistory methods ==========================

    @Override
    public Path getCommandHistoryFilePath() {
        return commandHistoryStorage.getCommandHistoryFilePath();
    }

    @Override
    public Optional<CommandHistory> readCommandHistory()
            throws DataLoadingException {
        logger.fine("Attempting to read command history from file: " + getCommandHistoryFilePath());
        return commandHistoryStorage.readCommandHistory();
    }

    @Override
    public void saveCommandHistory(CommandHistory commandHistory) throws IOException {
        logger.fine("Attempting to write command history to data file: " + getCommandHistoryFilePath());
        commandHistoryStorage.saveCommandHistory(commandHistory);
    }

    // ================ Session methods ==============================

    @Override
    public Optional<SessionData> readSession() throws DataLoadingException {
        return sessionStorage.readSession();
    }

    @Override
    public void saveSession(SessionData sessionData) throws IOException {
        sessionStorage.saveSession(sessionData);
    }

    @Override
    public Path getSessionDirectory() {
        return sessionStorage.getSessionDirectory();
    }

}
