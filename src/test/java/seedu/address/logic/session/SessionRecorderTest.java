package seedu.address.logic.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

class SessionRecorderTest {

    private static final GuiSettings GUI_SETTINGS = new GuiSettings(800, 600, 10, 10);
    private static final Path ADDRESS_BOOK_PATH = Path.of("data", "address.json");

    @Test
    void constructor_withInitialSessionCopiesState() {
        SessionCommand previousCommand = new SessionCommand(
                Instant.parse("2025-10-14T00:00:00Z"), "list");
        SessionData initial = new SessionData(
                Instant.parse("2025-10-14T00:00:00Z"),
                ADDRESS_BOOK_PATH,
                List.of("Alice"),
                List.of(previousCommand),
                GUI_SETTINGS);

        SessionRecorder recorder = new SessionRecorder(Optional.of(initial));
        SessionData snapshot = recorder.buildSnapshot(ADDRESS_BOOK_PATH, GUI_SETTINGS);

        assertEquals(List.of("Alice"), snapshot.getSearchKeywords());
        assertEquals(1, snapshot.getCommandHistory().size());
        assertEquals("list", snapshot.getCommandHistory().get(0).getCommandText());
    }

    @Test
    void afterSuccessfulCommand_withFindCommandTracksKeywords() {
        SessionRecorder recorder = new SessionRecorder();
        FindCommand findCommand = new FindCommand(new FieldContainsKeywordsPredicate(List.of("Alice")));

        recorder.afterSuccessfulCommand("find Alice", findCommand);
        SessionData snapshot = recorder.buildSnapshot(ADDRESS_BOOK_PATH, GUI_SETTINGS);

        assertEquals(List.of("Alice"), snapshot.getSearchKeywords());
        assertEquals("find Alice", snapshot.getCommandHistory().get(0).getCommandText());
    }

    @Test
    void afterSuccessfulCommand_withListCommandClearsKeywords() {
        SessionRecorder recorder = new SessionRecorder();
        FindCommand findCommand = new FindCommand(new FieldContainsKeywordsPredicate(List.of("Alice")));
        recorder.afterSuccessfulCommand("find Alice", findCommand);

        recorder.afterSuccessfulCommand(ListCommand.COMMAND_WORD, new ListCommand());
        SessionData snapshot = recorder.buildSnapshot(ADDRESS_BOOK_PATH, GUI_SETTINGS);

        assertTrue(snapshot.getSearchKeywords().isEmpty());
        assertEquals(2, snapshot.getCommandHistory().size());
        assertEquals(ListCommand.COMMAND_WORD, snapshot.getCommandHistory().get(1).getCommandText());
    }
}
