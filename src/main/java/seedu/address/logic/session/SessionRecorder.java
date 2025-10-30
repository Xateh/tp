package seedu.address.logic.session;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionData;

/**
 * Collects transient session information during program execution so it can be persisted on shutdown.
 */
public class SessionRecorder {

    private List<String> searchKeywords = Collections.emptyList();
    private boolean searchName = true;
    private boolean searchPhone = true;
    private boolean searchEmail = true;
    private boolean searchAddress = true;
    private boolean searchTag = true;
    private List<String> customKeys = Collections.emptyList();
    private GuiSettings currentGuiSettings = new GuiSettings();
    private boolean addressBookDirty;
    /** Tracks non-address-book session state changes (e.g. search keywords, gui settings). */
    private boolean sessionMetadataDirty;
    private SessionSignature lastPersistedSignature;
    private SessionSignature lastBuiltSnapshotSignature;

    /**
     * Creates a {@code SessionRecorder} with no existing session data.
     */
    public SessionRecorder() {
        this(new AddressBook(), new GuiSettings(), Optional.empty());
    }

    /**
     * Creates a {@code SessionRecorder} seeded with a previously saved session.
     *
     * @param initialSession snapshot to hydrate from before recording new commands
     */
    public SessionRecorder(Optional<SessionData> initialSession) {
        this(new AddressBook(), new GuiSettings(), initialSession);
    }

    /**
     * Creates a {@code SessionRecorder} with the provided initial state.
     *
     * @param initialAddressBook current address book state when the recorder is created
     * @param initialGuiSettings current GUI settings at recorder creation
     */
    public SessionRecorder(ReadOnlyAddressBook initialAddressBook, GuiSettings initialGuiSettings) {
        this(initialAddressBook, initialGuiSettings, Optional.empty());
    }

    /**
     * Creates a {@code SessionRecorder} seeded with optional previously saved session data and
     * aware of the current in-memory state of the application.
     */
    public SessionRecorder(ReadOnlyAddressBook initialAddressBook, GuiSettings initialGuiSettings,
            Optional<SessionData> initialSession) {
        // Do not restore persisted search keywords or GUI settings. We intentionally ignore
        // any saved keywords and GUI state so the application does not restore prior GUI
        // state or previously-entered search terms on startup.
        initialSession.ifPresent(session -> {
            // Hydrate current GUI settings from persisted session so runtime matches persisted state.
            currentGuiSettings = session.getGuiSettings();
            // Capture the persisted address book and GUI settings for equality comparisons.
            lastPersistedSignature = new SessionSignature(session.getAddressBook(), currentGuiSettings);
        });

        if (initialSession.isEmpty()) {
            currentGuiSettings = initialGuiSettings;
            lastPersistedSignature = new SessionSignature(initialAddressBook, currentGuiSettings);
        }

        addressBookDirty = false;
        sessionMetadataDirty = false;
        lastBuiltSnapshotSignature = null;

        recomputeSessionMetadataDirty();
    }

    /** Records a successfully executed command and updates session metadata if required. */
    public void afterSuccessfulCommand(Command command, boolean addressBookMutated) {
        if (addressBookMutated) {
            addressBookDirty = true;
        }

        if (command instanceof FindCommand) {
            FindCommand findCommand = (FindCommand) command;
            searchKeywords = List.copyOf(findCommand.getPredicate().getKeywords());
            // capture flags and custom keys from predicate
            FieldContainsKeywordsPredicate predicate = findCommand.getPredicate();
            searchName = predicate.isSearchName();
            searchPhone = predicate.isSearchPhone();
            searchEmail = predicate.isSearchEmail();
            searchAddress = predicate.isSearchAddress();
            searchTag = predicate.isSearchTag();
            customKeys = List.copyOf(predicate.getCustomKeys());
            recomputeSessionMetadataDirty();
            return;
        }

        if (command instanceof ListCommand) {
            searchKeywords = Collections.emptyList();
            recomputeSessionMetadataDirty();
        }
    }

    /**
     * Notify the recorder that GUI settings have changed and should be persisted.
     * This method should be invoked by the logic layer whenever GUI settings are updated so
     * the recorder can mark session metadata as dirty and ensure the session snapshot will be
     * considered for persistence on shutdown.
     */
    public void afterGuiSettingsChanged(GuiSettings newGuiSettings) {
        currentGuiSettings = newGuiSettings;
        recomputeSessionMetadataDirty();
    }

    /** Creates an immutable snapshot of the current session state. */
    public SessionData buildSnapshot(ReadOnlyAddressBook addressBook, GuiSettings guiSettings) {
        // When building a persisted snapshot, exclude search keywords but persist GUI settings.
        currentGuiSettings = guiSettings; // keep runtime value
        lastBuiltSnapshotSignature = new SessionSignature(addressBook, currentGuiSettings);
        return new SessionData(Instant.now(), addressBook,
            Collections.emptyList(), /* do not persist search keywords */
            searchName, searchPhone, searchEmail, searchAddress, searchTag,
            customKeys,
            currentGuiSettings /* persist GUI settings so GUI changes get saved, but will not be restored */);
    }

    /** Returns {@code true} if the address book changed since the last persisted snapshot. */
    public boolean isAddressBookDirty(ReadOnlyAddressBook currentAddressBook) {
        if (!addressBookDirty) {
            return false;
        }

        if (lastPersistedSignature != null && lastPersistedSignature.hasSameAddressBook(currentAddressBook)) {
            addressBookDirty = false;
            return false;
        }

        return true;
    }

    /** Returns true if any part of the session (address book or session metadata) has changed. */
    public boolean isAnyDirty(ReadOnlyAddressBook currentAddressBook, GuiSettings guiSettings) {
        boolean addressBookChanged = isAddressBookDirty(currentAddressBook);

        if (sessionMetadataDirty && lastPersistedSignature != null
            && lastPersistedSignature.hasSameMetadata(guiSettings)) {
            sessionMetadataDirty = false;
        }

        return addressBookChanged || sessionMetadataDirty;
    }

    /** Marks the current session snapshot as persisted. */
    public void markSnapshotPersisted() {
        if (lastBuiltSnapshotSignature != null) {
            lastPersistedSignature = lastBuiltSnapshotSignature;
        }

        addressBookDirty = false;
        sessionMetadataDirty = false;
    }

    private void recomputeSessionMetadataDirty() {
        if (lastPersistedSignature == null) {
            // Without a persisted signature, only address book changes matter; metadata is clean.
            sessionMetadataDirty = false;
            return;
        }

        // Compare metadata while ignoring persisted search keywords (we never persist keywords).
        sessionMetadataDirty = !lastPersistedSignature.hasSameMetadata(currentGuiSettings);
    }

    /**
     * Captures persisted session attributes excluding the timestamp so we can compare prospective snapshots
     * without considering the save time.
     */
    private static final class SessionSignature {
        private final AddressBook addressBookSnapshot;
        private final GuiSettings guiSettingsSnapshot;

        private SessionSignature(ReadOnlyAddressBook addressBook, GuiSettings guiSettings) {
            this.addressBookSnapshot = new AddressBook(addressBook);
            this.guiSettingsSnapshot = guiSettings;
        }

        private boolean hasSameAddressBook(ReadOnlyAddressBook other) {
            return addressBookSnapshot.equals(new AddressBook(other));
        }

        private boolean hasSameMetadata(GuiSettings guiSettings) {
            return guiSettingsSnapshot.equals(guiSettings);
        }
    }
}
