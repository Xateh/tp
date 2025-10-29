package seedu.address.ui;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.exceptions.AssemblyException;
import seedu.address.model.history.CommandHistory;

/**
 * The UI component that is responsible for receiving user command inputs.
 *
 * <p>Note: this class delegates direct interactions with the text field to
 * {@link seedu.address.ui.TextFieldAdapter}. The adapter is provided by the FXML-initialized `TextField` in production,
 * and tests can inject a lightweight, pure-Java implementation of {@code TextFieldAdapter} to exercise command-box
 * behaviour without initializing the JavaFX toolkit.</p>
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final HistorySupplier historySupplier;
    private boolean navigatingHistory;

    private final HistoryNavigator historyNavigator;

    @FXML
    private TextField commandTextField;

    private TextFieldAdapter textFieldAdapter;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor, HistorySupplier historySupplier) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.historySupplier = Objects.requireNonNull(historySupplier);
        this.historyNavigator = new HistoryNavigator();
        // create adapter wrapping the real TextField injected by FXML
        this.textFieldAdapter = new TextFieldAdapter() {
            @Override
            public void addTextChangeListener(Runnable listener) {
                commandTextField.textProperty().addListener((unused1, unused2, unused3) -> listener.run());
            }

            @Override
            public java.util.List<String> getStyleClass() {
                return commandTextField.getStyleClass();
            }

            @Override
            public void setText(String text) {
                commandTextField.setText(text);
            }

            @Override
            public void positionCaret(int pos) {
                commandTextField.positionCaret(pos);
            }

            @Override
            public String getText() {
                return commandTextField.getText();
            }
        };

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        textFieldAdapter.addTextChangeListener(() -> {
            setStyleToDefault();
            if (!navigatingHistory) {
                historyNavigator.reset(historySupplier.getHistory().getEntries());
            }
        });
        // hook up key event handling on the real TextField directly
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryNavigation);
        historyNavigator.reset(historySupplier.getHistory().getEntries());
    }

    /**
     * Test-friendly constructor that avoids FXML loading. Intended for use in unit tests.
     */
    /* package-private */ CommandBox(CommandExecutor commandExecutor, HistorySupplier historySupplier,
                                     TextFieldAdapter adapter) {
        super(); // use no-op UiPart constructor
        this.commandExecutor = commandExecutor;
        this.historySupplier = Objects.requireNonNull(historySupplier);
        this.historyNavigator = new HistoryNavigator();
        this.textFieldAdapter = Objects.requireNonNull(adapter);

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        textFieldAdapter.addTextChangeListener(() -> {
            setStyleToDefault();
            if (!navigatingHistory) {
                historyNavigator.reset(historySupplier.getHistory().getEntries());
            }
        });
        // No TextField available in this test-friendly ctor, so no event filter to install.
        historyNavigator.reset(historySupplier.getHistory().getEntries());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = textFieldAdapter.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            setCommandText("");
            historyNavigator.reset(historySupplier.getHistory().getEntries());
        } catch (CommandException | AssemblyException e) {
            setStyleToIndicateCommandFailure();
            historyNavigator.reset(historySupplier.getHistory().getEntries());
        }
    }

    private void handleHistoryNavigation(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            java.util.Optional<String> prev = historyNavigator.previous();
            if (prev.isPresent()) {
                setCommandText(prev.get());
            }
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            java.util.Optional<String> next = historyNavigator.next();
            if (next.isPresent()) {
                setCommandText(next.get());
            } else {
                setCommandText("");
            }
            event.consume();
        }
    }

    /**
     * Sets the command text field to the given text.
     */
    private void setCommandText(String text) {
        navigatingHistory = true;
        textFieldAdapter.setText(text);
        textFieldAdapter.positionCaret(textFieldAdapter.getText().length());
        navigatingHistory = false;
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        textFieldAdapter.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        java.util.List<String> styleClass = textFieldAdapter.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, AssemblyException;
    }

    /**
     * Supplies snapshots of the command history for navigation.
     */
    @FunctionalInterface
    public interface HistorySupplier {
        CommandHistory getHistory();
    }

}
