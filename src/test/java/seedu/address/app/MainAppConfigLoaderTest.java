package seedu.address.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ConfigUtil;

class MainAppConfigLoaderTest {

    private static final Logger TEST_LOGGER = LogsCenter.getLogger(MainAppConfigLoaderTest.class);

    @TempDir
    Path tempDir;

    @AfterEach
    void cleanUpDefaultConfig() throws Exception {
        Files.deleteIfExists(Config.DEFAULT_CONFIG_FILE);
    }

    @Test
    void load_nullPath_usesDefaultFile() throws Exception {
        Files.deleteIfExists(Config.DEFAULT_CONFIG_FILE);
        MainAppConfigLoader loader = new MainAppConfigLoader(TEST_LOGGER);

        Config config = loader.load(null);

        assertEquals(new Config(), config);
        assertTrue(Files.exists(Config.DEFAULT_CONFIG_FILE));
    }

    @Test
    void load_existingConfig_returnsStoredValues() throws Exception {
        Path configPath = tempDir.resolve("custom-config.json");
        Config expected = new Config();
        expected.setLogLevel(Level.FINE);
        ConfigUtil.saveConfig(expected, configPath);

        MainAppConfigLoader loader = new MainAppConfigLoader(TEST_LOGGER);
        Config loaded = loader.load(configPath);

        assertEquals(Level.FINE, loaded.getLogLevel());
    }

    @Test
    void load_invalidConfig_returnsDefaultConfig() throws Exception {
        Path configPath = tempDir.resolve("invalid-config.json");
        Files.createFile(configPath);
        Files.writeString(configPath, "{ invalid json }");

        MainAppConfigLoader loader = new MainAppConfigLoader(TEST_LOGGER);
        Config loaded = loader.load(configPath);

        assertEquals(new Config(), loaded);
        assertTrue(ConfigUtil.readConfig(configPath).isPresent());
    }
}
