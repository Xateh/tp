package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Point;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

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

        assertEquals(original, converted);
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
        // Remove the guiSettings field entirely (including the comma before it)
        String json = createBaseJson();
        int idx = json.lastIndexOf(",\"guiSettings\":");
        if (idx != -1) {
            json = json.substring(0, idx) + "}";
        }
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiWindowWidth_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst("\\\"windowWidth\\\":800,", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiWindowHeight_throwsIllegalValueException() throws IOException {
        String json = createBaseJson().replaceFirst(",\\\"windowHeight\\\":600", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_guiSettingsMissingCoordinates_defaultsToOrigin() throws IllegalValueException, IOException {
        // Remove windowX and windowY fields but keep valid JSON
        String json = createBaseJson().replace(",\"windowX\":10,\"windowY\":10", "");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        SessionData converted = jsonSession.toModelType();
        Point coordinates = converted.getGuiSettings().getWindowCoordinates();
        assertNotNull(coordinates);
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
        assertEquals(data.getSearchKeywords(), result.getSearchKeywords());
        assertEquals(data.getGuiSettings().getWindowWidth(), result.getGuiSettings().getWindowWidth());
        assertEquals(data.getGuiSettings().getWindowHeight(), result.getGuiSettings().getWindowHeight());
        // After deserialization, windowCoordinates should be (0,0)
        assertNotNull(result.getGuiSettings().getWindowCoordinates());
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().x);
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().y);
    }

    @Test
    void toModelType_nullGuiCoordinates_defaultsToOrigin() throws IllegalValueException {
        // Use default constructor, which sets windowCoordinates to null
        GuiSettings settings = new GuiSettings();
        SessionData data = new SessionData(
            Instant.now(),
            new seedu.address.model.AddressBook(),
            List.of(),
            settings
        );
        JsonSerializableSession jsonSession = new JsonSerializableSession(data);
        SessionData result = jsonSession.toModelType();
        assertNotNull(result.getGuiSettings().getWindowCoordinates());
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().x);
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().y);
    }

    @Test
    void toModelType_missingGuiYCoordinate_defaultsToOrigin() throws IOException, IllegalValueException {
        String json = createBaseJson().replaceFirst("\\\"windowY\\\":10", "\"windowY\":null");
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        SessionData result = jsonSession.toModelType();
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().y);
        assertEquals(0, result.getGuiSettings().getWindowCoordinates().x);
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

        assertEquals(original.getSearchKeywords(), converted.getSearchKeywords());
        assertEquals(original.isSearchName(), converted.isSearchName());
        assertEquals(original.isSearchPhone(), converted.isSearchPhone());
        assertEquals(original.isSearchEmail(), converted.isSearchEmail());
        assertEquals(original.isSearchAddress(), converted.isSearchAddress());
        assertEquals(original.isSearchTag(), converted.isSearchTag());
        // custom keys should be preserved (order is not important) - compare as sets
        assertEquals(
                Set.copyOf(original.getCustomKeys()),
                Set.copyOf(converted.getCustomKeys())
        );
    }
}
