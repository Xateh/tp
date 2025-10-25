package seedu.address.logic.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

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
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty(createSampleAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
    }

    @Test
    public void findAndListCommand_interleaving_keywordsAndDirtyFlag() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);
        assertEquals(keywords, recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS).getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
        recorder.afterSuccessfulCommand(new ListCommand(), false);
        assertTrue(recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS).getSearchKeywords().isEmpty());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }


    private AddressBook createSampleAddressBook() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().build());
        return book;
    }

    @Test
    public void constructor_withoutSession_initialisesCleanState() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        SessionData snapshot = recorder.buildSnapshot(createSampleAddressBook(), DEFAULT_GUI_SETTINGS);

        assertTrue(snapshot.getSearchKeywords().isEmpty());
        assertFalse(recorder.isAddressBookDirty(createSampleAddressBook()));
    }

    @Test
    public void constructor_withExistingSession_restoresKeywords() {
        AddressBook addressBook = createSampleAddressBook();
        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                addressBook,
                List.of("alice", "bob"),
                DEFAULT_GUI_SETTINGS);

        SessionRecorder recorder = new SessionRecorder(addressBook, DEFAULT_GUI_SETTINGS, Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(List.of("alice", "bob"), snapshot.getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty(addressBook));
    }

    @Test
    public void afterSuccessfulCommand_marksDirtyWhenAddressBookChanges() {
        AddressBook current = createSampleAddressBook();
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);

        assertTrue(recorder.isAddressBookDirty(current));
    }

    @Test
    public void afterSuccessfulCommand_nonMutatingCommandRetainsCleanFlag() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new ListCommand(), false);

        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void afterSuccessfulCommand_findUpdatesKeywordsWithoutDirtyFlag() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot.getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty(new AddressBook()));
    }

    @Test
    public void afterSuccessfulCommand_listClearsKeywords() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(List.of("alice"))), false);

        recorder.afterSuccessfulCommand(new ListCommand(), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertTrue(snapshot.getSearchKeywords().isEmpty());
    }

    @Test
    public void metadataChange_thenRevert_notDirty() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(List.of("alice"))), false);
        assertTrue(recorder.isAnyDirty(new AddressBook(), DEFAULT_GUI_SETTINGS));

        recorder.afterSuccessfulCommand(new ListCommand(), false);
        assertFalse(recorder.isAnyDirty(new AddressBook(), DEFAULT_GUI_SETTINGS));
    }

    @Test
    public void guiSettingsChange_onlyMarksDirtyWhenValueDiffers() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);

        GuiSettings updatedSettings = new GuiSettings(1024, 768, 10, 10);
        recorder.afterGuiSettingsChanged(updatedSettings);
        assertTrue(recorder.isAnyDirty(new AddressBook(), updatedSettings));

        // Setting the same value again should not clear the dirty flag prematurely
        recorder.afterGuiSettingsChanged(updatedSettings);
        assertTrue(recorder.isAnyDirty(new AddressBook(), updatedSettings));

        // Reverting to baseline clears the metadata dirty flag
        recorder.afterGuiSettingsChanged(DEFAULT_GUI_SETTINGS);
        assertFalse(recorder.isAnyDirty(new AddressBook(), DEFAULT_GUI_SETTINGS));
    }

    @Test
    public void markSnapshotPersisted_resetsDirtyFlag() {
        AddressBook current = createSampleAddressBook();
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty(current));

        recorder.markSnapshotPersisted();

        assertFalse(recorder.isAddressBookDirty(current));
    }

    @Test
    public void buildSnapshot_containsAddressBookState() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        AddressBook addressBook = createSampleAddressBook();

        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(addressBook.getPersonList(), snapshot.getAddressBook().getPersonList());
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
    }

    @Test
    public void dirtyFlag_andSnapshotReflectsAddressBookMutation() {
        SessionRecorder recorder = new SessionRecorder(new AddressBook(), DEFAULT_GUI_SETTINGS);
        AddressBook ab = createSampleAddressBook();
        // Simulate a mutating command
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().withName("Bob").build()), true);
        assertTrue(recorder.isAddressBookDirty(ab));

        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(ab, new AddressBook(snapshot.getAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty(ab));
    }

    @Test
    public void searchKeywords_restoredFromSessionData() {
        AddressBook ab = createSampleAddressBook();
        List<String> keywords = List.of("alice", "bob");
        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                ab,
                keywords,
                DEFAULT_GUI_SETTINGS);
        SessionRecorder recorder = new SessionRecorder(ab, DEFAULT_GUI_SETTINGS, Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot.getSearchKeywords());
    }
}

