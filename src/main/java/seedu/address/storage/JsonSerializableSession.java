package seedu.address.storage;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionData;

/**
 * Json representation of {@link SessionData} for persistence.
 *
 * Persisted session JSON schema contains only:
 * - formatVersion
 * - savedAt
 * - addressBook
 * - guiSettings
 */
@JsonRootName(value = "session")
class JsonSerializableSession {

    private static final String MESSAGE_MISSING_FIELD = "Session file is missing the '%s' field.";

    private final String formatVersion;
    private final String savedAt;
    private final JsonSerializableAddressBook addressBook;
    private final JsonGuiSettings guiSettings;

    /**
     * Jackson-friendly constructor for Json deserialization.
     */
    @JsonCreator
    public JsonSerializableSession(@JsonProperty("formatVersion") String formatVersion,
            @JsonProperty("savedAt") String savedAt,
            @JsonProperty("addressBook") JsonSerializableAddressBook addressBook,
            @JsonProperty("guiSettings") JsonGuiSettings guiSettings) {
        this.formatVersion = formatVersion;
        this.savedAt = savedAt;
        this.addressBook = addressBook;
        this.guiSettings = guiSettings;
    }

    /**
     * Converts a {@link SessionData} into this serializable form.
     */
    public JsonSerializableSession(SessionData source) {
        this.formatVersion = source.getFormatVersion();
        this.savedAt = source.getSavedAt().toString();
        this.addressBook = new JsonSerializableAddressBook(source.getAddressBook());
        this.guiSettings = new JsonGuiSettings(source.getGuiSettings());
    }

    /**
     * Converts this Jackson-friendly object into the model's {@link SessionData}.
     */
    public SessionData toModelType() throws IllegalValueException {
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

        // Persisted session JSON only includes formatVersion, savedAt, addressBook and guiSettings.
        // Restore those and use defaults for other session metadata (empty keywords, default flags).
        return new SessionData(parsedSavedAt, modelAddressBook, List.of(),
            true, true, true, true, true,
            List.of(), modelGuiSettings);
    }

    private static final class JsonGuiSettings {
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

            // Compute center of the screen when coordinates were not stored.
            try {
                java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                int centerX = (int) Math.round((screen.getWidth() - windowWidth) / 2.0);
                int centerY = (int) Math.round((screen.getHeight() - windowHeight) / 2.0);
                return new GuiSettings(windowWidth, windowHeight, centerX, centerY);
            } catch (RuntimeException e) {
                // Fall back to origin when screen size is unavailable.
                return new GuiSettings(windowWidth, windowHeight, 0, 0);
            }
        }
    }
}

