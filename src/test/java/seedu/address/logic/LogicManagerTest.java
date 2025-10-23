package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
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
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
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
    public void getAddressBookFilePath_delegatesToModel() {
        assertEquals(model.getAddressBookFilePath(), logic.getAddressBookFilePath());
    }

    @Test
    public void guiSettings_roundTripThroughLogic() {
        GuiSettings newSettings = new GuiSettings(1024, 768, 20, 40);
        logic.setGuiSettings(newSettings);
        assertEquals(newSettings, logic.getGuiSettings());
    }

    @Test
    public void execute_successCommand_recordsHistoryEntry() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;

        logic.execute(addCommand);

        CommandHistory historySnapshot = logic.getCommandHistorySnapshot();
        assertEquals(1, historySnapshot.size());
        assertEquals(addCommand.trim(), historySnapshot.getEntries().get(0));
    }

    @Test
    public void execute_parseException_doesNotRecordHistory() {
        CommandHistory before = logic.getCommandHistorySnapshot();
        String invalidCommand = "uicfhmowqewca";

        assertThrows(ParseException.class, () -> logic.execute(invalidCommand));

        CommandHistory after = logic.getCommandHistorySnapshot();
        assertEquals(before, after);
    }

    @Test
    public void execute_commandException_doesNotRecordHistory() {
        CommandHistory before = logic.getCommandHistorySnapshot();
        String deleteCommand = "delete 9";

        assertThrows(CommandException.class, () -> logic.execute(deleteCommand));

        CommandHistory after = logic.getCommandHistorySnapshot();
        assertEquals(before, after);
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
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
                throw new DataLoadingException(new IOException("dummy IO exception"));
            }
        };

        Model testModel = new ModelManager();
        new LogicManager(testModel, storage);

        assertTrue(testModel.getCommandHistory().isEmpty());
    }

    @Test
    public void getSessionSnapshotIfDirty_addressBookUnchanged_returnsEmpty() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());
    }

    @Test
    public void getSessionSnapshotIfDirty_addressBookChanged_returnsSnapshot() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();

        logic.execute(addCommand);

        Optional<SessionData> snapshot = logic.getSessionSnapshotIfDirty();
        assertTrue(snapshot.isPresent());
        assertTrue(snapshot.get().getAddressBook().getPersonList().contains(expectedPerson));
    }

    @Test
    public void getSessionSnapshotIfDirty_markPersistedClearsDirtyFlag() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addCommand);

        assertTrue(logic.getSessionSnapshotIfDirty().isPresent());
        logic.markSessionSnapshotPersisted();
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());
    }

    @Test
    public void getCommandHistorySnapshot_returnsDefensiveCopy() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addCommand);

        CommandHistory snapshot = logic.getCommandHistorySnapshot();
        snapshot.reset(List.of("modified"));

        CommandHistory latestSnapshot = logic.getCommandHistorySnapshot();
        assertEquals(List.of(addCommand.trim()), latestSnapshot.getEntries());
    }

    @Test
    public void sessionSnapshot_tracksSearchKeywordsAcrossMutations() throws Exception {
        model.addPerson(new PersonBuilder().withName("Alice Pauline").build());

        logic.execute("find Alice");
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());

        String addBobCommand = AddCommand.COMMAND_WORD + NAME_DESC_BOB + PHONE_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB;
        logic.execute(addBobCommand);

        SessionData snapshot = logic.getSessionSnapshotIfDirty().orElseThrow();
        assertEquals(List.of("Alice"), snapshot.getSearchKeywords());
        logic.markSessionSnapshotPersisted();

        logic.execute(ListCommand.COMMAND_WORD);
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());

        String addAmyCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addAmyCommand);

        snapshot = logic.getSessionSnapshotIfDirty().orElseThrow();
        assertEquals(List.of(), snapshot.getSearchKeywords());
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
    private void syncCommandHistory(Model expectedModel, Model actualModel) {
        CommandHistory historySnapshot = new CommandHistory(actualModel.getCommandHistory().getEntries());
        expectedModel.setCommandHistory(historySnapshot);
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
            model.getAddressBook(),
            List.of("Alice"),
            new GuiSettings());

        logic = new LogicManager(model, storage, Optional.of(previousSession));

        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals(alice, logic.getFilteredPersonList().get(0));
    }

    @Test
    public void constructor_withSessionWithoutKeywords_doesNotFilter() throws Exception {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("emptyRestoreAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("emptyRestorePrefs.json"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(temporaryFolder.resolve("emptyRestoreHistory.json"));
        JsonSessionStorage sessionStorage = new JsonSessionStorage(temporaryFolder.resolve("emptyRestoreSessions"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);

        SessionData previousSession = new SessionData(
            Instant.parse("2025-10-14T00:00:00Z"),
            model.getAddressBook(),
            List.of(),
            new GuiSettings());

        logic = new LogicManager(model, storage, Optional.of(previousSession));

        assertEquals(model.getFilteredPersonList(), logic.getFilteredPersonList());
    }
}
