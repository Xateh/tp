package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

/**
 * Minimal, clean tests for JsonSerializableSession to ensure malformed persisted
 * JSON is rejected where appropriate and that missing coordinates don't crash.
 */
class JsonSerializableSessionTest {

    @Test
    public void toModelType_missingSavedAt_throwsIllegalValueException() throws Exception {
        String json = "{\n"
            + "  \"formatVersion\": null,\n"
            + "  \"addressBook\": { \"persons\": [] },\n"
            + "  \"guiSettings\": { \"windowWidth\": 600.0, \"windowHeight\": 400.0 }\n"
            + "}";

        JsonSerializableSession session = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, session::toModelType);
    }

    @Test
    public void toModelType_invalidSavedAt_throwsIllegalValueException() throws Exception {
        String json = "{\n"
            + "  \"savedAt\": \"not-a-timestamp\",\n"
            + "  \"addressBook\": { \"persons\": [] },\n"
            + "  \"guiSettings\": { \"windowWidth\": 600.0, \"windowHeight\": 400.0 }\n"
            + "}";

        JsonSerializableSession session = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertThrows(IllegalValueException.class, session::toModelType);
    }

    @Test
    public void toModelType_centerFallback_coordsMissing() throws Exception {
        String json = "{\n"
            + "  \"savedAt\": \"2025-10-30T00:00:00Z\",\n"
            + "  \"addressBook\": { \"persons\": [] },\n"
            + "  \"guiSettings\": { \"windowWidth\": 600.0,\n"
            + "    \"windowHeight\": 400.0 }\n"
            + "}";

        JsonSerializableSession s = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        assertDoesNotThrow(s::toModelType);
    }

    @Test
    public void toModelType_fromSessionData_stripsKeywordsAndPreservesGui() throws IllegalValueException {
        SessionData data = new SessionData(
            Instant.now(),
            new AddressBook(),
            List.of("Alice"),
            new GuiSettings(740, 600, 0, 0)
        );

        JsonSerializableSession jsonSession = new JsonSerializableSession(data);
        SessionData result = jsonSession.toModelType();
        assertEquals(data.getFormatVersion(), result.getFormatVersion());
        assertEquals(data.getAddressBook(), result.getAddressBook());
        // Keywords are no longer persisted
        assertTrue(result.getSearchKeywords().isEmpty());
        // GUI settings are preserved
        assertEquals(new GuiSettings(740, 600, 0, 0), result.getGuiSettings());
    }

    @Test
    public void toModelType_nullGuiCoordinates_defaultsToOrigin() throws IllegalValueException {
        // Use default constructor, which sets windowCoordinates to null; persisted form will
        // set missing coordinates to origin (0,0)
        GuiSettings settings = new GuiSettings();
        SessionData data = new SessionData(
            Instant.now(),
            new AddressBook(),
            List.of(),
            settings
        );
        JsonSerializableSession jsonSession = new JsonSerializableSession(data);
        SessionData result = jsonSession.toModelType();
        // Resulting coordinates will be origin (0,0)
        assertEquals(new Point(0, 0), result.getGuiSettings().getWindowCoordinates());
    }

    @Test
    public void toModelType_missingGuiYCoordinate_defaultsToOrigin() throws IOException, IllegalValueException {
        // When Y coordinate missing, coordinates default to origin (0,0)
        String json = "{"
            + "\"savedAt\":\"2025-10-14T00:00:00Z\","
            + "\"addressBook\":{\"persons\":[]},"
            + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600,\"windowX\":10}}";
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        SessionData result = jsonSession.toModelType();
        assertEquals(new GuiSettings(800, 600, 0, 0), result.getGuiSettings());
    }

    @Test
    public void toModelType_nullFormatVersionAccepted() throws IOException, IllegalValueException {
        String json = "{\"formatVersion\":null,"
            + "\"savedAt\":\"2025-10-14T00:00:00Z\","
            + "\"addressBook\":{\"persons\":[]},"
            + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600,"
            + "\"windowX\":10,\"windowY\":10}}";
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);
        SessionData sessionData = jsonSession.toModelType();
        assertEquals(SessionData.FORMAT_VERSION, sessionData.getFormatVersion());
    }

    @Test
    public void toModelType_headlessFallback_defaultsToOrigin() throws Exception {
        // Force AWT into headless mode for this test
        String previous = System.getProperty("java.awt.headless");
        System.setProperty("java.awt.headless", "true");
        try {
            GuiSettings s = new GuiSettings();
            // Build a JsonSerializableSession from SessionData that has null coordinates
            seedu.address.session.SessionData data = new seedu.address.session.SessionData(
                    java.time.Instant.parse("2025-10-30T00:00:00Z"),
                    new seedu.address.model.AddressBook(), java.util.List.of(), s);

            JsonSerializableSession json = new JsonSerializableSession(data);
            seedu.address.session.SessionData restored = json.toModelType();

            // In headless mode, Toolkit access will fail and code falls back to origin (0,0)
            assertEquals(new Point(0, 0), restored.getGuiSettings().getWindowCoordinates());
        } finally {
            if (previous == null) {
                System.clearProperty("java.awt.headless");
            } else {
                System.setProperty("java.awt.headless", previous);
            }
        }
    }
}
