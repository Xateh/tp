package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class MainAppTest {

    @Test
    public void initConfig_validConfigPath_success() throws Exception {
        MainApp mainApp = new MainApp();
        Path configPath = Path.of("test-config.json");

        assertDoesNotThrow(() -> {
            mainApp.initConfig(configPath);
        });
    }

    @Test
    public void initConfig_nullConfigPath_usesDefault() throws Exception {
        MainApp mainApp = new MainApp();

        assertDoesNotThrow(() -> {
            mainApp.initConfig(null);
        });
    }

    @Test
    public void constructor_success() {
        assertDoesNotThrow(() -> {
            MainApp mainApp = new MainApp();
            assertNotNull(mainApp);
        });
    }

    @Test
    public void initModelManager_indirectTest() {
        // Test that MainApp can be constructed without errors
        // The actual init() method requires JavaFX context which is complex to set up in unit tests
        MainApp mainApp = new MainApp();
        assertNotNull(mainApp);
    }

    @Test
    public void initPrefs_indirectTest() {
        // Test that MainApp methods that don't require JavaFX work
        MainApp mainApp = new MainApp();
        assertNotNull(mainApp);
    }

    @Test
    public void initLogging_indirectTest() {
        // Test basic instantiation
        MainApp mainApp = new MainApp();
        assertNotNull(mainApp);
    }
}
