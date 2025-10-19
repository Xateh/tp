package seedu.address.ui;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * A UI component that displays a text editor for a Person's information.
 */
public class InfoEditorPanel extends UiPart<Region> {

    private static final String FXML = "InfoEditorPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(InfoEditorPanel.class);

    private int personIndex;
    private final Consumer<String> commandExecutor;
    private final Runnable showPersonList;

    @FXML
    private Label infoLabel;
    @FXML
    private TextArea infoTextArea;
    @FXML
    private Button saveButton;

    /**
     * Creates an {@code InfoEditorPanel}.
     */
    public InfoEditorPanel(Consumer<String> commandExecutor, Runnable showPersonList) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.showPersonList = showPersonList;
    }

    /**
     * Sets the person whose information is to be edited.
     */
    public void setPerson(Person person, int personIndex) {
        this.personIndex = personIndex;
        infoLabel.setText("Editing information for: " + person.getName().fullName);
        infoTextArea.setText(person.getInfo().value);
        infoTextArea.requestFocus();
        infoTextArea.positionCaret(infoTextArea.getText().length());
    }

    @FXML
    private void handleSave() {
        int oneBasedIndex = personIndex + 1;
        // Escape quotes in the text to avoid breaking the parser
        String infoText = infoTextArea.getText().replace("\"", "\\\"");
        String commandText = "infosave " + oneBasedIndex + " \"" + infoText + "\"";

        logger.info("Executing save command: " + commandText);
        commandExecutor.accept(commandText);
        showPersonList.run();
    }
}
