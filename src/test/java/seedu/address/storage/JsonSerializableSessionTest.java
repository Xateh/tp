package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

class JsonSerializableSessionTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(800, 600, 10, 10);
    private static final Path ADDRESS_BOOK_PATH = Path.of("data", "address.json");

    @Test
    void toModelType_validSession_success() throws IllegalValueException {
    AddressBook addressBook = new AddressBook();
    addressBook.addPerson(new PersonBuilder().withName("Alice").build());
        SessionData original = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                ADDRESS_BOOK_PATH,
        addressBook,
                List.of("Alice"),
                List.of(new SessionCommand(Instant.parse("2025-10-14T00:01:00Z"), "find Alice")),
                GUI_SETTINGS);

        JsonSerializableSession jsonSession = new JsonSerializableSession(original);
        SessionData converted = jsonSession.toModelType();

        assertEquals(original, converted);
    }

    @Test
    void toModelType_missingSavedAt_throwsIllegalValueException() throws IOException {
    String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"addressBookPath\":\"%s\"," 
        + "\"addressBook\":{\"persons\":[]},\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
        + "\"commandHistory\":[]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_invalidSavedAt_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"invalid\",\"addressBookPath\":\"%s\"," 
        + "\"addressBook\":{\"persons\":[]},\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
        + "\"commandHistory\":[]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingCommandTimestamp_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},"
        + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
                + "\"commandHistory\":[{\"commandText\":\"list\"}]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingCommandText_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},"
        + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
                + "\"commandHistory\":[{\"timestamp\":\"2025-10-14T00:01:00Z\"}]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_invalidCommandTimestamp_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
        String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\","
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},"
        + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
                + "\"commandHistory\":[{\"timestamp\":\"invalid-timestamp\",\"commandText\":\"list\"}]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingAddressBookPath_throwsIllegalValueException() throws IOException {
    String json = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBook\":{\"persons\":[]},\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
        + "\"commandHistory\":[]}";
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiSettings_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},\"guiSettings\":null,"
        + "\"commandHistory\":[]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_missingGuiWindowWidth_throwsIllegalValueException() throws IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},"
        + "\"guiSettings\":{\"windowHeight\":600},\"commandHistory\":[]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        assertThrows(IllegalValueException.class, jsonSession::toModelType);
    }

    @Test
    void toModelType_guiSettingsMissingCoordinates_defaultsToOrigin() throws IllegalValueException, IOException {
        String addressBookPathJson = ADDRESS_BOOK_PATH.toString().replace("\\", "\\\\");
    String template = "{\"formatVersion\":\"1.0\",\"savedAt\":\"2025-10-14T00:00:00Z\"," 
        + "\"addressBookPath\":\"%s\",\"addressBook\":{\"persons\":[]},"
        + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600},"
                + "\"commandHistory\":[]}";
        String json = String.format(template, addressBookPathJson);
        JsonSerializableSession jsonSession = JsonUtil.fromJsonString(json, JsonSerializableSession.class);

        SessionData converted = jsonSession.toModelType();
        Point coordinates = converted.getGuiSettings().getWindowCoordinates();
        assertNotNull(coordinates);
        assertEquals(new Point(0, 0), coordinates);
    }
}
