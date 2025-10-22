package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }

    @Test
    public void setCommandHistoryFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setCommandHistoryFilePath(null));
    }

    @Test
    public void resetData_copiesCommandHistoryPath() {
        UserPrefs source = new UserPrefs();
        GuiSettings guiSettings = new GuiSettings(800, 600, 100, 100);
        Path addressBookPath = Paths.get("data", "address.json");
        Path historyPath = Paths.get("data", "commandhistory.json");
        source.setGuiSettings(guiSettings);
        source.setAddressBookFilePath(addressBookPath);
        source.setCommandHistoryFilePath(historyPath);

        UserPrefs target = new UserPrefs();
        target.resetData(source);

        assertEquals(source, target);
    }

    @Test
    public void equals_differentCommandHistoryPath_returnsFalse() {
        UserPrefs first = new UserPrefs();
        UserPrefs second = new UserPrefs();
        second.setCommandHistoryFilePath(Paths.get("data", "alt-history.json"));

        assertFalse(first.equals(second));
    }
}
