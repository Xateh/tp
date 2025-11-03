package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.exceptions.AssemblyException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.history.CommandHistory;
import seedu.address.model.person.Person;
import seedu.address.session.SessionData;
import seedu.address.storage.CommandHistoryStorage;
import seedu.address.storage.JsonCommandHistoryStorage;
import seedu.address.storage.Storage;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for {@link MainAppLifecycleManager}.
 */
class MainAppLifecycleManagerTest {

    private static final Logger TEST_LOGGER = LogsCenter.getLogger(MainAppLifecycleManagerTest.class);
    private final MainAppLifecycleManager lifecycleManager = new MainAppLifecycleManager(TEST_LOGGER);

    @Test
    void deriveSessionDirectory_withParent_returnsSiblingSessions() {
        Path addressBookPath = Path.of("data", "addressbook.json");
        Path expected = Path.of("data", "sessions");
        assertEquals(expected, lifecycleManager.deriveSessionDirectory(addressBookPath));
    }

    private static class RecordingLogicMetadataOnlyStub implements Logic {
        private final CommandHistory history;
        private final Optional<SessionData> snapshot;
        private boolean sessionMarkedPersisted;

        RecordingLogicMetadataOnlyStub(CommandHistory history, Optional<SessionData> snapshot) {
            this.history = history;
            this.snapshot = snapshot;
        }

        @Override
        public void markAddressBookDirty() {
        }

        @Override
        public CommandHistory getCommandHistorySnapshot() {
            return history;
        }

        @Override
        public Optional<SessionData> getSessionSnapshotIfDirty() {
            // Simulate address-book-only dirty = false
            return Optional.empty();
        }

        @Override
        public Optional<SessionData> getSessionSnapshotIfAnyDirty() {
            return snapshot;
        }

