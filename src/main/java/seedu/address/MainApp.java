package seedu.address;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.app.MainAppConfigLoader;
import seedu.address.app.MainAppUserPrefsLoader;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.session.SessionData;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    private final MainAppLifecycleManager lifecycleManager;
    private final MainAppConfigLoader configLoader;
    private final MainAppUserPrefsLoader userPrefsLoader;
    private AppParameters overrideAppParameters;

    MainApp(MainAppLifecycleManager lifecycleManager, MainAppConfigLoader configLoader,
            MainAppUserPrefsLoader userPrefsLoader) {
        this.lifecycleManager = requireNonNull(lifecycleManager);
        this.configLoader = requireNonNull(configLoader);
        this.userPrefsLoader = requireNonNull(userPrefsLoader);
    }

    MainApp(MainAppLifecycleManager lifecycleManager) {
        this(lifecycleManager, new MainAppConfigLoader(logger), new MainAppUserPrefsLoader(logger));
    }

    public MainApp() {
        this(new MainAppLifecycleManager(logger));
    }

    void setAppParameters(AppParameters appParameters) {
        this.overrideAppParameters = appParameters;
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing AddressBook ]===========================");
        super.init();

        AppParameters appParameters = overrideAppParameters != null
                ? overrideAppParameters
                : AppParameters.parse(getParameters());
        overrideAppParameters = null;

        initFromParameters(appParameters);
    }

    void initFromParameters(AppParameters appParameters) {
        requireNonNull(appParameters);

        this.config = configLoader.load(appParameters.getConfigPath());
        initLogging(config);

        UserPrefs userPrefs = userPrefsLoader.load(config);

        this.storage = new StorageManager(config, userPrefs);

        Optional<SessionData> restoredSession = lifecycleManager.loadSession(storage);

        this.model = lifecycleManager.initModel(storage, userPrefs, restoredSession);

        this.logic = createLogic(model, storage, restoredSession);

        this.ui = createUi(logic);
    }

    protected Logic createLogic(Model model, Storage storage, Optional<SessionData> restoredSession) {
        return new LogicManager(model, storage, restoredSession);
    }

    protected Ui createUi(Logic logic) {
        return new UiManager(logic);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping AddressBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
        lifecycleManager.persistOnStop(storage, logic);
    }
}
