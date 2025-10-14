package seedu.address.session;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Immutable snapshot of a user session, capturing transient data that should persist across runs.
 */
public class SessionData {

    public static final String FORMAT_VERSION = "1.0";

    private final Instant savedAt;
    private final Path addressBookPath;
    private final List<String> searchKeywords;
    private final List<SessionCommand> commandHistory;
    private final GuiSettings guiSettings;

    /**
     * Constructs a {@code SessionData} snapshot with all persisted session attributes.
     *
     * @param savedAt time the snapshot was captured
     * @param addressBookPath path to the address book associated with this session
     * @param searchKeywords active search keywords when the snapshot was taken
     * @param commandHistory history of executed commands
     * @param guiSettings GUI settings to restore on the next launch
     */
    public SessionData(Instant savedAt, Path addressBookPath, List<String> searchKeywords,
        List<SessionCommand> commandHistory, GuiSettings guiSettings) {
        this.savedAt = requireNonNull(savedAt);
        this.addressBookPath = requireNonNull(addressBookPath);
        this.searchKeywords = List.copyOf(requireNonNull(searchKeywords));
        this.commandHistory = List.copyOf(requireNonNull(commandHistory));
        this.guiSettings = requireNonNull(guiSettings);
    }

    public String getFormatVersion() {
        return FORMAT_VERSION;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public Path getAddressBookPath() {
        return addressBookPath;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public List<SessionCommand> getCommandHistory() {
        return commandHistory;
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SessionData)) {
            return false;
        }

        SessionData otherData = (SessionData) other;
        return savedAt.equals(otherData.savedAt)
                && addressBookPath.equals(otherData.addressBookPath)
                && searchKeywords.equals(otherData.searchKeywords)
                && commandHistory.equals(otherData.commandHistory)
                && guiSettings.equals(otherData.guiSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedAt, addressBookPath, searchKeywords, commandHistory, guiSettings);
    }

    @Override
    public String toString() {
        return "SessionData{"
                + "formatVersion='" + FORMAT_VERSION + '\''
                + ", savedAt=" + savedAt
                + ", addressBookPath=" + addressBookPath
                + ", searchKeywords=" + searchKeywords
                + ", commandHistorySize=" + commandHistory.size()
                + ", guiSettings=" + guiSettings
                + '}';
    }
}
