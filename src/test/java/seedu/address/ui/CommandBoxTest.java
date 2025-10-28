package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.model.history.CommandHistory;


public class CommandBoxTest {

    @Test
    public void setCommandText_historyInteraction() throws Exception {
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory(Arrays.asList("one", "two"));
        CommandBox.CommandExecutor dummyExecutor = (s) -> null;

        // create a pure-java adapter for testing (no JavaFX classes)
        TextFieldAdapter adapter = new TextFieldAdapter() {
            private String text = "";
            private final java.util.List<String> style = new java.util.ArrayList<>();

            @Override
            public void addTextChangeListener(Runnable listener) {
                // no-op for this test
            }

            // no addEventFilter in adapter for tests

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
            }

            @Override
            public void positionCaret(int pos) {
                // no-op
            }

            @Override
            public String getText() {
                return text;
            }
        };

        // construct using adapter-based ctor to avoid FXML/JavaFX
        CommandBox box = new CommandBox(dummyExecutor, historySupplier, adapter);
        org.junit.jupiter.api.Assertions.assertNotNull(box);
        org.junit.jupiter.api.Assertions.assertNotNull(box);
        // invoke private setCommandText and verify adapter text updated
        Method setCommandText = CommandBox.class.getDeclaredMethod("setCommandText", String.class);
        setCommandText.setAccessible(true);
        setCommandText.invoke(box, "two");
        assertEquals("two", adapter.getText());
    }

    @Test
    public void textChange_listener_clearsErrorStyle() throws Exception {
        CommandBox.CommandExecutor dummyExecutor = (s) -> null;
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory();

        // adapter that stores the listener and invokes it when setText is called
        final class ListeningAdapter implements TextFieldAdapter {
            private String text = "";
            private final java.util.List<String> style = new java.util.ArrayList<>();
            private Runnable listener;

            @Override
            public void addTextChangeListener(Runnable listener) {
                this.listener = listener;
            }

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
                if (listener != null) {
                    listener.run();
                }
            }

            @Override
            public void positionCaret(int pos) {
            }

            @Override
            public String getText() {
                return text;
            }
        }

        ListeningAdapter adapter = new ListeningAdapter();
        // pre-populate with error style
        adapter.getStyleClass().add(CommandBox.ERROR_STYLE_CLASS);

        CommandBox box = new CommandBox(dummyExecutor, historySupplier, adapter);
        org.junit.jupiter.api.Assertions.assertNotNull(box);

        // change text which should invoke listener and clear the error style
        adapter.setText("new text");

        org.junit.jupiter.api.Assertions.assertFalse(adapter.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
    }

    @Test
    public void setCommandText_positionsCaretAtEnd() throws Exception {
        CommandBox.CommandExecutor dummyExecutor = (s) -> null;
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory();

        final class CaretAdapter implements TextFieldAdapter {
            private final java.util.List<String> style = new java.util.ArrayList<>();

            private String text = "";
            private int lastCaret = -1;

            @Override
            public void addTextChangeListener(Runnable listener) {
            }

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
            }

            @Override
            public void positionCaret(int pos) {
                this.lastCaret = pos;
            }

            @Override
            public String getText() {
                return text;
            }
        }

        CaretAdapter adapter = new CaretAdapter();
        CommandBox box = new CommandBox(dummyExecutor, historySupplier, adapter);

        Method setCommandText = CommandBox.class.getDeclaredMethod("setCommandText", String.class);
        setCommandText.setAccessible(true);
        setCommandText.invoke(box, "hello");

        assertEquals("hello", adapter.getText());
        assertEquals(adapter.getText().length(), adapter.lastCaret);
    }

    @Test
    public void setCommandText_doesNotResetNavigatorWhenNavigating() throws Exception {
        CommandBox.CommandExecutor dummyExecutor = (s) -> null;
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory(Arrays.asList("one", "two", "three"));

        // adapter that stores listener and invokes on setText
        final class ListeningAdapter implements TextFieldAdapter {
            private String text = "";
            private final java.util.List<String> style = new java.util.ArrayList<>();
            private Runnable listener;

            @Override
            public void addTextChangeListener(Runnable listener) {
                this.listener = listener;
            }

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
                if (listener != null) {
                    listener.run();
                }
            }

            @Override
            public void positionCaret(int pos) {
            }

            @Override
            public String getText() {
                return text;
            }
        }

        ListeningAdapter adapter = new ListeningAdapter();
        CommandBox box = new CommandBox(dummyExecutor, historySupplier, adapter);

        // access private historyNavigator
        Field navField = CommandBox.class.getDeclaredField("historyNavigator");
        navField.setAccessible(true);
        HistoryNavigator nav = (HistoryNavigator) navField.get(box);

        // move pointer by calling previous()
        nav.previous();
        int before = nav.getPointer();

        // call setCommandText which sets navigatingHistory = true and triggers listener
        Method setCommandText = CommandBox.class.getDeclaredMethod("setCommandText", String.class);
        setCommandText.setAccessible(true);
        setCommandText.invoke(box, "two");

        int after = nav.getPointer();
        // pointer should remain unchanged because navigatingHistory prevents reset
        assertEquals(before, after);
    }

    @Test
    public void handleCommandEntered_success_resetsTextAndQueriesHistory() throws Exception {
        CommandBox.CommandExecutor executor = (String cmd) -> null;
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory(Arrays.asList("a"));

        // adapter backing for test
        final TextFieldAdapter adapter = new TextFieldAdapter() {
            private String text = "";
            private final java.util.List<String> style = new java.util.ArrayList<>();

            @Override
            public void addTextChangeListener(Runnable listener) {
            }

            // no addEventFilter in adapter for tests

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
            }

            @Override
            public void positionCaret(int pos) {
            }

            @Override
            public String getText() {
                return text;
            }
        };

        CommandBox box = new CommandBox(executor, historySupplier, adapter);

        // set the command text via adapter
        adapter.setText("some command");

        // call private handleCommandEntered
        Method m = CommandBox.class.getDeclaredMethod("handleCommandEntered");
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(box));

        // verify text cleared
        assertEquals("", adapter.getText());
    }

    @Test
    public void handleCommandEntered_failure_addsErrorStyle() throws Exception {
        CommandBox.CommandExecutor executor = (String cmd) -> {
            throw new ValidationException("err");
        };
        CommandBox.HistorySupplier historySupplier = () -> new CommandHistory();

        final TextFieldAdapter adapter = new TextFieldAdapter() {
            private String text = "";
            private final java.util.List<String> style = new java.util.ArrayList<>();

            @Override
            public void addTextChangeListener(Runnable listener) {
            }

            // no addEventFilter in adapter for tests

            @Override
            public java.util.List<String> getStyleClass() {
                return style;
            }

            @Override
            public void setText(String text) {
                this.text = text;
            }

            @Override
            public void positionCaret(int pos) {
            }

            @Override
            public String getText() {
                return text;
            }
        };

        CommandBox box = new CommandBox(executor, historySupplier, adapter);

        adapter.setText("bad command");

        Method m = CommandBox.class.getDeclaredMethod("handleCommandEntered");
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(box));

        // after failure, adapter style list should contain the error style
        boolean contains = adapter.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS);
        assert (contains);
    }

}