        @Override
        public void markSessionSnapshotPersisted() {
            sessionMarkedPersisted = true;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, AssemblyException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void deriveSessionDirectory_withoutParent_returnsDefaultSessionsDirectory() {
        Path addressBookPath = Path.of("addressbook.json");
        assertEquals(Path.of("sessions"), lifecycleManager.deriveSessionDirectory(addressBookPath));
    }

    @Test
    void createSessionStorage_returnsStorageWithDerivedDirectory() {
        Path addressBookPath = Path.of("data", "addressbook.json");
        assertEquals(Path.of("data", "sessions"),
                lifecycleManager.createSessionStorage(addressBookPath).getSessionDirectory());
    }

    @Test
    void loadSession_successfulRead_returnsSession() {
        SessionData expected = sampleSession();
        Storage storage = new SessionStorageStub(Optional.of(expected));

        Optional<SessionData> restored = lifecycleManager.loadSession(storage);
        assertTrue(restored.isPresent());
        assertEquals(expected, restored.get());
    }

    @Test
    void loadSession_dataLoadingException_returnsEmptyOptional() {
        Storage storage = new SessionStorageThrowsStub();
        Optional<SessionData> restored = lifecycleManager.loadSession(storage);
        assertFalse(restored.isPresent());
    }

    @Test
    void initModel_withRestoredSession_usesSessionAddressBook() {
        AddressBook sessionBook = new AddressBook();
        sessionBook.addPerson(TypicalPersons.ALICE);
        SessionData sessionData = new SessionData(Instant.now(), sessionBook, List.of(), new GuiSettings());

        Storage storage = new StorageAvoidingReadStub();
        Model model = lifecycleManager.initModel(storage, new UserPrefs(), Optional.of(sessionData));
        assertEquals(sessionBook, model.getAddressBook());
    }

    @Test
    void initModel_withoutSession_usesStorageAddressBookWhenPresent() {
        AddressBook stored = new AddressBook();
        stored.addPerson(TypicalPersons.BENSON);
        Storage storage = new AddressBookStorageStub(Optional.of(stored));

        Model model = lifecycleManager.initModel(storage, new UserPrefs(), Optional.empty());
        assertEquals(stored, model.getAddressBook());
    }

    @Test
    void initModel_withoutSession_usesSampleData() {
        Storage storage = new AddressBookStorageStub(Optional.empty());

        Model model = lifecycleManager.initModel(storage, new UserPrefs(), Optional.empty());
        assertEquals(seedu.address.model.util.SampleDataUtil.getSampleAddressBook(), model.getAddressBook());
    }

    @Test
    void initModel_storageThrows_returnsEmptyAddressBook() {
        Storage storage = new AddressBookStorageThrowsStub();
        Model model = lifecycleManager.initModel(storage, new UserPrefs(), Optional.empty());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    void persistOnStop_dirtySessionAndCommandHistory_savesBothAndMarksPersisted() throws Exception {
        SessionData expectedSession = sampleSession();
        RecordingLogicStub logic = new RecordingLogicStub(new CommandHistory(List.of("help")),
                Optional.of(expectedSession));
        RecordingStorageStub storage = new RecordingStorageStub();

        lifecycleManager.persistOnStop(storage, logic);

        assertNotNull(storage.getSavedHistory());
        assertEquals(List.of("help"), storage.getSavedHistory().getEntries());
        assertEquals(expectedSession, storage.getSavedSession());
        assertTrue(logic.sessionMarkedPersisted);
    }

    @Test
    void persistOnStop_notDirtySession_onlySavesCommandHistory() throws Exception {
        RecordingLogicStub logic = new RecordingLogicStub(new CommandHistory(List.of("list")), Optional.empty());
        RecordingStorageStub storage = new RecordingStorageStub();

        lifecycleManager.persistOnStop(storage, logic);

        assertNotNull(storage.getSavedHistory());
        assertEquals(List.of("list"), storage.getSavedHistory().getEntries());
        assertNull(storage.getSavedSession());
        assertFalse(logic.sessionMarkedPersisted);
    }

    @Test
    void persistOnStop_sessionSaveFails_doesNotMarkPersisted() throws Exception {
        RecordingLogicStub logic = new RecordingLogicStub(new CommandHistory(), Optional.of(sampleSession()));
        RecordingStorageStub storage = new RecordingStorageStub();
        storage.setSessionException(new IOException("boom"));

        lifecycleManager.persistOnStop(storage, logic);

        assertFalse(logic.sessionMarkedPersisted);
    }

    @Test
    void persistOnStop_commandHistorySaveFails_stillAttemptsSessionPersistence() throws Exception {
        SessionData expectedSession = sampleSession();
        RecordingLogicStub logic = new RecordingLogicStub(new CommandHistory(List.of("add")),
                Optional.of(expectedSession));
        RecordingStorageStub storage = new RecordingStorageStub();
        storage.setCommandHistoryException(new IOException("fail"));

        lifecycleManager.persistOnStop(storage, logic);

        assertEquals(expectedSession, storage.getSavedSession());
    }

    @Test
    void persistOnStop_metadataOnlySession_savedOnStop() throws Exception {
        // simulate a logic implementation where address-book dirty check is false
        // but metadata-only snapshot is present (e.g., search keywords or GUI changes)
        SessionData expectedSession = sampleSession();
        RecordingLogicMetadataOnlyStub logic = new RecordingLogicMetadataOnlyStub(
                new CommandHistory(List.of("meta")), Optional.of(expectedSession));
        RecordingStorageStub storage = new RecordingStorageStub();

        lifecycleManager.persistOnStop(storage, logic);

        assertEquals(expectedSession, storage.getSavedSession());
        assertTrue(logic.sessionMarkedPersisted);
    }

    @Test
    void createCommandHistoryStorage_returnsJsonCommandHistoryStorage() {
        Path p = Path.of("data", "commandhistory.json");
        CommandHistoryStorage storage = lifecycleManager.createCommandHistoryStorage(p);
        assertTrue(storage instanceof JsonCommandHistoryStorage);
        assertEquals(p, storage.getCommandHistoryFilePath());
    }

    private SessionData sampleSession() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(TypicalPersons.CARL);
        return new SessionData(Instant.now(), addressBook, List.of("Carl"), new GuiSettings());
    }

    private abstract static class BaseStorageStub implements Storage {
        @Override
        public Path getUserPrefsFilePath() {
            return Path.of("prefs.json");
        }

        @Override
        public Optional<seedu.address.model.UserPrefs> readUserPrefs() throws DataLoadingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            return Path.of("data", "addressbook.json");
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getCommandHistoryFilePath() {
            return Path.of("data", "commandhistory.json");
        }

        @Override
        public Optional<CommandHistory> readCommandHistory() throws DataLoadingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveCommandHistory(CommandHistory commandHistory) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SessionData> readSession() throws DataLoadingException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveSession(SessionData sessionData) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getSessionDirectory() {
            return Path.of("data", "sessions");
        }
    }

    private static class SessionStorageStub extends BaseStorageStub {
        private final Optional<SessionData> session;

        SessionStorageStub(Optional<SessionData> session) {
            this.session = session;
        }

        @Override
        public Optional<SessionData> readSession() {
            return session;
        }
    }

    private static class SessionStorageThrowsStub extends BaseStorageStub {
        @Override
        public Optional<SessionData> readSession() throws DataLoadingException {
            throw new DataLoadingException(new IOException("failure"));
        }
    }

    private static class StorageAvoidingReadStub extends BaseStorageStub {
        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() {
            throw new AssertionError("Address book should not be read when session is restored");
        }
    }

    private static class AddressBookStorageStub extends BaseStorageStub {
        private final Optional<ReadOnlyAddressBook> addressBook;

        AddressBookStorageStub(Optional<ReadOnlyAddressBook> addressBook) {
            this.addressBook = addressBook;
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() {
            return addressBook;
        }
    }

    private static class AddressBookStorageThrowsStub extends BaseStorageStub {
        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            throw new DataLoadingException(new IOException("broken"));
        }
    }

    private static class RecordingStorageStub extends BaseStorageStub {
        private CommandHistory savedHistory;
        private SessionData savedSession;
        private IOException commandHistoryException;
        private IOException sessionException;

        CommandHistory getSavedHistory() {
            return savedHistory;
        }

        SessionData getSavedSession() {
            return savedSession;
        }

        void setCommandHistoryException(IOException e) {
            this.commandHistoryException = e;
        }

        void setSessionException(IOException e) {
            this.sessionException = e;
        }

        @Override
        public void saveCommandHistory(CommandHistory commandHistory) throws IOException {
            if (commandHistoryException != null) {
                throw commandHistoryException;
            }
            this.savedHistory = commandHistory;
        }

        @Override
        public void saveSession(SessionData sessionData) throws IOException {
            if (sessionException != null) {
                throw sessionException;
            }
            this.savedSession = sessionData;
        }
    }

    private static class RecordingLogicStub implements Logic {
        private final CommandHistory history;
        private final Optional<SessionData> snapshot;
        private boolean sessionMarkedPersisted;

        RecordingLogicStub(CommandHistory history, Optional<SessionData> snapshot) {
            this.history = history;
            this.snapshot = snapshot;
        }

        @Override
        public void markAddressBookDirty() {
        }

        @Override
        public CommandHistory getCommandHistorySnapshot() {
            return history;
        }

        @Override
        public Optional<SessionData> getSessionSnapshotIfDirty() {
            return snapshot;
        }

        @Override
        public Optional<SessionData> getSessionSnapshotIfAnyDirty() {
            return snapshot;
        }

        @Override
        public void markSessionSnapshotPersisted() {
            sessionMarkedPersisted = true;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, AssemblyException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Model getModel() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    void constructor_nullLogger_throwsNpe() {
        assertThrows(NullPointerException.class, () -> new MainAppLifecycleManager(null));
    }

    @Test
    void createCommandHistoryStorage_nullPath_throwsNpe() {
        assertThrows(NullPointerException.class, () -> lifecycleManager.createCommandHistoryStorage(null));
    }

    @Test
    void createSessionStorage_nullPath_throwsNpe() {
        assertThrows(NullPointerException.class, () -> lifecycleManager.createSessionStorage(null));
    }

    @Test
    void loadSession_nullStorage_throwsNpe() {
        assertThrows(NullPointerException.class, () -> lifecycleManager.loadSession(null));
    }

    @Test
    void initModel_nullArgs_throwsNpe() {
        // null storage
        assertThrows(NullPointerException.class, ()
                -> lifecycleManager.initModel(null, new UserPrefs(), Optional.empty()));

        // create a minimal Storage stub
        Storage stub = new BaseStorageStub() {
        };

        assertThrows(NullPointerException.class, () -> lifecycleManager.initModel(stub, null, Optional.empty()));
        // null restoredSession
        assertThrows(NullPointerException.class, () -> lifecycleManager.initModel(stub, new UserPrefs(), null));
    }

    @Test
    void persistOnStop_nullArgs_throwsNpe() {
        Logic logicStub = new RecordingLogicStub(new seedu.address.model.history.CommandHistory(), Optional.empty());

        assertThrows(NullPointerException.class, () -> lifecycleManager.persistOnStop(null, logicStub));

        Storage storageStub = new RecordingStorageStub();

        assertThrows(NullPointerException.class, () -> lifecycleManager.persistOnStop(storageStub, null));
    }
}
