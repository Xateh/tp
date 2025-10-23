package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seedu.address.logic.commands.CommandResult;

class CommandBoxTest {

    @BeforeAll
    static void initFxToolkit() {
        // Try to start JavaFX normally first. If that fails (e.g., no display),
        // fall back to Monocle headless platform if available.
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException ignored) {
            // Toolkit already initialised.
        } catch (Throwable firstAttemptError) {
            // First attempt failed; attempt headless Monocle startup.
            System.setProperty("glass.platform", "Monocle");
            System.setProperty("monocle.platform", "Headless");
            System.setProperty("javafx.platform", "Monocle");
            System.setProperty("prism.order", "sw");
            try {
                Platform.startup(() -> { });
            } catch (IllegalStateException ignored) {
                // Toolkit already initialised.
            } catch (Throwable t) {
                // Give up; tests requiring JavaFX will likely fail later with clearer errors.
            }
        }
    }

    @Test
    void pressingUpWithEmptyHistory_keepsFieldBlank() {
        List<String> history = new ArrayList<>();
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        assertEquals("", getText(field));
    }

    @Test
    void pressingUpCyclesToLatestCommand() {
        List<String> history = new ArrayList<>(List.of("first", "second", "third"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        assertEquals("third", getText(field));
    }

    @Test
    void pressingUpPastOldest_stopsAtOldestEntry() {
        List<String> history = new ArrayList<>(List.of("first", "second", "third"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        pressKey(field, KeyCode.UP);
        pressKey(field, KeyCode.UP);
        pressKey(field, KeyCode.UP);
        assertEquals("first", getText(field));
    }

    @Test
    void pressingDownAfterNavigating_movesForwardAndClearsAtEnd() {
        List<String> history = new ArrayList<>(List.of("first", "second", "third"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        pressKey(field, KeyCode.UP);
        assertEquals("second", getText(field));

        pressKey(field, KeyCode.DOWN);
        assertEquals("third", getText(field));

        pressKey(field, KeyCode.DOWN);
        assertEquals("", getText(field));

        pressKey(field, KeyCode.DOWN);
        assertEquals("", getText(field));
    }

    @Test
    void typingNewCommand_resetsHistoryPointer() {
        List<String> history = new ArrayList<>(List.of("first", "second", "third"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        assertEquals("third", getText(field));

        setText(field, "custom command");
        assertEquals("custom command", getText(field));

        pressKey(field, KeyCode.UP);
        assertEquals("third", getText(field));
    }

    @Test
    void historyShrinkWhileNavigating_adjustsPointerSafely() {
        List<String> history = new ArrayList<>(List.of("first", "second", "third"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.UP);
        assertEquals("third", getText(field));

        runOnFxThread(() -> {
            history.clear();
            history.add("only");
        });

        pressKey(field, KeyCode.UP);
        assertEquals("only", getText(field));
    }

    @Test
    void pressingDownWithEmptyHistory_keepsFieldBlank() {
        List<String> history = new ArrayList<>();
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        pressKey(field, KeyCode.DOWN);
        assertEquals("", getText(field));
    }

    @Test
    void historyShrinkBelowPointer_onNavigateDown_clearsSafely() {
        List<String> history = new ArrayList<>(List.of("one", "two"));
        CommandBox commandBox = createCommandBox(history);
        TextField field = extractCommandTextField(commandBox);
        focusField(field);

        // move up to latest
        pressKey(field, KeyCode.UP);
        assertEquals("two", getText(field));

        // shrink history so pointer would be out-of-bounds
        runOnFxThread(() -> {
            history.clear();
            history.add("single");
            history.add("second");
        });

        // pressing DOWN should move forward or clear safely without throwing
        pressKey(field, KeyCode.DOWN);
        // either clears to empty (if at end) or sets to an existing entry; ensure no exception
        String got = getText(field);
        // allowed values: empty or any of new history entries
        boolean valid = got.isEmpty() || got.equals("single") || got.equals("second");
        assertEquals(true, valid);
    }

    private CommandBox createCommandBox(List<String> history) {
        return callOnFxThread(() -> new CommandBox(commandText -> new CommandResult("ok"),
                () -> new ArrayList<>(history)));
    }

    private TextField extractCommandTextField(CommandBox commandBox) {
        return callOnFxThread(() -> {
            try {
                Field field = CommandBox.class.getDeclaredField("commandTextField");
                field.setAccessible(true);
                return (TextField) field.get(commandBox);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        });
    }

    private void pressKey(TextField field, KeyCode keyCode) {
        runOnFxThread(() -> field.fireEvent(
                new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode, false, false, false, false)));
    }

    private void focusField(TextField field) {
        runOnFxThread(field::requestFocus);
    }

    private void setText(TextField field, String value) {
        runOnFxThread(() -> field.setText(value));
    }

    private String getText(TextField field) {
        return callOnFxThread(field::getText);
    }

    private static void runOnFxThread(Runnable runnable) {
        callOnFxThread(() -> {
            runnable.run();
            return null;
        });
    }

    private static <T> T callOnFxThread(Callable<T> callable) {
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }

        FutureTask<T> task = new FutureTask<>(callable);
        Platform.runLater(task);
        try {
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        } catch (ExecutionException e) {
            throw new AssertionError(e.getCause());
        }
    }
}
