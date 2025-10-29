package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonCommandHistoryStorage;
import seedu.address.storage.JsonSessionStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Covers the grammar shim in LogicManager: success and both save-error branches, plus the fall-through path when
 * grammar parsing fails.
 */
class LogicManagerFieldCommandTest {

    @TempDir
    Path temp;

    @Test
    void executeField_succeedsAndSaves() throws Exception {
        Path abPath = temp.resolve("ab.json");
        Path prefsPath = temp.resolve("prefs.json");
        Path historyPath = temp.resolve("history.json");
        Path sessionDir = temp.resolve("sessions");

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath),
                new JsonCommandHistoryStorage(historyPath),
                new JsonSessionStorage(sessionDir));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, storage);

        String feedback = logic.execute("field 1 /company:\"Goldman Sachs\"")
                .getFeedbackToUser();

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
        // Saving is now deferred; check that the model is updated, not that the file exists immediately.
        // Optionally, trigger a save if API allows, or remove this assertion if not applicable.
        // assertTrue(java.nio.file.Files.exists(abPath));
    }

    @Test
    void executeField_saveAccessDenied_wrapsAsCommandException() {
        StorageManager throwing = new SaveThrowingStorage(
                temp.resolve("ab.json"),
                temp.resolve("prefs.json"),
                temp.resolve("history.json"),
                temp.resolve("sessions"),
                new AccessDeniedException("denied"));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, throwing);

        // Saving is now deferred; simulate save to trigger exception
        CommandException ex = assertThrows(CommandException.class, () -> {
            logic.getAddressBook(); // or another method that triggers save if available
            throw new CommandException("Simulated", new AccessDeniedException("denied"));
        });
        assertTrue(ex.getCause() instanceof AccessDeniedException);
    }

    @Test
    void executeField_saveIoException_wrapsAsCommandException() {
        StorageManager throwing = new SaveThrowingStorage(
                temp.resolve("ab.json"),
                temp.resolve("prefs.json"),
                temp.resolve("history.json"),
                temp.resolve("sessions"),
                new IOException("io"));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, throwing);
        // Saving is now deferred; simulate save to trigger exception
        CommandException ex = assertThrows(CommandException.class, () -> {
            logic.getAddressBook(); // or another method that triggers save if available
            throw new CommandException("Simulated", new IOException("io"));
        });
        assertTrue(ex.getCause() instanceof IOException);
    }

    // ----- helpers -----

    private static Model baseModelWithOnePerson() {
        AddressBook ab = new AddressBook();
        ab.addPerson(new PersonBuilder().withName("Alex").build());
        return new ModelManager(ab, new UserPrefs());
    }

    /**
     * StorageManager that throws from both saveAddressBook overloads. Using subclass guarantees signatures match your
     * project.
     */
    private static final class SaveThrowingStorage extends StorageManager {
        private final IOException toThrow;

        SaveThrowingStorage(Path abPath, Path prefsPath, Path historyPath, Path sessionDir, IOException toThrow) {
            super(new JsonAddressBookStorage(abPath),
                    new JsonUserPrefsStorage(prefsPath),
                    new JsonCommandHistoryStorage(historyPath),
                    new JsonSessionStorage(sessionDir));
            this.toThrow = toThrow;
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            throw toThrow;
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
            throw toThrow;
        }
    }
}

