package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.history.CommandHistory;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(getTempFilePath("prefs"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(getTempFilePath("history"));
        JsonSessionStorage sessionStorage =
                new JsonSessionStorage(getTempFilePath("sessions"));
        storageManager = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void addressBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonAddressBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonAddressBookStorageTest} class.
         */
        AddressBook original = getTypicalAddressBook();
        storageManager.saveAddressBook(original);
        ReadOnlyAddressBook retrieved = storageManager.readAddressBook().get();
        assertEquals(original, new AddressBook(retrieved));
    }

    @Test
    public void getAddressBookFilePath() {
        assertNotNull(storageManager.getAddressBookFilePath());
    }

    @Test
    public void getCommandHistoryFilePath() {
        assertNotNull(storageManager.getCommandHistoryFilePath());
    }

    @Test
    public void commandHistoryReadSave() throws Exception {
        CommandHistory original = new CommandHistory();
        original.add("list");
        original.add("add n/Bob");
        storageManager.saveCommandHistory(original);
        CommandHistory retrieved = storageManager.readCommandHistory().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void sessionReadSave() throws Exception {
        SessionData olderSession = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                storageManager.getAddressBookFilePath(),
                List.of("Alice"),
                List.of(new SessionCommand(Instant.parse("2025-10-14T00:00:00Z"), "list")),
                new GuiSettings());
        SessionData newerSession = new SessionData(
                Instant.parse("2025-10-15T00:00:00Z"),
                storageManager.getAddressBookFilePath(),
                List.of("Bob"),
                List.of(new SessionCommand(Instant.parse("2025-10-15T00:00:00Z"), "find Bob")),
                new GuiSettings());

        storageManager.saveSession(olderSession);
        storageManager.saveSession(newerSession);

        SessionData retrieved = storageManager.readSession().get();
        assertEquals(newerSession.getSavedAt(), retrieved.getSavedAt());
        assertEquals(newerSession.getSearchKeywords(), retrieved.getSearchKeywords());

        long fileCount;
        try (var stream = Files.list(storageManager.getSessionDirectory())) {
            fileCount = stream.count();
        }
        assertEquals(2, fileCount);
    }

}
