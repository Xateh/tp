package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.history.CommandHistory;
import seedu.address.model.person.Person;
import seedu.address.session.SessionData;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonCommandHistoryStorage;
import seedu.address.storage.JsonSessionStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(temporaryFolder.resolve("commandHistory.json"));
        JsonSessionStorage sessionStorage = new JsonSessionStorage(temporaryFolder.resolve("sessions"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_historyCommand_displaysRecordedCommands() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        CommandResult result = logic.execute(HistoryCommand.COMMAND_WORD);
        String expectedHistory = String.format("1. %s", ListCommand.COMMAND_WORD);
        assertEquals(String.format(HistoryCommand.MESSAGE_SUCCESS, expectedHistory),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_commandHistorySaveIoException_throwsCommandException() {
        Path abPath = temporaryFolder.resolve("historyFailAb.json");
        Path prefsPath = temporaryFolder.resolve("historyFailPrefs.json");
        Path historyPath = temporaryFolder.resolve("historyFailHistory.json");
        Path sessionDir = temporaryFolder.resolve("historyFailSessions");

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath),
                new JsonCommandHistoryStorage(historyPath),
                new JsonSessionStorage(sessionDir)) {
            @Override
            public void saveCommandHistory(seedu.address.model.history.CommandHistory commandHistory)
                    throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };

        logic = new LogicManager(model, storage);

        assertCommandFailure(ListCommand.COMMAND_WORD, CommandException.class,
                String.format(LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()),
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void execute_commandHistorySaveAccessDenied_throwsCommandException() {
        Path abPath = temporaryFolder.resolve("historyDeniedAb.json");
        Path prefsPath = temporaryFolder.resolve("historyDeniedPrefs.json");
        Path historyPath = temporaryFolder.resolve("historyDeniedHistory.json");
        Path sessionDir = temporaryFolder.resolve("historyDeniedSessions");
        AccessDeniedException denied = new AccessDeniedException(historyPath.toString());

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath),
                new JsonCommandHistoryStorage(historyPath),
                new JsonSessionStorage(sessionDir)) {
            @Override
            public void saveCommandHistory(seedu.address.model.history.CommandHistory commandHistory)
                    throws IOException {
                throw denied;
            }
        };

        logic = new LogicManager(model, storage);

        assertCommandFailure(ListCommand.COMMAND_WORD, CommandException.class,
                String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, historyPath),
                new ModelManager(model.getAddressBook(), new UserPrefs()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void getCommandHistorySnapshot_unmodifiableList() {
        assertThrows(UnsupportedOperationException.class, () ->
            logic.getCommandHistorySnapshot().add("new command")
        );
    }

    @Test
    public void getCommandHistorySnapshot_includesExecutedCommands() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        logic.execute(HistoryCommand.COMMAND_WORD);

        List<String> snapshot = logic.getCommandHistorySnapshot();
        assertEquals(List.of(ListCommand.COMMAND_WORD, HistoryCommand.COMMAND_WORD), snapshot);
    }

    @Test
    public void constructor_existingCommandHistory_populatesModelHistory() throws Exception {
        Path abPath = temporaryFolder.resolve("existingHistoryAb.json");
        Path prefsPath = temporaryFolder.resolve("existingHistoryPrefs.json");
        Path historyPath = temporaryFolder.resolve("existingHistoryHistory.json");
        Path sessionDir = temporaryFolder.resolve("existingHistorySessions");

        CommandHistory storedHistory = new CommandHistory(List.of("list", "find Alice"));

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath),
                new JsonCommandHistoryStorage(historyPath),
                new JsonSessionStorage(sessionDir)) {
            @Override
            public Optional<CommandHistory> readCommandHistory() {
                return Optional.of(storedHistory);
            }
        };

        Model testModel = new ModelManager();
        new LogicManager(testModel, storage);

        assertEquals(storedHistory.getEntries(), testModel.getCommandHistory().getEntries());
    }

    @Test
    public void constructor_commandHistoryLoadFailure_initialisesEmptyHistory() throws Exception {
        Path abPath = temporaryFolder.resolve("loadFailureAb.json");
        Path prefsPath = temporaryFolder.resolve("loadFailurePrefs.json");
        Path historyPath = temporaryFolder.resolve("loadFailureHistory.json");
        Path sessionDir = temporaryFolder.resolve("loadFailureSessions");

        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(abPath),
                new JsonUserPrefsStorage(prefsPath),
                new JsonCommandHistoryStorage(historyPath),
                new JsonSessionStorage(sessionDir)) {
            @Override
            public Optional<CommandHistory> readCommandHistory() throws DataLoadingException {
                throw new DataLoadingException(DUMMY_IO_EXCEPTION);
            }
        };

        Model testModel = new ModelManager();
        new LogicManager(testModel, storage);

        assertTrue(testModel.getCommandHistory().isEmpty());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        syncCommandHistory(expectedModel, model);
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        syncCommandHistory(expectedModel, model);
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path addressBookPath = temporaryFolder.resolve("ExceptionAddressBook.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(temporaryFolder.resolve("ExceptionCommandHistory.json"));
        JsonSessionStorage sessionStorage =
                new JsonSessionStorage(temporaryFolder.resolve("ExceptionSessions"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    private void syncCommandHistory(Model expectedModel, Model actualModel) {
        CommandHistory historySnapshot = new CommandHistory(actualModel.getCommandHistory().getEntries());
        expectedModel.setCommandHistory(historySnapshot);
    }

    @Test
    public void getCurrentSessionData_recordsSuccessfulCommands() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        SessionData sessionData = logic.getCurrentSessionData();
        assertEquals(1, sessionData.getCommandHistory().size());
        assertEquals(ListCommand.COMMAND_WORD, sessionData.getCommandHistory().get(0).getCommandText());
    }

    @Test
    public void getCurrentSessionData_tracksSearchKeywords() throws Exception {
        model.addPerson(new PersonBuilder().withName("Alice Pauline").build());
        logic.execute("find Alice");
        SessionData sessionData = logic.getCurrentSessionData();
        assertEquals(List.of("Alice"), sessionData.getSearchKeywords());

        logic.execute(ListCommand.COMMAND_WORD);
        sessionData = logic.getCurrentSessionData();
        assertEquals(List.of(), sessionData.getSearchKeywords());
    }

    @Test
    public void constructor_withExistingSession_restoresFilter() throws Exception {
        Person alice = new PersonBuilder().withName("Alice Pauline").build();
        model.addPerson(alice);

        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("restoreAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("restorePrefs.json"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(temporaryFolder.resolve("restoreHistory.json"));
        JsonSessionStorage sessionStorage = new JsonSessionStorage(temporaryFolder.resolve("restoreSessions"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);

        SessionData previousSession = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                storage.getAddressBookFilePath(),
                List.of("Alice"),
                List.of(),
                new GuiSettings());

        logic = new LogicManager(model, storage, Optional.of(previousSession));

        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals(alice, logic.getFilteredPersonList().get(0));
    }
}
