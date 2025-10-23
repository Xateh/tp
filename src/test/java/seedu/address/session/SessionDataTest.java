package seedu.address.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

public class SessionDataTest {

    private static final Instant SAVED_AT_1 = Instant.parse("2024-01-01T10:00:00Z");
    private static final Instant SAVED_AT_2 = Instant.parse("2024-01-01T11:00:00Z");
    private static final Path ADDRESS_BOOK_PATH_1 = Paths.get("data", "addressbook.json");
    private static final Path ADDRESS_BOOK_PATH_2 = Paths.get("data", "addressbook2.json");
    private static final List<String> SEARCH_KEYWORDS_1 = Arrays.asList("alice", "bob");
    private static final List<String> SEARCH_KEYWORDS_2 = Arrays.asList("charlie");
    private static final SessionCommand COMMAND_1 = new SessionCommand(
            Instant.parse("2024-01-01T09:00:00Z"), "add n/John");
    private static final SessionCommand COMMAND_2 = new SessionCommand(
            Instant.parse("2024-01-01T09:30:00Z"), "list");
    private static final List<SessionCommand> COMMAND_HISTORY_1 = Arrays.asList(COMMAND_1);
    private static final List<SessionCommand> COMMAND_HISTORY_2 = Arrays.asList(COMMAND_1, COMMAND_2);
    private static final GuiSettings GUI_SETTINGS_1 = new GuiSettings(800, 600, 0, 0);
    private static final GuiSettings GUI_SETTINGS_2 = new GuiSettings(1024, 768, 100, 100);
    private static final ReadOnlyAddressBook ADDRESS_BOOK_1 = new AddressBook();
    private static final ReadOnlyAddressBook ADDRESS_BOOK_2 = new AddressBook();

    @Test
    public void constructor_nullSavedAt_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                null, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1));
    }

    @Test
    public void constructor_nullAddressBookPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                SAVED_AT_1, null, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1));
        }

    @Test
    public void constructor_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, null, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1));
    }

    @Test
    public void constructor_nullSearchKeywords_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, null, COMMAND_HISTORY_1, GUI_SETTINGS_1));
    }

    @Test
    public void constructor_nullCommandHistory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, null, GUI_SETTINGS_1));
    }

    @Test
    public void constructor_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, null));
    }

    @Test
    public void constructor_validInputs_success() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);

        assertEquals(SAVED_AT_1, sessionData.getSavedAt());
        assertEquals(ADDRESS_BOOK_PATH_1, sessionData.getAddressBookPath());
        assertEquals(ADDRESS_BOOK_1, sessionData.getAddressBook());
        assertEquals(SEARCH_KEYWORDS_1, sessionData.getSearchKeywords());
        assertEquals(COMMAND_HISTORY_1, sessionData.getCommandHistory());
        assertEquals(GUI_SETTINGS_1, sessionData.getGuiSettings());
        assertEquals(SessionData.FORMAT_VERSION, sessionData.getFormatVersion());
    }

    @Test
    public void constructor_emptyLists_success() {
        List<String> emptyKeywords = new ArrayList<>();
        List<SessionCommand> emptyHistory = new ArrayList<>();

        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, emptyKeywords, emptyHistory, GUI_SETTINGS_1);

        assertTrue(sessionData.getSearchKeywords().isEmpty());
        assertTrue(sessionData.getCommandHistory().isEmpty());
    }

    @Test
    public void getSearchKeywords_modifyReturnedList_originalUnchanged() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);

        List<String> keywords = sessionData.getSearchKeywords();
        assertThrows(UnsupportedOperationException.class, () -> keywords.add("newKeyword"));
    }

    @Test
    public void getCommandHistory_modifyReturnedList_originalUnchanged() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);

        List<SessionCommand> history = sessionData.getCommandHistory();
        assertThrows(UnsupportedOperationException.class, () -> history.add(COMMAND_2));
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertTrue(sessionData.equals(sessionData));
    }

    @Test
    public void equals_null_returnsFalse() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertFalse(sessionData.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertFalse(sessionData.equals("string"));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertTrue(sessionData1.equals(sessionData2));
    }

    @Test
    public void equals_differentSavedAt_returnsFalse() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_2, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertFalse(sessionData1.equals(sessionData2));
    }

    @Test
    public void equals_differentAddressBookPath_returnsFalse() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_2, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertFalse(sessionData1.equals(sessionData2));
    }

    @Test
    public void equals_differentSearchKeywords_returnsFalse() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_2, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertFalse(sessionData1.equals(sessionData2));
    }

    @Test
    public void equals_differentCommandHistory_returnsFalse() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_2, GUI_SETTINGS_1);
        assertFalse(sessionData1.equals(sessionData2));
    }

    @Test
    public void equals_differentGuiSettings_returnsFalse() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_2);
        assertFalse(sessionData1.equals(sessionData2));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertEquals(sessionData1.hashCode(), sessionData2.hashCode());
    }

    @Test
    public void hashCode_differentValues_returnsDifferentHashCode() {
        SessionData sessionData1 = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        SessionData sessionData2 = new SessionData(
                SAVED_AT_2, ADDRESS_BOOK_PATH_2, ADDRESS_BOOK_2, SEARCH_KEYWORDS_2, COMMAND_HISTORY_2, GUI_SETTINGS_2);
        assertNotEquals(sessionData1.hashCode(), sessionData2.hashCode());
    }

    @Test
    public void toString_validSessionData_containsAllFields() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        String result = sessionData.toString();

        assertTrue(result.contains("SessionData"));
        assertTrue(result.contains(SessionData.FORMAT_VERSION));
        assertTrue(result.contains(SAVED_AT_1.toString()));
        assertTrue(result.contains(ADDRESS_BOOK_PATH_1.toString()));
    }

    @Test
    public void getFormatVersion_returnsConstantValue() {
        SessionData sessionData = new SessionData(
                SAVED_AT_1, ADDRESS_BOOK_PATH_1, ADDRESS_BOOK_1, SEARCH_KEYWORDS_1, COMMAND_HISTORY_1, GUI_SETTINGS_1);
        assertEquals("1.0", sessionData.getFormatVersion());
    }
}

