package seedu.address.storage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

/**
 * Json representation of {@link SessionData} for persistence.
 */
@JsonRootName(value = "session")
class JsonSerializableSession {

    private static final String MESSAGE_MISSING_FIELD = "Session file is missing the '%s' field.";

    @SuppressWarnings("unused")
    private final String formatVersion;
    private final String savedAt;
    private final String addressBookPath;
    private final JsonSerializableAddressBook addressBook;
    private final List<String> searchKeywords;
    private final List<JsonSessionCommand> commandHistory;
    private final JsonGuiSettings guiSettings;

    @JsonCreator
    JsonSerializableSession(@JsonProperty("formatVersion") String formatVersion,
            @JsonProperty("savedAt") String savedAt,
            @JsonProperty("addressBookPath") String addressBookPath,
            @JsonProperty("addressBook") JsonSerializableAddressBook addressBook,
            @JsonProperty("searchKeywords") List<String> searchKeywords,
            @JsonProperty("commandHistory") List<JsonSessionCommand> commandHistory,
            @JsonProperty("guiSettings") JsonGuiSettings guiSettings) {
        this.formatVersion = formatVersion;
        this.savedAt = savedAt;
        this.addressBookPath = addressBookPath;
        this.addressBook = addressBook;
        this.searchKeywords = searchKeywords != null ? new ArrayList<>(searchKeywords) : new ArrayList<>();
        this.commandHistory = commandHistory != null ? new ArrayList<>(commandHistory) : new ArrayList<>();
        this.guiSettings = guiSettings;
    }

    JsonSerializableSession(SessionData source) {
        this.formatVersion = source.getFormatVersion();
        this.savedAt = source.getSavedAt().toString();
        this.addressBookPath = source.getAddressBookPath().toString();
        this.addressBook = new JsonSerializableAddressBook(source.getAddressBook());
        this.searchKeywords = new ArrayList<>(source.getSearchKeywords());
        this.commandHistory = source.getCommandHistory().stream()
                .map(JsonSessionCommand::new)
                .collect(Collectors.toList());
        this.guiSettings = new JsonGuiSettings(source.getGuiSettings());
    }

    SessionData toModelType() throws IllegalValueException {
        if (savedAt == null) {
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "savedAt"));
        }

        if (addressBookPath == null) {
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "addressBookPath"));
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

    Path sessionAddressBookPath = Paths.get(addressBookPath);
    AddressBook modelAddressBook = addressBook.toModelType();

        List<SessionCommand> modelCommands = new ArrayList<>();
        for (JsonSessionCommand command : commandHistory) {
            modelCommands.add(command.toModelType());
        }

        GuiSettings modelGuiSettings = guiSettings.toModelType();

        return new SessionData(parsedSavedAt, sessionAddressBookPath, modelAddressBook,
                searchKeywords, modelCommands, modelGuiSettings);
    }

    private static class JsonSessionCommand {
        private final String timestamp;
        private final String commandText;

        @JsonCreator
        JsonSessionCommand(@JsonProperty("timestamp") String timestamp,
                @JsonProperty("commandText") String commandText) {
            this.timestamp = timestamp;
            this.commandText = commandText;
        }

        JsonSessionCommand(SessionCommand source) {
            this.timestamp = source.getTimestamp().toString();
            this.commandText = source.getCommandText();
        }

        SessionCommand toModelType() throws IllegalValueException {
            if (timestamp == null) {
                throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "commandHistory.timestamp"));
            }
            if (commandText == null) {
                throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, "commandHistory.commandText"));
            }
            Instant parsedTimestamp;
            try {
                parsedTimestamp = Instant.parse(timestamp);
            } catch (Exception ex) {
                throw new IllegalValueException("Invalid timestamp format for commandHistory entry: " + timestamp);
            }
            return new SessionCommand(parsedTimestamp, commandText);
        }
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
