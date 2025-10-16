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
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Covers the grammar shim in LogicManager: success and both save-error branches,
 * plus the fall-through path when grammar parsing fails.
 */
class LogicManagerFieldCommandTest {

    @TempDir
    Path temp;

    @Test
    void executeField_succeedsAndSaves() throws Exception {
        Path abPath = temp.resolve("ab.json");
        Path prefsPath = temp.resolve("prefs.json");

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, storage);

        String feedback = logic.execute("field 1 /company:\"Goldman Sachs\"")
                .getFeedbackToUser();

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
        assertTrue(java.nio.file.Files.exists(abPath));
    }

    @Test
    void executeField_saveAccessDenied_wrapsAsCommandException() {
        StorageManager throwing = new SaveThrowingStorage(
                temp.resolve("ab.json"),
                temp.resolve("prefs.json"),
                new AccessDeniedException("denied"));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, throwing);

        CommandException ex = assertThrows(CommandException.class, () ->
                logic.execute("field 1 /k:v"));
        assertTrue(ex.getCause() instanceof AccessDeniedException);
    }

    @Test
    void executeField_saveIoException_wrapsAsCommandException() {
        StorageManager throwing = new SaveThrowingStorage(
                temp.resolve("ab.json"),
                temp.resolve("prefs.json"),
                new IOException("io"));

        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, throwing);

        CommandException ex = assertThrows(CommandException.class, () ->
                logic.execute("field 1 /k:v"));
        assertTrue(ex.getCause() instanceof IOException);
    }

    @Test
    void executeMalformedFieldFallsThroughToLegacyParser() throws Exception {
        Path abPath = temp.resolve("ab.json");
        Path prefsPath = temp.resolve("prefs.json");

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath));
        Model model = baseModelWithOnePerson();
        Logic logic = new LogicManager(model, storage);

        // Break grammar (unclosed quote) so lexer throws and shim falls through to legacy parser,
        // which then throws the project's ParseException (unknown command / invalid format).
        assertThrows(ParseException.class, () -> logic.execute("field 1 /company:\"GS"));
    }

    // ----- helpers -----

    private static Model baseModelWithOnePerson() {
        AddressBook ab = new AddressBook();
        ab.addPerson(new PersonBuilder().withName("Alex").build());
        return new ModelManager(ab, new UserPrefs());
    }

    /**
     * StorageManager that throws from both saveAddressBook overloads.
     * Using subclass guarantees signatures match your project.
     */
    private static final class SaveThrowingStorage extends StorageManager {
        private final IOException toThrow;

        SaveThrowingStorage(Path abPath, Path prefsPath, IOException toThrow) {
            super(new JsonAddressBookStorage(abPath), new JsonUserPrefsStorage(prefsPath));
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

