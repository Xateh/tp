package seedu.address.logic.session;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.session.SessionData;

/**
 * Collects transient session information during program execution so it can be persisted on shutdown.
 */
public class SessionRecorder {

    private List<String> searchKeywords = Collections.emptyList();
    private boolean addressBookDirty;

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
            searchKeywords = List.copyOf(session.getSearchKeywords());
        });
        addressBookDirty = false;
    }

    /** Records a successfully executed command and updates session metadata if required. */
    public void afterSuccessfulCommand(Command command, boolean addressBookMutated) {
        if (addressBookMutated) {
            addressBookDirty = true;
        }

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
    public SessionData buildSnapshot(ReadOnlyAddressBook addressBook, GuiSettings guiSettings) {
        return new SessionData(Instant.now(), addressBook,
                searchKeywords, guiSettings);
    }

    /** Returns {@code true} if the address book changed since the last persisted snapshot. */
    public boolean isAddressBookDirty() {
        return addressBookDirty;
    }

    /** Marks the current session snapshot as persisted. */
    public void markSnapshotPersisted() {
        addressBookDirty = false;
    }
}
