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
        SessionRecorder recorder = new SessionRecorder();
        assertFalse(recorder.isAddressBookDirty());
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void markSnapshotPersisted_calledMultipleTimes_idempotent() {
        SessionRecorder recorder = new SessionRecorder();
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty());
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty());
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void findAndListCommand_interleaving_keywordsAndDirtyFlag() {
        SessionRecorder recorder = new SessionRecorder();
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);
        assertEquals(keywords, recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS).getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty());
        recorder.afterSuccessfulCommand(new ListCommand(), false);
        assertTrue(recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS).getSearchKeywords().isEmpty());
        assertFalse(recorder.isAddressBookDirty());
    }


    private AddressBook createSampleAddressBook() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().build());
        return book;
    }

    @Test
    public void constructor_withoutSession_initialisesCleanState() {
        SessionRecorder recorder = new SessionRecorder();
        SessionData snapshot = recorder.buildSnapshot(createSampleAddressBook(), DEFAULT_GUI_SETTINGS);

        assertTrue(snapshot.getSearchKeywords().isEmpty());
        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void constructor_withExistingSession_restoresKeywords() {
        AddressBook addressBook = createSampleAddressBook();
        SessionData previous = new SessionData(
                Instant.parse("2024-01-01T10:10:00Z"),
                addressBook,
                List.of("alice", "bob"),
                DEFAULT_GUI_SETTINGS);

        SessionRecorder recorder = new SessionRecorder(Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(List.of("alice", "bob"), snapshot.getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void afterSuccessfulCommand_marksDirtyWhenAddressBookChanges() {
        SessionRecorder recorder = new SessionRecorder();
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);

        assertTrue(recorder.isAddressBookDirty());
    }

    @Test
    public void afterSuccessfulCommand_nonMutatingCommandRetainsCleanFlag() {
        SessionRecorder recorder = new SessionRecorder();
        recorder.afterSuccessfulCommand(new ListCommand(), false);

        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void afterSuccessfulCommand_findUpdatesKeywordsWithoutDirtyFlag() {
        SessionRecorder recorder = new SessionRecorder();
        List<String> keywords = List.of("alice", "bob");
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(keywords)), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot.getSearchKeywords());
        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void afterSuccessfulCommand_listClearsKeywords() {
        SessionRecorder recorder = new SessionRecorder();
        recorder.afterSuccessfulCommand(new FindCommand(new FieldContainsKeywordsPredicate(List.of("alice"))), false);

        recorder.afterSuccessfulCommand(new ListCommand(), false);

        SessionData snapshot = recorder.buildSnapshot(new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertTrue(snapshot.getSearchKeywords().isEmpty());
    }

    @Test
    public void markSnapshotPersisted_resetsDirtyFlag() {
        SessionRecorder recorder = new SessionRecorder();
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().build()), true);
        assertTrue(recorder.isAddressBookDirty());

        recorder.markSnapshotPersisted();

        assertFalse(recorder.isAddressBookDirty());
    }

    @Test
    public void buildSnapshot_containsAddressBookState() {
        SessionRecorder recorder = new SessionRecorder();
        AddressBook addressBook = createSampleAddressBook();

        SessionData snapshot = recorder.buildSnapshot(addressBook, DEFAULT_GUI_SETTINGS);

        assertEquals(addressBook.getPersonList(), snapshot.getAddressBook().getPersonList());
        assertEquals(DEFAULT_GUI_SETTINGS, snapshot.getGuiSettings());
    }

    @Test
    public void dirtyFlag_andSnapshotReflectsAddressBookMutation() {
        SessionRecorder recorder = new SessionRecorder();
        AddressBook ab = createSampleAddressBook();
        // Simulate a mutating command
        recorder.afterSuccessfulCommand(new AddCommand(new PersonBuilder().withName("Bob").build()), true);
        assertTrue(recorder.isAddressBookDirty());

        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(ab, new AddressBook(snapshot.getAddressBook()));
        recorder.markSnapshotPersisted();
        assertFalse(recorder.isAddressBookDirty());
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
        SessionRecorder recorder = new SessionRecorder(Optional.of(previous));
        SessionData snapshot = recorder.buildSnapshot(ab, DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot.getSearchKeywords());
    }
}

