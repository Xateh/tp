package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.ui.UiManager;

public class InfoEditCommandTest {

    private Model model;
    private TestUiManager testUiManager;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        testUiManager = new TestUiManager();
        InfoEditCommand.setUiManager(testUiManager);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        InfoEditCommand infoEditCommand = new InfoEditCommand(INDEX_FIRST_PERSON);

        CommandResult commandResult = infoEditCommand.execute(model);

        assertEquals(String.format(InfoEditCommand.MESSAGE_INFO_EDIT_SUCCESS,
                Messages.format(personToEdit)), commandResult.getFeedbackToUser());
        assertTrue(testUiManager.showInfoEditorCalled.get());
        assertEquals(personToEdit, testUiManager.lastPersonEdited.get());
        assertEquals(INDEX_FIRST_PERSON.getZeroBased(), testUiManager.lastPersonIndex.get());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        InfoEditCommand infoEditCommand = new InfoEditCommand(outOfBoundIndex);

        assertThrows(CommandException.class, () -> infoEditCommand.execute(model));
    }

    @Test
    public void execute_nullUiManager_throwsCommandException() {
        InfoEditCommand.setUiManager(null);
        InfoEditCommand infoEditCommand = new InfoEditCommand(INDEX_FIRST_PERSON);

        CommandException exception = assertThrows(CommandException.class, (
        ) -> infoEditCommand.execute(model));
        assertEquals("UI Manager not initialized. Cannot open info editor.", exception.getMessage());

        // Restore for other tests
        InfoEditCommand.setUiManager(testUiManager);
    }

    @Test
    public void equals() {
        InfoEditCommand infoEditFirstCommand = new InfoEditCommand(INDEX_FIRST_PERSON);
        InfoEditCommand infoEditSecondCommand = new InfoEditCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(infoEditFirstCommand.equals(infoEditFirstCommand));

        // same values -> returns true
        InfoEditCommand infoEditFirstCommandCopy = new InfoEditCommand(INDEX_FIRST_PERSON);
        assertTrue(infoEditFirstCommand.equals(infoEditFirstCommandCopy));

        // different types -> returns false
        assertFalse(infoEditFirstCommand.equals(1));

        // null -> returns false
        assertFalse(infoEditFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(infoEditFirstCommand.equals(infoEditSecondCommand));
    }

    /**
     * Test UiManager that tracks method calls
     */
    private static class TestUiManager extends UiManager {
        private AtomicBoolean showInfoEditorCalled = new AtomicBoolean(false);
        private AtomicReference<Person> lastPersonEdited = new AtomicReference<>();
        private AtomicReference<Integer> lastPersonIndex = new AtomicReference<>();

        public TestUiManager() {
            super(null);
        }

        @Override
        public void showInfoEditor(Person person, int personIndex) {
            showInfoEditorCalled.set(true);
            lastPersonEdited.set(person);
            lastPersonIndex.set(personIndex);
        }

        @Override
        public void start(javafx.stage.Stage primaryStage) {
            // Do nothing for tests
        }
    }
}
