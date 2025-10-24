package seedu.address.session;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

/**
 * Resolves the directory used to persist session data based on the address book file location.
 */
public final class SessionDirectoryResolver {

    private SessionDirectoryResolver() {
        // Utility class
    }

    /**
     * Derives the session directory path from the given {@code addressBookPath}.
     * If the address book resides in the working directory, the sessions folder
     * will be created directly under it.
     *
     * @param addressBookPath path to the address book file
     * @return resolved session directory path
     */
    public static Path resolve(Path addressBookPath) {
        requireNonNull(addressBookPath);
        Path parent = addressBookPath.getParent();
        if (parent == null) {
            return Path.of("sessions");
        }
        return parent.resolve("sessions");
    }
}
