package seedu.address.logic.session;

import java.time.Instant;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
// search keywords are intentionally not persisted; Find/List command imports removed
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.session.SessionData;

/**
 * Collects transient session information during program execution so it can be persisted on shutdown.
 */
public class SessionRecorder {

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
        initialSession.ifPresent(session -> {
            // Do not restore search keywords from previous session. They are transient.
            currentGuiSettings = session.getGuiSettings();
            // The last persisted signature should reflect what was actually stored: keywords are
            // not persisted, so use an empty list for the persisted keywords snapshot.
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

        // Do not persist search keywords. They are runtime-only and should not mark session
        // metadata dirty for persistence purposes.
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
        currentGuiSettings = guiSettings;
        // When building a snapshot for persistence, exclude search keywords (transient).
        lastBuiltSnapshotSignature = new SessionSignature(addressBook, currentGuiSettings);
        return new SessionData(Instant.now(), addressBook, currentGuiSettings);
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
            // No persisted metadata yet — only GUI settings are considered for persistence.
            sessionMetadataDirty = true;
            return;
        }

        // Only GUI settings are considered part of session metadata for persistence.
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
