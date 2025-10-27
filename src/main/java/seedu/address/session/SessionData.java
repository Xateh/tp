package seedu.address.session;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.List;
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
    private final List<String> searchKeywords;
    private final boolean searchName;
    private final boolean searchPhone;
    private final boolean searchEmail;
    private final boolean searchAddress;
    private final boolean searchTag;
    private final List<String> customKeys;
    private final GuiSettings guiSettings;

    /**
     * Constructs a {@code SessionData} snapshot with all persisted session attributes.
     *
     * @param savedAt time the snapshot was captured
     * @param addressBook the address book state associated with this session
     * @param searchKeywords active search keywords when the snapshot was taken
     * @param guiSettings GUI settings to restore on the next launch
     */
    public SessionData(Instant savedAt, ReadOnlyAddressBook addressBook,
        List<String> searchKeywords, boolean searchName, boolean searchPhone,
        boolean searchEmail, boolean searchAddress, boolean searchTag,
        List<String> customKeys, GuiSettings guiSettings) {
        this.savedAt = requireNonNull(savedAt);
        requireNonNull(addressBook);
        this.addressBook = new AddressBook(addressBook);
        this.searchKeywords = List.copyOf(requireNonNull(searchKeywords));
        this.searchName = searchName;
        this.searchPhone = searchPhone;
        this.searchEmail = searchEmail;
        this.searchAddress = searchAddress;
        this.searchTag = searchTag;
        this.customKeys = List.copyOf(requireNonNull(customKeys));
        this.guiSettings = requireNonNull(guiSettings);
    }

    /**
     * Backwards-compatible constructor that preserves previous behaviour.
     * Defaults to searching all non-custom fields and no custom keys.
     */
    public SessionData(Instant savedAt, ReadOnlyAddressBook addressBook,
            List<String> searchKeywords, GuiSettings guiSettings) {
        this(savedAt, addressBook, searchKeywords, true, true, true, true, true,
                List.of(), guiSettings);
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

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public boolean isSearchName() {
        return searchName;
    }

    public boolean isSearchPhone() {
        return searchPhone;
    }

    public boolean isSearchEmail() {
        return searchEmail;
    }

    public boolean isSearchAddress() {
        return searchAddress;
    }

    public boolean isSearchTag() {
        return searchTag;
    }

    public List<String> getCustomKeys() {
        return customKeys;
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
            && searchKeywords.equals(otherData.searchKeywords)
            && searchName == otherData.searchName
            && searchPhone == otherData.searchPhone
            && searchEmail == otherData.searchEmail
            && searchAddress == otherData.searchAddress
            && searchTag == otherData.searchTag
            && customKeys.equals(otherData.customKeys)
            && guiSettings.equals(otherData.guiSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedAt, addressBook, searchKeywords, searchName, searchPhone,
            searchEmail, searchAddress, searchTag, customKeys, guiSettings);
    }

    @Override
    public String toString() {
        return "SessionData{"
                + "formatVersion='" + FORMAT_VERSION + '\''
                + ", savedAt=" + savedAt
                + ", addressBookPersons=" + addressBook.getPersonList().size()
                + ", searchKeywords=" + searchKeywords
                + ", searchName=" + searchName
                + ", searchPhone=" + searchPhone
                + ", searchEmail=" + searchEmail
                + ", searchAddress=" + searchAddress
                + ", searchTag=" + searchTag
                + ", customKeys=" + customKeys
                + ", guiSettings=" + guiSettings
                + '}';
    }
}
