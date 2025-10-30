package seedu.address.storage;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionData;
/**
 * Json representation of {@link SessionData} for persistence.
 */
@JsonRootName(value = "session")
class JsonSerializableSession {

    private static final String MESSAGE_MISSING_FIELD = "Session file is missing the '%s' field.";

    private final String formatVersion;
    private final String savedAt;
    private final JsonSerializableAddressBook addressBook;
    private final JsonGuiSettings guiSettings;
    @JsonCreator
    JsonSerializableSession(@JsonProperty("formatVersion") String formatVersion,
            @JsonProperty("savedAt") String savedAt,
            @JsonProperty("addressBook") JsonSerializableAddressBook addressBook,
            @JsonProperty("guiSettings") JsonGuiSettings guiSettings) {
        this.formatVersion = formatVersion;
        this.savedAt = savedAt;
        this.addressBook = addressBook;
        this.guiSettings = guiSettings;
    }

    JsonSerializableSession(SessionData source) {
        this.formatVersion = source.getFormatVersion();
        this.savedAt = source.getSavedAt().toString();
        this.addressBook = new JsonSerializableAddressBook(source.getAddressBook());
        // Intentionally do not persist search keywords. They are transient and should not be
        // stored in the session file.
        this.guiSettings = new JsonGuiSettings(source.getGuiSettings());
    }

    SessionData toModelType() throws IllegalValueException {
        if (formatVersion != null && !SessionData.FORMAT_VERSION.equals(formatVersion)) {
            throw new IllegalValueException("Unsupported session format version: " + formatVersion);
        }

        if (savedAt == null) {
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "savedAt"));
        }

        if (addressBook == null) {
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "addressBook"));
        }

        if (guiSettings == null) {
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "guiSettings"));
        }

        Instant parsedSavedAt;
        try {
            parsedSavedAt = Instant.parse(savedAt);
        } catch (Exception ex) {
            throw new IllegalValueException("Invalid timestamp format for savedAt: " + savedAt);
        }

        AddressBook modelAddressBook = addressBook.toModelType();
        GuiSettings modelGuiSettings = guiSettings.toModelType();

        // Do not restore search keywords from storage — keywords are transient and not persisted.
        return new SessionData(parsedSavedAt, modelAddressBook, modelGuiSettings);
    }

    private static class JsonGuiSettings {
        private final Double windowWidth;
        private final Double windowHeight;
        private final Integer windowX;
        private final Integer windowY;

        @JsonCreator
        JsonGuiSettings(@JsonProperty("windowWidth") Double windowWidth,
                @JsonProperty("windowHeight") Double windowHeight,
                @JsonProperty("windowX") Integer windowX,
                @JsonProperty("windowY") Integer windowY) {
            this.windowWidth = windowWidth;
            this.windowHeight = windowHeight;
            this.windowX = windowX;
            this.windowY = windowY;
        }

        JsonGuiSettings(GuiSettings source) {
            this.windowWidth = source.getWindowWidth();
            this.windowHeight = source.getWindowHeight();
            if (source.getWindowCoordinates() != null) {
                this.windowX = source.getWindowCoordinates().x;
                this.windowY = source.getWindowCoordinates().y;
            } else {
                this.windowX = null;
                this.windowY = null;
            }
        }

        GuiSettings toModelType() throws IllegalValueException {
            if (windowWidth == null) {
                throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "guiSettings.windowWidth"));
            }
            if (windowHeight == null) {
                throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "guiSettings.windowHeight"));
            }

            if (windowX != null && windowY != null) {
                return new GuiSettings(windowWidth, windowHeight, windowX, windowY);
            }
            // Fall back to defaults for coordinates when they were not stored.
            return new GuiSettings(windowWidth, windowHeight, 0, 0);
        }
    }
}
