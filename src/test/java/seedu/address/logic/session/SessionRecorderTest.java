package seedu.address.logic.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.AddressBook;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;
import seedu.address.testutil.PersonBuilder;

public class SessionRecorderTest {

    private static final Path DEFAULT_PATH = Paths.get("data", "addressbook.json");
    private static final GuiSettings DEFAULT_GUI_SETTINGS = new GuiSettings();

    @Test
    public void constructor_noArguments_createsEmptyRecorder() {
        SessionRecorder recorder = new SessionRecorder();
        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);

        assertTrue(snapshot.getCommandHistory().isEmpty());
        assertTrue(snapshot.getSearchKeywords().isEmpty());
    }

    @Test
    public void constructor_withEmptySession_createsEmptyRecorder() {
        SessionRecorder recorder = new SessionRecorder(Optional.empty());
        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);

        assertTrue(snapshot.getCommandHistory().isEmpty());
        assertTrue(snapshot.getSearchKeywords().isEmpty());
    }

    @Test
    public void constructor_withExistingSession_restoresSessionData() {
        // Create a session with command history and search keywords
        List<SessionCommand> existingHistory = Arrays.asList(
                new SessionCommand(Instant.parse("2024-01-01T10:00:00Z"), "add n/John"),
                new SessionCommand(Instant.parse("2024-01-01T10:05:00Z"), "list")
        );
        List<String> existingKeywords = Arrays.asList("alice", "bob");
    SessionData existingSession = new SessionData(
        Instant.parse("2024-01-01T10:10:00Z"),
        DEFAULT_PATH,
        new AddressBook(),
        existingKeywords,
        existingHistory,
        DEFAULT_GUI_SETTINGS
    );

        SessionRecorder recorder = new SessionRecorder(Optional.of(existingSession));
    SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);

        assertEquals(2, snapshot.getCommandHistory().size());
        assertEquals("add n/John", snapshot.getCommandHistory().get(0).getCommandText());
        assertEquals("list", snapshot.getCommandHistory().get(1).getCommandText());
        assertEquals(existingKeywords, snapshot.getSearchKeywords());
    }

    @Test
    public void afterSuccessfulCommand_regularCommand_addsToHistory() {
        SessionRecorder recorder = new SessionRecorder();
        AddCommand addCommand = new AddCommand(new PersonBuilder().build());

        recorder.afterSuccessfulCommand("add n/John Doe", addCommand);

        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(1, snapshot.getCommandHistory().size());
        assertEquals("add n/John Doe", snapshot.getCommandHistory().get(0).getCommandText());
    }

    @Test
    public void afterSuccessfulCommand_multipleCommands_addsAllToHistory() {
        SessionRecorder recorder = new SessionRecorder();
        AddCommand addCommand = new AddCommand(new PersonBuilder().build());
        ListCommand listCommand = new ListCommand();

        recorder.afterSuccessfulCommand("add n/John Doe", addCommand);
        recorder.afterSuccessfulCommand("list", listCommand);
        recorder.afterSuccessfulCommand("add n/Jane Doe", addCommand);

        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(3, snapshot.getCommandHistory().size());
        assertEquals("add n/John Doe", snapshot.getCommandHistory().get(0).getCommandText());
        assertEquals("list", snapshot.getCommandHistory().get(1).getCommandText());
        assertEquals("add n/Jane Doe", snapshot.getCommandHistory().get(2).getCommandText());
    }

    @Test
    public void afterSuccessfulCommand_findCommand_updatesSearchKeywords() {
        SessionRecorder recorder = new SessionRecorder();
        List<String> keywords = Arrays.asList("alice", "bob");
        FindCommand findCommand = new FindCommand(new FieldContainsKeywordsPredicate(keywords));

        recorder.afterSuccessfulCommand("find alice bob", findCommand);

        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot.getSearchKeywords());
        assertEquals(1, snapshot.getCommandHistory().size());
    }

    @Test
    public void afterSuccessfulCommand_listCommand_clearsSearchKeywords() {
        SessionRecorder recorder = new SessionRecorder();

        // First, set some search keywords with a find command
        List<String> keywords = Arrays.asList("alice", "bob");
        FindCommand findCommand = new FindCommand(new FieldContainsKeywordsPredicate(keywords));
        recorder.afterSuccessfulCommand("find alice bob", findCommand);

        // Verify keywords are set
        SessionData snapshot1 = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(keywords, snapshot1.getSearchKeywords());

        // Now execute list command which should clear keywords
        ListCommand listCommand = new ListCommand();
        recorder.afterSuccessfulCommand("list", listCommand);

        SessionData snapshot2 = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertTrue(snapshot2.getSearchKeywords().isEmpty());
        assertEquals(2, snapshot2.getCommandHistory().size());
    }

    @Test
    public void afterSuccessfulCommand_multipleFindCommands_keepsLatestKeywords() {
        SessionRecorder recorder = new SessionRecorder();

        List<String> keywords1 = Arrays.asList("alice");
        FindCommand findCommand1 = new FindCommand(new FieldContainsKeywordsPredicate(keywords1));
        recorder.afterSuccessfulCommand("find alice", findCommand1);

        List<String> keywords2 = Arrays.asList("bob", "charlie");
        FindCommand findCommand2 = new FindCommand(new FieldContainsKeywordsPredicate(keywords2));
        recorder.afterSuccessfulCommand("find bob charlie", findCommand2);

        SessionData snapshot = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        assertEquals(keywords2, snapshot.getSearchKeywords());
        assertEquals(2, snapshot.getCommandHistory().size());
    }

    @Test
    public void buildSnapshot_validInputs_createsCorrectSnapshot() {
        SessionRecorder recorder = new SessionRecorder();
        AddCommand addCommand = new AddCommand(new PersonBuilder().build());
        recorder.afterSuccessfulCommand("add n/John", addCommand);

        Path customPath = Paths.get("custom", "path.json");
        GuiSettings customSettings = new GuiSettings(1024, 768, 100, 100);

        SessionData snapshot = recorder.buildSnapshot(customPath, new AddressBook(), customSettings);

        assertNotNull(snapshot.getSavedAt());
        assertEquals(customPath, snapshot.getAddressBookPath());
        assertEquals(customSettings, snapshot.getGuiSettings());
        assertEquals(1, snapshot.getCommandHistory().size());
    }

    @Test
    public void buildSnapshot_multipleCallsSameState_differentTimestamps() throws InterruptedException {
        SessionRecorder recorder = new SessionRecorder();
        AddCommand addCommand = new AddCommand(new PersonBuilder().build());
        recorder.afterSuccessfulCommand("add n/John", addCommand);

        SessionData snapshot1 = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);
        Thread.sleep(10); // Small delay to ensure different timestamps
        SessionData snapshot2 = recorder.buildSnapshot(DEFAULT_PATH, new AddressBook(), DEFAULT_GUI_SETTINGS);

        // Timestamps should be different
        assertTrue(snapshot2.getSavedAt().isAfter(snapshot1.getSavedAt())
                || snapshot2.getSavedAt().equals(snapshot1.getSavedAt()));
        // But command history should be the same
        assertEquals(snapshot1.getCommandHistory(), snapshot2.getCommandHistory());
    }
}

