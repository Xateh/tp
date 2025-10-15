package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

class JsonSerializableSessionTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(800, 600, 10, 10);
    private static final Path ADDRESS_BOOK_PATH = Path.of("data", "address.json");

    @Test
    void toModelType_validSession_success() throws IllegalValueException {
        SessionData original = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                ADDRESS_BOOK_PATH,
                List.of("Alice"),
                List.of(new SessionCommand(Instant.parse("2025-10-14T00:01:00Z"), "find Alice")),
                GUI_SETTINGS);

        JsonSerializableSession jsonSession = new JsonSerializableSession(original);
        SessionData converted = jsonSession.toModelType();

        assertEquals(original, converted);
    }

    @Test
    void toModelType_missingSavedAt_throwsIllegalValueException() throws IOException {
        String template = "{\"formatVersion\":\"1.0\",\"addressBookPath\":\"%s\","
                + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},\"commandHistory\":[]}";
        String json = String.format(template, ADDRESS_BOOK_PATH);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_invalidSavedAt_throwsIllegalValueException() throws IOException {
        String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"invalid\",\"addressBookPath\":\"%s\","
                + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},\"commandHistory\":[]}";
        String json = String.format(template, ADDRESS_BOOK_PATH);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingCommandTimestamp_throwsIllegalValueException() throws IOException {
        String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\","
                + "\"addressBookPath\":\"%s\",\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
                + "\"commandHistory\":[{\"commandText\":\"list\"}]}";
        String json = String.format(template, ADDRESS_BOOK_PATH);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }
}
