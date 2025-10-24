package seedu.address.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonUserPrefsStorage;

class MainAppUserPrefsLoaderTest {

    private static final Logger TEST_LOGGER = LogsCenter.getLogger(MainAppUserPrefsLoaderTest.class);

    @TempDir
    Path tempDir;

    @Test
    void load_missingFile_returnsDefaultAndCreatesFile() throws Exception {
        Config config = new Config();
        Path prefsPath = tempDir.resolve("prefs.json");
        config.setUserPrefsFilePath(prefsPath);

        MainAppUserPrefsLoader loader = new MainAppUserPrefsLoader(TEST_LOGGER);
        UserPrefs loaded = loader.load(config);

        assertEquals(new UserPrefs(), loaded);
        assertTrue(Files.exists(prefsPath));
    }

    @Test
    void load_existingFile_returnsStoredValues() throws Exception {
        Config config = new Config();
        Path prefsPath = tempDir.resolve("prefs.json");
        config.setUserPrefsFilePath(prefsPath);

        UserPrefs expected = new UserPrefs();
        expected.setAddressBookFilePath(tempDir.resolve("address.json"));
        expected.setCommandHistoryFilePath(tempDir.resolve("history.json"));
        new JsonUserPrefsStorage(prefsPath).saveUserPrefs(expected);

        MainAppUserPrefsLoader loader = new MainAppUserPrefsLoader(TEST_LOGGER);
        UserPrefs loaded = loader.load(config);

        assertEquals(expected, loaded);
    }

    @Test
    void load_invalidFile_returnsDefaultPreferences() throws Exception {
        Config config = new Config();
        Path prefsPath = tempDir.resolve("prefs.json");
        config.setUserPrefsFilePath(prefsPath);

        Files.createFile(prefsPath);
        Files.writeString(prefsPath, "{ invalid }");

        MainAppUserPrefsLoader loader = new MainAppUserPrefsLoader(TEST_LOGGER);
        UserPrefs loaded = loader.load(config);

        assertEquals(new UserPrefs(), loaded);
        assertTrue(new JsonUserPrefsStorage(prefsPath).readUserPrefs().isPresent());
    }
}
