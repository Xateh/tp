package seedu.address.logic.session;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.session.SessionCommand;
import seedu.address.session.SessionData;

/**
 * Collects transient session information during program execution so it can be persisted on shutdown.
 */
public class SessionRecorder {

    private final List<SessionCommand> commandHistory = new ArrayList<>();
    private List<String> searchKeywords = Collections.emptyList();

    /**
     * Creates a {@code SessionRecorder} with no existing session data.
     */
    public SessionRecorder() {
        this(Optional.empty());
    }

    /**
     * Creates a {@code SessionRecorder} seeded with a previously saved session.
     *
     * @param initialSession snapshot to hydrate from before recording new commands
     */
    public SessionRecorder(Optional<SessionData> initialSession) {
        initialSession.ifPresent(session -> {
            commandHistory.addAll(session.getCommandHistory());
            searchKeywords = List.copyOf(session.getSearchKeywords());
        });
    }

    /** Records a successfully executed command and updates session metadata if required. */
    public void afterSuccessfulCommand(String commandText, Command command) {
        commandHistory.add(new SessionCommand(Instant.now(), commandText));

        if (command instanceof FindCommand) {
            FindCommand findCommand = (FindCommand) command;
            searchKeywords = List.copyOf(findCommand.getPredicate().getKeywords());
            return;
        }

        if (command instanceof ListCommand) {
            searchKeywords = Collections.emptyList();
        }
    }

    /** Creates an immutable snapshot of the current session state. */
    public SessionData buildSnapshot(Path addressBookPath, GuiSettings guiSettings) {
        return new SessionData(Instant.now(), addressBookPath,
                searchKeywords, commandHistory, guiSettings);
    }
}
