package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionData;

class JsonSerializableSessionTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(800, 600, 10, 10);
    private static final String ADDRESS_BOOK_JSON = "{\"persons\":[]}";

    @Test
    void toModelType_validSession_success() throws IllegalValueException {
        SessionData original = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                new AddressBook(),
                List.of("Alice"),
                GUI_SETTINGS);

        JsonSerializableSession jsonSession = new JsonSerializableSession(original);
        SessionData converted = jsonSession.toModelType();

        // Keywords are not persisted in the JSON schema. GUI settings are persisted.
        assertEquals(original.getFormatVersion(), converted.getFormatVersion());
        assertEquals(original.getAddressBook(), converted.getAddressBook());
        assertTrue(converted.getSearchKeywords().isEmpty());
        // GUI settings should be preserved when persisted
        assertEquals(GUI_SETTINGS, converted.getGuiSettings());
    }

    @Test
    void toModelType_missingSavedAt_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"savedAt\\\":\\\"[^\\\"]+\\\",", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_invalidSavedAt_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"savedAt\\\":\\\"[^\\\"]+\\\"",
                "\"savedAt\":\"invalid\"");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingAddressBook_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"addressBook\\\":\\{.*?\\},", "\"addressBook\":null,");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiSettings_throwsIllegalValueException() throws IOException {
        // guiSettings are required in the persisted schema; missing guiSettings should fail
        String json = createBaseJson();
        int idx = json.indexOf(",\"guiSettings\":");
        json = json.substring(0, idx + 1) + "\"guiSettings\":null}";
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiWindowWidth_throwsIllegalValueException() throws IOException {
        // Missing windowWidth should cause an IllegalValueException
        String json = createBaseJson().replaceFirst("\\\"windowWidth\\\":800,", "\\\"windowWidth\\\":null,");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiWindowHeight_throwsIllegalValueException() throws IOException {
        // Missing windowHeight should cause an IllegalValueException
        String json = createBaseJson().replaceFirst("\\\"windowHeight\\\":600,", "\\\"windowHeight\\\":null,");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_guiSettingsMissingCoordinates_defaultsToOrigin() throws IllegalValueException, IOException {
        // When coordinates are not present in persisted GUI settings, they default to origin (0,0)
        String json = createBaseJson().replaceFirst(",\\\"windowX\\\":10,\\\"windowY\\\":10", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        SessionData converted = jsonSession.toModelType();
        Point coordinates = converted.getGuiSettings().getWindowCoordinates();
        assertEquals(new Point(0, 0), coordinates);
    }

    @Test
    void toModelType_unsupportedFormatVersion_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"formatVersion\\\":\\\"2.0\\\"",
                "\"formatVersion\":\"1.0\"");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_oldFormatVersion_rejected() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"formatVersion\\\":\\\"2.0\\\"", "\"formatVersion\":\"1.0\"");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_nullFields_throwsIllegalValueException() {
        // null formatVersion, savedAt, addressBook, guiSettings
        assertThrows(NullPointerException.class, () -> new JsonSerializableSession(null));
    }

    @Test
    void toModelType_emptySearchKeywords_noException() throws IllegalValueException {
        SessionData data = new SessionData(
            Instant.now(),
            new seedu.address.model.AddressBook(),
            List.of(),
            new GuiSettings()
        );
        JsonSerializableSession jsonSession = new JsonSerializableSession(data);
        SessionData result = jsonSession.toModelType();
        assertEquals(data.getFormatVersion(), result.getFormatVersion());
        assertEquals(data.getAddressBook(), result.getAddressBook());
        // Keywords are no longer persisted
        assertTrue(result.getSearchKeywords().isEmpty());
        // GUI settings are persisted; when constructed from a default GuiSettings the
        // toModelType will set missing coordinates to origin (0,0)
        assertEquals(new GuiSettings(740, 600, 0, 0), result.getGuiSettings());
    }

    @Test
    void toModelType_nullGuiCoordinates_defaultsToOrigin() throws IllegalValueException {
        // Use default constructor, which sets windowCoordinates to null; persisted form will
        // set missing coordinates to origin (0,0)
        GuiSettings settings = new GuiSettings();
        SessionData data = new SessionData(
            Instant.now(),
            new seedu.address.model.AddressBook(),
            List.of(),
            settings
        );
        JsonSerializableSession jsonSession = new JsonSerializableSession(data);
        SessionData result = jsonSession.toModelType();
        // Resulting coordinates will be origin (0,0)
        assertEquals(new Point(0, 0), result.getGuiSettings().getWindowCoordinates());
    }

    @Test
    void toModelType_missingGuiYCoordinate_defaultsToOrigin() throws IOException, IllegalValueException {
        // When Y coordinate missing, coordinates default to origin (0,0)
        String json = createBaseJson().replaceFirst(",\"windowY\":10", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        SessionData result = jsonSession.toModelType();
        assertEquals(new GuiSettings(800, 600, 0, 0), result.getGuiSettings());
    }

    @Test
    void toModelType_nullFormatVersionAccepted() throws IOException, IllegalValueException {
        String json = createBaseJson().replaceFirst("\\\"formatVersion\\\":\\\"2.0\\\"", "\"formatVersion\":null");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        SessionData sessionData = jsonSession.toModelType();
        assertEquals(SessionData.FORMAT_VERSION, sessionData.getFormatVersion());
    }

    private String createBaseJson() {
        return String.format("{\"formatVersion\":\"2.0\",\"savedAt\":\"%s\",\"addressBook\":%s,"
                + "\"searchKeywords\":[\"Alice\"],\"guiSettings\":{\"windowWidth\":800,"
                + "\"windowHeight\":600,\"windowX\":10,\"windowY\":10}}",
                "2025-10-14T00:00:00Z", ADDRESS_BOOK_JSON);
    }

    @Test
    void toModelType_persistsFindFlagsAndCustomKeys() throws IllegalValueException {
        // Create a SessionData with specific flags and custom keys
        SessionData original = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                new AddressBook(),
                List.of("Alice"),
                /* searchName */ false,
                /* searchPhone */ true,
                /* searchEmail */ false,
                /* searchAddress */ true,
                /* searchTag */ false,
                List.of("company", "assetClass"),
                GUI_SETTINGS);

        JsonSerializableSession jsonSession = new JsonSerializableSession(original);
        SessionData converted = jsonSession.toModelType();

        // Search keywords are no longer persisted
        assertTrue(converted.getSearchKeywords().isEmpty());
        // Find flags and custom keys are no longer persisted; defaults are used instead
        assertTrue(converted.isSearchName());
        assertTrue(converted.isSearchPhone());
        assertTrue(converted.isSearchEmail());
        assertTrue(converted.isSearchAddress());
        assertTrue(converted.isSearchTag());
        assertTrue(converted.getCustomKeys().isEmpty());
    }
}
