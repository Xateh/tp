package seedu.address.logic.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

/**
 * Clean, focused tests for {@link SessionRecorder} behaviours.
 */
public class SessionRecorderTest {

    private static final GuiSettings DEFAULT_GUI_SETTINGS = new GuiSettings();

    @Test
    public void markSnapshotPersisted_withoutDirtyFlag_noEffect() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void markSnapshotPersisted_calledMultipleTimes_idempotent() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        // simulate a command that mutates the address book
        AddressBook mutated = new AddressBook();
        mutated.addPerson(new PersonBuilder().build());
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        // the recorder should report the address book as dirty when compared to the current state
        assertTrue(recorder.isAddressBookDirty(mutated));
        recorder.markSnapshotPersisted();
        // after marking persisted, it should no longer be dirty
        assertFalse(recorder.isAddressBookDirty(mutated));
        // idempotent: repeated calls should have no effect
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(mutated));
    }

    @Test
    public void constructor_withExistingSession_doesNotRestoreKeywords() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().build());

        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                addressBook,
                List.of("alice", "bob"),
                DEFAULT_GUI_SETTINGS);

        SessionRecorder recorder = new SessionRecorder(addressBook, DEFAULT_GUI_SETTINGS, Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        // Previously-saved keywords should not be restored in the persisted snapshot
        assertTrue(snapshot.getSearchKeywords().isEmpty());
        assertFalse(recorder.isAddressBookDirty(addressBook));
    }

    @Test
    public void afterSuccessfulCommand_marksDirtyWhenAddressBookChanges() {
        AddressBook current = new AddressBook();
        current.addPerson(new PersonBuilder().build());
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);

        assertTrue(recorder.isAddressBookDirty(current));
    }

    @Test
    public void findCommand_doesNotPersistKeywords() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        List<String> keywords = List.of("alice");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertTrue(snapshot.getSearchKeywords().isEmpty());
    }

    @Test
    public void findCommand_capturesFlagsAndCustomKeys() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(
                List.of("alice"), /*name*/false, /*phone*/true, /*email*/false, /*address*/true, /*tag*/false,
                Set.of("company", "assetclass")
        );
        recorder.afterSuccessfulCommand(new FindCommand(predicate), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        // Keywords are not persisted, but flags/custom keys should be present in the snapshot
        assertTrue(snapshot.getSearchKeywords().isEmpty());
        assertFalse(snapshot.isSearchName());
        assertTrue(snapshot.isSearchPhone());
        assertFalse(snapshot.isSearchEmail());
        assertTrue(snapshot.isSearchAddress());
        assertFalse(snapshot.isSearchTag());
        assertEquals(Set.of("company", "assetclass"), Set.copyOf(snapshot.getCustomKeys()));
    }

    @Test
    public void metadataChange_thenRevert_notDirty() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(List.of("alice"))), false);
        // metadata change only should not mark address book dirty
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void afterGuiSettingsChangedMarksMetadataDirtyAndMarkSnapshotPersistedClearsIt() {
        AddressBook ab = new AddressBook();
        ab.addPerson(new PersonBuilder().build());

        // seed recorder with an initial persisted session so lastPersistedSignature is set
        SessionData persisted = new SessionData(
            Instant.now(),
            ab,
            java.util.List.of(),
            new GuiSettings(600, 400, 0, 0)
        );
        SessionRecorder recorder = new SessionRecorder(
            ab,
            new GuiSettings(600, 400, 0, 0),
            Optional.of(persisted)
        );

        // change GUI settings
        GuiSettings newGui = new GuiSettings(800, 600, 10, 10);
        recorder.afterGuiSettingsChanged(newGui);

        // currentAddressBook equals persisted address book; metadata change should be reported
        assertTrue(recorder.isAnyDirty(ab, newGui));

        // simulate persisting snapshot
        recorder.markSnapshotPersisted();
        // after persisting, nothing should be dirty
        assertFalse(recorder.isAnyDirty(ab, newGui));
    }

    @Test
    void metadataChange_onlyDoesNotMarkAddressBookDirty() {
        AddressBook ab = new AddressBook();
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), new GuiSettings());

        recorder.afterSuccessfulCommand(new seedu.address.logic.commands.FindCommand(
                new seedu.address.model.person.FieldContainsKeywordsPredicate(java.util.List.of("x"))), false);

        // metadata change only should not mark address book dirty when compared to new AddressBook
        assertFalse(recorder.isAddressBookDirty(ab));
    }
}
