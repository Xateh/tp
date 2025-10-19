package seedu.address.ui;

import java.nio.charset.StandardCharsets;
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
        String infoText = infoTextArea.getText();

        // Use custom hex encoding that only produces alphanumeric characters
        String encodedInfo = encodeToSafeHex(infoText);
        String commandText = "infosave " + oneBasedIndex + " " + encodedInfo;

        logger.info("Executing save command with hex encoded info");
        commandExecutor.accept(commandText);
        showPersonList.run();
    }

    /**
     * Encodes a string to hex using only alphanumeric characters (0-9, A-F).
     */
    private String encodeToSafeHex(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b & 0xFF));
        }
        return hexString.toString();
    }
}
