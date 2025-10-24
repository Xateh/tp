package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import seedu.address.app.MainAppConfigLoader;
import seedu.address.app.MainAppUserPrefsLoader;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.history.CommandHistory;
import seedu.address.model.person.Person;
import seedu.address.session.SessionData;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

class MainAppTest {

    private static final Logger TEST_LOGGER = LogsCenter.getLogger(MainAppTest.class);

    @TempDir
    Path tempDir;

    @Test
    void initFromParameters_setsUpComponents() {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));

        UserPrefs userPrefs = createUserPrefs();

        StubConfigLoader configLoader = new StubConfigLoader(config);
        StubUserPrefsLoader userPrefsLoader = new StubUserPrefsLoader(userPrefs);
        StubModel model = new StubModel(userPrefs);
        StubLifecycleManager lifecycleManager = new StubLifecycleManager(TEST_LOGGER, model, Optional.empty());
        StubLogic logic = new StubLogic();
        StubUi ui = new StubUi();

        TestMainApp app = new TestMainApp(lifecycleManager, configLoader, userPrefsLoader, logic, ui);
        AppParameters params = new AppParameters();
        params.setConfigPath(tempDir.resolve("config.json"));

        app.initFromParameters(params);

        assertSame(config, app.config);
        assertSame(model, app.model);
        assertSame(logic, app.logic);
        assertSame(ui, app.ui);
        assertTrue(lifecycleManager.loadSessionCalled);
        assertEquals(config.getUserPrefsFilePath(), app.storage.getUserPrefsFilePath());
    }

    @Test
    void init_usesOverrideParameters() throws Exception {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));
        UserPrefs userPrefs = createUserPrefs();

        StubConfigLoader configLoader = new StubConfigLoader(config);
        StubUserPrefsLoader userPrefsLoader = new StubUserPrefsLoader(userPrefs);
        StubModel model = new StubModel(userPrefs);
        StubLifecycleManager lifecycleManager = new StubLifecycleManager(TEST_LOGGER, model, Optional.empty());
        StubLogic logic = new StubLogic();
        StubUi ui = new StubUi();

        TestMainApp app = new TestMainApp(lifecycleManager, configLoader, userPrefsLoader, logic, ui);
        AppParameters params = new AppParameters();
        params.setConfigPath(tempDir.resolve("override.json"));
        app.setAppParameters(params);

        app.init();

        assertTrue(lifecycleManager.loadSessionCalled);
        assertSame(config, app.config);
    }

    @Test
    void start_delegatesToUi() {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));
        UserPrefs userPrefs = createUserPrefs();

        StubConfigLoader configLoader = new StubConfigLoader(config);
        StubUserPrefsLoader userPrefsLoader = new StubUserPrefsLoader(userPrefs);
        StubModel model = new StubModel(userPrefs);
        StubLifecycleManager lifecycleManager = new StubLifecycleManager(TEST_LOGGER, model, Optional.empty());
        StubLogic logic = new StubLogic();
        StubUi ui = new StubUi();

        TestMainApp app = new TestMainApp(lifecycleManager, configLoader, userPrefsLoader, logic, ui);
        AppParameters params = new AppParameters();
        params.setConfigPath(tempDir.resolve("config.json"));
        app.initFromParameters(params);

        app.start(null);

        assertTrue(ui.started);
    }

    @Test
    void stop_successfulSave_callsPersist() {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));
        UserPrefs userPrefs = createUserPrefs();

        StubConfigLoader configLoader = new StubConfigLoader(config);
        StubUserPrefsLoader userPrefsLoader = new StubUserPrefsLoader(userPrefs);
        StubModel model = new StubModel(userPrefs);
        StubLifecycleManager lifecycleManager = new StubLifecycleManager(TEST_LOGGER, model, Optional.empty());
        StubLogic logic = new StubLogic();
        StubUi ui = new StubUi();

        TestMainApp app = new TestMainApp(lifecycleManager, configLoader, userPrefsLoader, logic, ui);
        AppParameters params = new AppParameters();
        params.setConfigPath(tempDir.resolve("config.json"));
        app.initFromParameters(params);

        app.stop();

        assertTrue(lifecycleManager.persistCalled);
        assertTrue(Files.exists(app.storage.getUserPrefsFilePath()));
    }

    @Test
    void stop_saveFails_stillCallsPersist() {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));
        UserPrefs userPrefs = createUserPrefs();

        StubConfigLoader configLoader = new StubConfigLoader(config);
        StubUserPrefsLoader userPrefsLoader = new StubUserPrefsLoader(userPrefs);
        StubModel model = new StubModel(userPrefs);
        StubLifecycleManager lifecycleManager = new StubLifecycleManager(TEST_LOGGER, model, Optional.empty());
        StubLogic logic = new StubLogic();
        StubUi ui = new StubUi();

        TestMainApp app = new TestMainApp(lifecycleManager, configLoader, userPrefsLoader, logic, ui);
        AppParameters params = new AppParameters();
        params.setConfigPath(tempDir.resolve("config.json"));
        app.initFromParameters(params);

        ThrowingStorageStub throwingStorage = new ThrowingStorageStub();
        app.storage = throwingStorage;
        app.model = model;

        app.stop();

        assertTrue(throwingStorage.saveAttempted);
        assertTrue(lifecycleManager.persistCalled);
    }

    @Test
    void createLogic_defaultImplementation_returnsLogicManager() {
        Config config = new Config();
        config.setUserPrefsFilePath(tempDir.resolve("prefs.json"));
        UserPrefs userPrefs = createUserPrefs();
        Storage storage = new StorageManager(config, userPrefs);
        Model model = new seedu.address.model.ModelManager(new AddressBook(), userPrefs);

        ExposedMainApp app = new ExposedMainApp();
        Logic logic = app.createLogic(model, storage, Optional.empty());
        Ui ui = app.createUi(logic);

        assertTrue(logic instanceof LogicManager);
        assertTrue(ui instanceof UiManager);
    }

    private UserPrefs createUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(tempDir.resolve("address.json"));
        userPrefs.setCommandHistoryFilePath(tempDir.resolve("history.json"));
        return userPrefs;
    }

    private static class StubConfigLoader extends MainAppConfigLoader {
        private final Config config;

        StubConfigLoader(Config config) {
            super(TEST_LOGGER);
            this.config = config;
        }

        @Override
        public Config load(Path configFilePath) {
            return config;
        }
    }

    private static class StubUserPrefsLoader extends MainAppUserPrefsLoader {
        private final UserPrefs userPrefs;

        StubUserPrefsLoader(UserPrefs userPrefs) {
            super(TEST_LOGGER);
            this.userPrefs = userPrefs;
        }

        @Override
        public UserPrefs load(Config config) {
            return userPrefs;
        }
    }

    private static class StubLifecycleManager extends MainAppLifecycleManager {
        private final Model modelToReturn;
        private final Optional<SessionData> sessionToReturn;
        private boolean loadSessionCalled;
        private boolean persistCalled;

        StubLifecycleManager(Logger logger, Model modelToReturn, Optional<SessionData> sessionToReturn) {
            super(logger);
            this.modelToReturn = modelToReturn;
            this.sessionToReturn = sessionToReturn;
        }

        @Override
        public Optional<SessionData> loadSession(Storage storage) {
            loadSessionCalled = true;
            return sessionToReturn;
        }

        @Override
        public Model initModel(Storage storage, ReadOnlyUserPrefs userPrefs, Optional<SessionData> restoredSession) {
            return modelToReturn;
        }

        @Override
        public void persistOnStop(Storage storage, Logic logic) {
            persistCalled = true;
        }
    }

    private static class StubModel implements Model {
        private final UserPrefs userPrefs;

        StubModel(UserPrefs userPrefs) {
            this.userPrefs = userPrefs;
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return userPrefs;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return userPrefs.getGuiSettings();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            return userPrefs.getAddressBookFilePath();
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deletePerson(Person target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addPerson(Person person) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateFilteredPersonList(java.util.function.Predicate<Person> predicate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CommandHistory getCommandHistory() {
            return new CommandHistory();
        }

        @Override
        public void setCommandHistory(CommandHistory commandHistory) {
            throw new UnsupportedOperationException();
        }
    }

    private static class StubLogic implements Logic {

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
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
        public Optional<SessionData> getSessionSnapshotIfDirty() {
            return Optional.empty();
        }

        @Override
        public void markSessionSnapshotPersisted() {}

        @Override
        public CommandHistory getCommandHistorySnapshot() {
            return new CommandHistory();
        }
    }

    private static class StubUi implements Ui {
        private boolean started;

        @Override
        public void start(Stage primaryStage) {
            started = true;
        }
    }

    private static class TestMainApp extends MainApp {
        private final Logic logicToReturn;
        private final Ui uiToReturn;

        TestMainApp(MainAppLifecycleManager lifecycleManager, MainAppConfigLoader configLoader,
                MainAppUserPrefsLoader userPrefsLoader, Logic logicToReturn, Ui uiToReturn) {
            super(lifecycleManager, configLoader, userPrefsLoader);
            this.logicToReturn = logicToReturn;
            this.uiToReturn = uiToReturn;
        }

        @Override
        protected Logic createLogic(Model model, Storage storage, Optional<SessionData> restoredSession) {
            return logicToReturn;
        }

        @Override
        protected Ui createUi(Logic logic) {
            return uiToReturn;
        }
    }

    private static class ThrowingStorageStub extends StorageStub {
        private boolean saveAttempted;

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            saveAttempted = true;
            throw new IOException("boom");
        }
    }

    private abstract static class StorageStub implements Storage {
        @Override
        public Path getUserPrefsFilePath() {
            return Path.of("prefs.json");
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            return Path.of("address.json");
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getCommandHistoryFilePath() {
            return Path.of("history.json");
        }

        @Override
        public Optional<CommandHistory> readCommandHistory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveCommandHistory(CommandHistory commandHistory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SessionData> readSession() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveSession(SessionData sessionData) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getSessionDirectory() {
            return Path.of("sessions");
        }
    }

    private static class ExposedMainApp extends MainApp {
        ExposedMainApp() {
            super();
        }

        @Override
        protected Logic createLogic(Model model, Storage storage, Optional<SessionData> restoredSession) {
            return super.createLogic(model, storage, restoredSession);
        }

        @Override
        protected Ui createUi(Logic logic) {
            return super.createUi(logic);
        }
    }
}
