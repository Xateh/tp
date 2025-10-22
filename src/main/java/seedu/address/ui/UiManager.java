package seedu.address.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.InfoSaveCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Info;
import seedu.address.model.person.Person;

/**
 * The manager of the UI component.
 */
public class UiManager implements Ui {

    public static final String ALERT_DIALOG_PANE_FIELD_ID = "alertDialogPane";

    private static final Logger logger = LogsCenter.getLogger(UiManager.class);
    private static final String ICON_APPLICATION = "/images/address_book_32.png";

    private Logic logic;
    private MainWindow mainWindow;

    /**
     * Creates a {@code UiManager} with the given {@code Logic}.
     */
    public UiManager(Logic logic) {
        this.logic = logic;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting UI...");

        //Set the application icon.
        primaryStage.getIcons().add(getImage(ICON_APPLICATION));

        try {
            mainWindow = new MainWindow(primaryStage, logic);
            mainWindow.show(); //This should be called before creating other UI parts
            mainWindow.fillInnerParts();

        } catch (Throwable e) {
            logger.severe(StringUtil.getDetails(e));
            showFatalErrorDialogAndShutdown("Fatal error during initializing", e);
        }
    }

    /**
     * Shows an info editor dialog for the specified person.
     * This method is called directly by InfoEditCommand.
     *
     * @param person The person whose info is to be edited
     * @param personIndex The index of the person in the filtered list
     */
    public void showInfoEditor(Person person, int personIndex) {
        Platform.runLater(() -> {
            try {
                showInfoEditorDialog(person, personIndex);
            } catch (Exception e) {
                logger.severe("Error showing info editor: " + e.getMessage());
                showAlertDialogAndWait(Alert.AlertType.ERROR, "Error",
                        "Failed to open info editor", e.getMessage());
            }
        });
    }

    /**
     * Displays the info editor dialog and handles saving.
     */
    private void showInfoEditorDialog(Person person, int personIndex) {
        // Create a custom dialog with a proper text area
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Information");
        dialog.setHeaderText("Editing information for: " + person.getName().fullName);

        // Create the text area
        TextArea textArea = new TextArea(person.getInfo().value);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(10);
        textArea.setPrefColumnCount(50);
        textArea.setMinHeight(200);
        textArea.setMinWidth(400);

        // Create a container for the text area
        VBox container = new VBox(10);
        container.getChildren().add(textArea);

        // Set the content
        dialog.getDialogPane().setContent(container);

        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style the dialog
        dialog.getDialogPane().getStylesheets().add("view/DarkTheme.css");
        dialog.initOwner(mainWindow.getPrimaryStage());

        // Set the result converter to get text from the text area
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return textArea.getText();
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newInfoText -> {
            savePersonInfo(personIndex, newInfoText);
        });
    }

    /**
     * Saves the edited information for a person.
     */
    private void savePersonInfo(int personIndex, String infoText) {
        try {
            Index index = Index.fromOneBased(personIndex + 1);
            Info info = new Info(infoText);
            InfoSaveCommand saveCommand = new InfoSaveCommand(index, info);

            // Execute the save command directly
            var result = saveCommand.execute(logic.getModel());

            // Update the main window's result display
            mainWindow.showFeedback(result.getFeedbackToUser());

            logger.info("Successfully saved info for person at index " + (personIndex + 1));

        } catch (CommandException e) {
            logger.severe("Failed to save info: " + e.getMessage());
            showAlertDialogAndWait(Alert.AlertType.ERROR, "Save Failed",
                    "Could not save information", e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error while saving: " + e.getMessage());
            showAlertDialogAndWait(Alert.AlertType.ERROR, "Error",
                    "An unexpected error occurred", e.getMessage());
        }
    }

    private Image getImage(String imagePath) {
        return new Image(MainApp.class.getResourceAsStream(imagePath));
    }

    void showAlertDialogAndWait(Alert.AlertType type, String title, String headerText, String contentText) {
        showAlertDialogAndWait(mainWindow.getPrimaryStage(), type, title, headerText, contentText);
    }

    /**
     * Shows an alert dialog on {@code owner} with the given parameters.
     * This method only returns after the user has closed the alert dialog.
     */
    private static void showAlertDialogAndWait(Stage owner, AlertType type, String title, String headerText,
                                               String contentText) {
        final Alert alert = new Alert(type);
        alert.getDialogPane().getStylesheets().add("view/DarkTheme.css");
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setId(ALERT_DIALOG_PANE_FIELD_ID);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog with {@code title} and error message, {@code e},
     * and exits the application after the user has closed the alert dialog.
     */
    private void showFatalErrorDialogAndShutdown(String title, Throwable e) {
        logger.severe(title + " " + e.getMessage() + StringUtil.getDetails(e));
        showAlertDialogAndWait(Alert.AlertType.ERROR, title, e.getMessage(), e.toString());
        Platform.exit();
        System.exit(1);
    }
}
