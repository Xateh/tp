package seedu.address.app;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.Config;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;

/**
 * Loads the application configuration from disk, falling back to defaults when necessary.
 */
public class MainAppConfigLoader {

    private final Logger logger;

    /**
     * Creates a loader that will emit diagnostic messages using the supplied {@code logger}.
     */
    public MainAppConfigLoader(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    /**
     * Loads the configuration located at {@code configFilePath}. If {@code configFilePath} is {@code null}, the
     * default configuration path is used. Any missing files or fields cause defaults to be restored and persisted.
     */
    public Config load(Path configFilePath) {
        Path configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        Config initializedConfig;
        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }
}
