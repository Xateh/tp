package seedu.address.session;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Immutable snapshot of a user session, capturing transient data that should persist across runs.
 */
public class SessionData {

    public static final String FORMAT_VERSION = "2.0";

    private final Instant savedAt;
    private final AddressBook addressBook;
    private final GuiSettings guiSettings;

    /**
     * Constructs a {@code SessionData} snapshot with persisted session attributes.
     *
     * @param savedAt time the snapshot was captured
     * @param addressBook the address book state associated with this session
     * @param guiSettings GUI settings to restore on the next launch
     */
    public SessionData(Instant savedAt, ReadOnlyAddressBook addressBook, GuiSettings guiSettings) {
        this.savedAt = requireNonNull(savedAt);
        requireNonNull(addressBook);
        this.addressBook = new AddressBook(addressBook);
        this.guiSettings = requireNonNull(guiSettings);
    }

    public String getFormatVersion() {
        return FORMAT_VERSION;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
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
            && addressBook.equals(otherData.addressBook)
            && guiSettings.equals(otherData.guiSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedAt, addressBook, guiSettings);
    }

    @Override
    public String toString() {
        return "SessionData{"
                + "formatVersion='" + FORMAT_VERSION + '\''
                + ", savedAt=" + savedAt
                + ", addressBookPersons=" + addressBook.getPersonList().size()
                + ", guiSettings=" + guiSettings
                + '}';
    }
}
