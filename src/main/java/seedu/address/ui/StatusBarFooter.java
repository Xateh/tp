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
        saveLocationStatus.setText(getDisplayedSessionDir(saveLocation));
    }

    /**
     * Computes the string to display in the status bar for the session directory derived
     * from the supplied address book file path.
     *
     * <p>Mirrors the logic in {@code MainAppLifecycleManager.deriveSessionDirectory(Path)} and
     * returns a path resolved against the current working directory so it resembles how the
     * status bar displays paths (e.g. "data/sessions" or "sessions").
     */
    static String getDisplayedSessionDir(Path addressBookFilePath) {
        Path parent = addressBookFilePath == null ? null : addressBookFilePath.getParent();
        Path sessionDir = (parent == null) ? Path.of("sessions") : parent.resolve("sessions");
        String pathString = Paths.get(".").resolve(sessionDir).toString();
        return pathString != null ? pathString : "";
    }
}
