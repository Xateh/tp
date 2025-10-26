package seedu.address.logic;

import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.exceptions.AssemblyException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.history.CommandHistory;
import seedu.address.model.person.Person;
import seedu.address.session.SessionData;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException  If an error occurs during command execution.
     * @throws AssemblyException If an error occurs during command assembly.
     */
    CommandResult execute(String commandText) throws CommandException, AssemblyException;

    /**
     * Returns the AddressBook.
     *
     * @see seedu.address.model.Model#getAddressBook()
     */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns an unmodifiable view of the filtered list of persons
     */
    ObservableList<Person> getFilteredPersonList();

    // Returns the history of executed commands, ordered from oldest to newest.

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns a snapshot of the current session data if the address book has changed since the last save.
     */
    Optional<SessionData> getSessionSnapshotIfDirty();

    /**
     * Returns a snapshot of the current session data if any part of the session (address book or session metadata such
     * as search keywords or GUI settings) has changed since the last save. This method is intended to be used by
     * lifecycle/shutdown code to decide whether to persist a session snapshot.
     */
    Optional<SessionData> getSessionSnapshotIfAnyDirty();

    /**
     * Marks the current session snapshot as successfully persisted.
     */
    void markSessionSnapshotPersisted();

    /**
     * Returns the current command history snapshot for persistence.
     *
     * <p>Callers who only require the raw list of entries may use
     * {@code getCommandHistorySnapshot().getEntries()}.
     */
    CommandHistory getCommandHistorySnapshot();
}
