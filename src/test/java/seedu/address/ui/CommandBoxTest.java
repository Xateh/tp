package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
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

        // invoke private setCommandText and verify adapter text updated
        Method setCommandText = CommandBox.class.getDeclaredMethod("setCommandText", String.class);
        setCommandText.setAccessible(true);
        setCommandText.invoke(box, "two");
        assertEquals("two", adapter.getText());
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
            throw new ParseException("err");
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
        assert(contains);
    }

}
