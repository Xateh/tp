package seedu.address.ui;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A ui for the status bar that is displayed at the footer of the application.
 */
public class StatusBarFooter extends UiPart<Region> {

    private static final String FXML = "StatusBarFooter.fxml";

    @FXML
    private Label saveLocationStatus;

    /**
     * Creates a {@code StatusBarFooter} showing the directory where session snapshots are stored.
     *
     * <p>The {@code addressBookFilePath} parameter is used to derive the session directory in the
     * same way as {@link seedu.address.MainAppLifecycleManager#deriveSessionDirectory(Path)}:
     * if the address book file has a parent directory, the session directory is
     * parent.resolve("sessions"); otherwise it falls back to a top-level "sessions" directory.
     */
    public StatusBarFooter(Path saveLocation) {
        super(FXML);
        // Derive the sessions directory from the provided address book file path.
        Path sessionDir;
        Path parent = saveLocation == null ? null : saveLocation.getParent();
        if (parent == null) {
            sessionDir = Path.of("sessions");
        } else {
            sessionDir = parent.resolve("sessions");
        }
        saveLocationStatus.setText(Paths.get(".").resolve(sessionDir).toString());
    }

}
