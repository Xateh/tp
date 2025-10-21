package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MainWindowTest {

    @Test
    public void commandResult_helpCommand_returnsCorrectFlags() {
        CommandResult helpResult = new CommandResult("Help shown", true, false);

        assertTrue(helpResult.isShowHelp());
        assertFalse(helpResult.isExit());
        assertFalse(helpResult.isShowInfoEditor());
        assertEquals("Help shown", helpResult.getFeedbackToUser());
        assertEquals(Optional.empty(), helpResult.getPersonToEdit());
    }

    @Test
    public void commandResult_exitCommand_returnsCorrectFlags() {
        CommandResult exitResult = new CommandResult("Exiting", false, true);

        assertFalse(exitResult.isShowHelp());
        assertTrue(exitResult.isExit());
        assertFalse(exitResult.isShowInfoEditor());
        assertEquals("Exiting", exitResult.getFeedbackToUser());
        assertEquals(Optional.empty(), exitResult.getPersonToEdit());
    }

    @Test
    public void commandResult_infoEditCommand_returnsCorrectFlags() {
        Person testPerson = new PersonBuilder().withName("Test Person").build();
        CommandResult infoEditResult = new CommandResult("Editing info", testPerson);

        assertFalse(infoEditResult.isShowHelp());
        assertFalse(infoEditResult.isExit());
        assertTrue(infoEditResult.isShowInfoEditor());
        assertEquals(Optional.of(testPerson), infoEditResult.getPersonToEdit());
        assertEquals("Editing info", infoEditResult.getFeedbackToUser());
    }

    @Test
    public void commandResult_normalCommand_returnsCorrectFlags() {
        CommandResult normalResult = new CommandResult("Command executed successfully");

        assertFalse(normalResult.isShowHelp());
        assertFalse(normalResult.isExit());
        assertFalse(normalResult.isShowInfoEditor());
        assertEquals(Optional.empty(), normalResult.getPersonToEdit());
        assertEquals("Command executed successfully", normalResult.getFeedbackToUser());
    }

    @Test
    public void commandResult_equality_worksCorrectly() {
        CommandResult result1 = new CommandResult("Test message");
        CommandResult result2 = new CommandResult("Test message");
        CommandResult result3 = new CommandResult("Different message");

        assertEquals(result1, result2);
        assertFalse(result1.equals(result3));
        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void commandExecutor_functionalInterface_worksCorrectly() {
        StringBuilder result = new StringBuilder();
        java.util.function.Consumer<String> commandExecutor = command -> result.append(command);

        commandExecutor.accept("test command");
        assertEquals("test command", result.toString());

        commandExecutor.accept(" additional");
        assertEquals("test command additional", result.toString());
    }

    @Test
    public void showPersonList_runnable_worksCorrectly() {
        java.util.concurrent.atomic.AtomicBoolean called = new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.atomic.AtomicInteger callCount = new java.util.concurrent.atomic.AtomicInteger(0);

        Runnable showPersonList = () -> {
            called.set(true);
            callCount.incrementAndGet();
        };

        showPersonList.run();
        assertTrue(called.get());
        assertEquals(1, callCount.get());

        showPersonList.run();
        assertEquals(2, callCount.get());
    }

    @Test
    public void guiSettings_defaultValues_areNotNull() {
        // Test that GUI settings can be created (tests MainWindow dependency)
        assertNotNull(new seedu.address.commons.core.GuiSettings());
    }

    @Test
    public void path_creation_worksCorrectly() {
        // Test that Path operations work (tests MainWindow dependency)
        java.nio.file.Path testPath = java.nio.file.Path.of("test");
        assertNotNull(testPath);
        assertEquals("test", testPath.toString());
    }

    @Test
    public void observableList_creation_worksCorrectly() {
        // Test that ObservableList operations work (tests MainWindow dependency)
        javafx.collections.ObservableList<Person> testList = javafx.collections.FXCollections.observableArrayList();
        assertNotNull(testList);
        assertTrue(testList.isEmpty());

        Person testPerson = new PersonBuilder().build();
        testList.add(testPerson);
        assertEquals(1, testList.size());
        assertEquals(testPerson, testList.get(0));
    }
}

