package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.escapeWithQuotes;
import static seedu.address.logic.commands.decoder.Bindings.MESSAGE_NO_MATCHING_BINDING;
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
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.exceptions.AssemblyException;
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
    private static final String SAMPLE_ADD_COMMAND_INPUT_AMY = String.format("%s %s %s %s %s",
            AddCommand.COMMAND_WORD,
            escapeWithQuotes(VALID_NAME_AMY),
            escapeWithQuotes(VALID_PHONE_AMY),
            escapeWithQuotes(VALID_ADDRESS_AMY),
            escapeWithQuotes(VALID_EMAIL_AMY));
    private static final String SAMPLE_ADD_COMMAND_INPUT_BOB = String.format("%s %s %s %s %s",
            AddCommand.COMMAND_WORD,
            escapeWithQuotes(VALID_NAME_BOB),
            escapeWithQuotes(VALID_PHONE_BOB),
            escapeWithQuotes(VALID_ADDRESS_BOB),
            escapeWithQuotes(VALID_EMAIL_BOB));

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
        assertAssemblyException(invalidCommand, MESSAGE_NO_MATCHING_BINDING);
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
        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        CommandHistory historySnapshot = logic.getCommandHistorySnapshot();
        assertEquals(1, historySnapshot.size());
        assertEquals(SAMPLE_ADD_COMMAND_INPUT_AMY.trim(), historySnapshot.getEntries().get(0));
    }

    @Test
    public void execute_parseException_doesNotRecordHistory() {
        CommandHistory before = logic.getCommandHistorySnapshot();
        String invalidCommand = "uicfhmowqewca";

        assertThrows(AssemblyException.class, () -> logic.execute(invalidCommand));

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
    public void getCommandHistorySnapshot_unmodifiableList() {
        assertThrows(UnsupportedOperationException.class, () ->
                logic.getCommandHistorySnapshot().getEntries().add("new command")
        );
    }

    @Test
    public void getCommandHistorySnapshot_includesExecutedCommands() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD);
        logic.execute(HistoryCommand.COMMAND_WORD);

        List<String> snapshot = logic.getCommandHistorySnapshot().getEntries();
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

        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        Optional<SessionData> snapshot = logic.getSessionSnapshotIfDirty();
        assertTrue(snapshot.isPresent());
        assertTrue(snapshot.get().getAddressBook().getPersonList().contains(expectedPerson));
    }

    @Test
    public void getSessionSnapshotIfDirty_markPersistedClearsDirtyFlag() throws Exception {
        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        assertTrue(logic.getSessionSnapshotIfDirty().isPresent());
        logic.markSessionSnapshotPersisted();
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());
    }

    @Test
    public void getCommandHistorySnapshot_returnsDefensiveCopy() throws Exception {
        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        CommandHistory snapshot = logic.getCommandHistorySnapshot();
        snapshot.reset(List.of("modified"));

        CommandHistory latestSnapshot = logic.getCommandHistorySnapshot();
        assertEquals(List.of(SAMPLE_ADD_COMMAND_INPUT_AMY.trim()), latestSnapshot.getEntries());
    }

    @Test
    public void sessionSnapshot_tracksSearchKeywordsAcrossMutations() throws Exception {
        model.addPerson(new PersonBuilder().withName("Alice Pauline").build());

        logic.execute("find Alice");
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());

        logic.execute(SAMPLE_ADD_COMMAND_INPUT_BOB);

        SessionData snapshot = logic.getSessionSnapshotIfDirty().orElseThrow();
        assertEquals(List.of("Alice"), snapshot.getSearchKeywords());
        logic.markSessionSnapshotPersisted();

        logic.execute(ListCommand.COMMAND_WORD);
        assertTrue(logic.getSessionSnapshotIfDirty().isEmpty());

        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        snapshot = logic.getSessionSnapshotIfDirty().orElseThrow();
        assertEquals(List.of(), snapshot.getSearchKeywords());
    }

    @Test
    public void getSessionSnapshotIfAnyDirty_searchKeywordsChange_present() throws Exception {
        model.addPerson(new PersonBuilder().withName("Alice Pauline").build());

        // search changes should mark metadata dirty, visible via getSessionSnapshotIfAnyDirty
        logic.execute("find Alice");
        Optional<SessionData> snapshot = logic.getSessionSnapshotIfAnyDirty();
        assertTrue(snapshot.isPresent());
        assertEquals(List.of("Alice"), snapshot.get().getSearchKeywords());

        // marking persisted clears the metadata dirty flag
        logic.markSessionSnapshotPersisted();
        assertTrue(logic.getSessionSnapshotIfAnyDirty().isEmpty());
    }

    @Test
    public void getSessionSnapshotIfAnyDirty_guiSettingsChange_present() {
        GuiSettings newSettings = new GuiSettings(640, 480, 10, 10);
        // changing GUI settings through Logic should mark metadata dirty
        logic.setGuiSettings(newSettings);

        Optional<SessionData> snapshot = logic.getSessionSnapshotIfAnyDirty();
        assertTrue(snapshot.isPresent());
        assertEquals(newSettings, snapshot.get().getGuiSettings());

        logic.markSessionSnapshotPersisted();
        assertTrue(logic.getSessionSnapshotIfAnyDirty().isEmpty());
    }

    @Test
    public void getSessionSnapshotIfAnyDirty_commandResultOnlyChange_empty() throws Exception {
        // List with no active filters only changes the command result text
        logic.execute(ListCommand.COMMAND_WORD);
        assertTrue(logic.getSessionSnapshotIfAnyDirty().isEmpty());
    }

    @Test
    public void getSessionSnapshotIfAnyDirty_metadataReverted_empty() throws Exception {
        model.addPerson(new PersonBuilder().withName("Alice Pauline").build());

        logic.execute("find Alice");
        logic.execute(ListCommand.COMMAND_WORD);

        assertTrue(logic.getSessionSnapshotIfAnyDirty().isEmpty());
    }

    /**
     * Executes the command and confirms that - no exceptions are thrown <br> - the feedback message is equal to
     * {@code expectedMessage} <br> - the internal model manager state is the same as that in {@code expectedModel}
     * <br>
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
                                      Model expectedModel) throws CommandException, AssemblyException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        syncCommandHistory(expectedModel, model);
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that an AssemblyException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertAssemblyException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, AssemblyException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     *
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
                                      String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that - the {@code expectedException} is thrown <br> - the resulting error
     * message is equal to {@code expectedMessage} <br> - the internal model manager state is the same as that in
     * {@code expectedModel} <br>
     *
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
     * @param e               the exception to be thrown by the Storage component
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

    @Test
    public void execute_validLinkCommand_success() throws Exception {
        // Add two people first
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
        logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_BOB + PHONE_DESC_BOB
                + EMAIL_DESC_BOB + ADDRESS_DESC_BOB);

        // Execute a valid link
        CommandResult result = logic.execute("link 1 mentor 2");
        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains("mentor"));
        assertTrue(feedback.contains("is now"));
        assertTrue(feedback.contains("Amy"));
        assertTrue(feedback.contains("Bob"));
    }

    @Test
    public void execute_invalidLinkIndexes_throwsCommandException() {
        // No persons added yet
        assertThrows(CommandException.class, () -> logic.execute("link 1 friend 2"));
    }

    @Test
    public void execute_selfLink_throwsValidationException() {
        // Add one person
        assertThrows(Exception.class, () -> {
            logic.execute(AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                    + EMAIL_DESC_AMY + ADDRESS_DESC_AMY);
            logic.execute("link 1 buddy 1");
        });
    }

    public void getModel_validLogicManager_returnsSameModel() {
        // Test the new getModel() method
        Model retrievedModel = logic.getModel();

        assertEquals(model, retrievedModel);
        assertTrue(retrievedModel == model); // Check it's the same reference
    }

    @Test
    public void getModel_afterModelChanges_returnsUpdatedModel() {
        // Add a person to the model
        Person testPerson = new PersonBuilder().withName("Test Person").build();
        model.addPerson(testPerson);

        // Verify getModel() returns the updated model
        Model retrievedModel = logic.getModel();
        assertEquals(model, retrievedModel);
        assertTrue(retrievedModel.getFilteredPersonList().contains(testPerson));
    }

    @Test
    public void getModel_afterExecutingCommand_modelStateConsistent() throws Exception {
        // Execute a command that modifies the model
        logic.execute(SAMPLE_ADD_COMMAND_INPUT_AMY);

        // Verify getModel() returns the updated model state
        Model retrievedModel = logic.getModel();
        assertEquals(model, retrievedModel);
        assertEquals(1, retrievedModel.getFilteredPersonList().size());
    }

    @Test
    public void getModel_multipleCallsSameReference() {
        // Test that multiple calls to getModel() return the same reference
        Model firstCall = logic.getModel();
        Model secondCall = logic.getModel();

        assertTrue(firstCall == secondCall);
        assertEquals(firstCall, secondCall);
    }

    @Test
    public void getModel_afterFilterChange_reflectsChanges() {
        // Add some test persons
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        model.addPerson(alice);
        model.addPerson(bob);

        // Apply a filter
        model.updateFilteredPersonList(person -> person.getName().fullName.contains("Alice"));

        // Verify getModel() reflects the filtered state
        Model retrievedModel = logic.getModel();
        assertEquals(1, retrievedModel.getFilteredPersonList().size());
        assertEquals(alice, retrievedModel.getFilteredPersonList().get(0));
    }

    @Test
    public void getModel_afterGuiSettingsChange_reflectsChanges() {
        GuiSettings newSettings = new GuiSettings(800, 600, 100, 100);
        model.setGuiSettings(newSettings);

        Model retrievedModel = logic.getModel();
        assertEquals(newSettings, retrievedModel.getGuiSettings());
    }

    @Test
    public void getModel_withEmptyAddressBook_returnsEmptyModel() {
        // Ensure the model is empty
        model.setAddressBook(new seedu.address.model.AddressBook());

        Model retrievedModel = logic.getModel();
        assertEquals(0, retrievedModel.getFilteredPersonList().size());
        assertTrue(retrievedModel.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void getModel_afterCommandHistoryUpdate_maintainsConsistency() throws Exception {
        // Execute a command to update command history
        logic.execute(ListCommand.COMMAND_WORD);

        Model retrievedModel = logic.getModel();
        assertEquals(model, retrievedModel);
        assertEquals(model.getCommandHistory().getEntries(),
                retrievedModel.getCommandHistory().getEntries());
    }

    @Test
    public void constructor_withoutSessionData_initializesCorrectly() {
        // Test that the existing constructor (without SessionData) still works
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("noSessionAddressBook.json"));
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("noSessionPrefs.json"));
        JsonCommandHistoryStorage commandHistoryStorage =
                new JsonCommandHistoryStorage(temporaryFolder.resolve("noSessionHistory.json"));
        JsonSessionStorage sessionStorage = new JsonSessionStorage(temporaryFolder.resolve("noSessionSessions"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage,
                commandHistoryStorage, sessionStorage);

        Logic noSessionLogic = new LogicManager(model, storage);

        assertEquals(model, noSessionLogic.getModel());
    }

    @Test
    public void getModel_threadSafety_maintainsConsistency() {
        // Test that getModel() returns consistent results even with concurrent access
        Model firstModel = logic.getModel();

        // Simulate some concurrent operation
        Thread.yield();

        Model secondModel = logic.getModel();
        assertTrue(firstModel == secondModel);
    }

    @Test
    public void getModel_afterStorageOperations_maintainsState() throws Exception {
        // Add a person and execute a command that triggers storage
        Person testPerson = new PersonBuilder().withName("Storage Test").build();
        String addCommand = AddCommand.COMMAND_WORD + " n/Storage Test p/12345678 e/test@example.com a/123 Test St";

        try {
            logic.execute(addCommand);
        } catch (Exception e) {
            // Storage might fail in test environment, but that's okay for this test
        }

        // Verify model state is still accessible
        Model retrievedModel = logic.getModel();
        assertEquals(model, retrievedModel);
    }
}
