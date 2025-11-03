package seedu.address.logic;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.decoder.Decoder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.exceptions.AssemblyException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.session.SessionRecorder;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.history.CommandHistory;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.session.SessionData;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final SessionRecorder sessionRecorder;
    private final Storage storage;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, Optional.empty());
    }

    /**
     * Constructs a {@code LogicManager} that optionally restores state from a previous session snapshot.
     *
     * @param model          backing model instance
     * @param storage        storage layer used for persistence
     * @param initialSession previously saved session data to restore, if any
     */
    public LogicManager(Model model, Storage storage, Optional<SessionData> initialSession) {
        this.model = model;
        this.storage = storage;

        CommandHistory initialHistory = loadCommandHistory(storage);
        this.model.setCommandHistory(initialHistory);
        sessionRecorder = new SessionRecorder(model.getAddressBook(), model.getGuiSettings(), initialSession);
        initialSession.ifPresent(this::restoreSessionState);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, AssemblyException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = Decoder.decode(BareCommand.parse(commandText));
        AddressBook beforeState = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute(model);
        AddressBook afterState = new AddressBook(model.getAddressBook());
        boolean addressBookChanged = !beforeState.equals(afterState);

        model.getCommandHistory().add(commandText);

        //ensures links added persists in address book
        if (addressBookChanged) {
            try {
                storage.saveAddressBook(model.getAddressBook());
            } catch (Exception e) {
                throw new CommandException("Could not save data to file: " + e.getMessage(), e);
            }
        }

        sessionRecorder.afterSuccessfulCommand(command, addressBookChanged);

        return commandResult;
    }

    @Override
    public void markAddressBookDirty() {
        Command dummyCommand = new seedu.address.logic.commands.InfoCommand(
                seedu.address.commons.core.index.Index.fromZeroBased(0)
        );
        sessionRecorder.afterSuccessfulCommand(dummyCommand, true);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }


    private CommandHistory loadCommandHistory(Storage storage) {
        try {
            return storage.readCommandHistory().orElseGet(CommandHistory::new);
        } catch (DataLoadingException e) {
            logger.warning("Command history at " + storage.getCommandHistoryFilePath()
                    + " could not be loaded. Starting with an empty history.");
            return new CommandHistory();
        }
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
        // Record that GUI settings changed so session snapshot will include the new GUI state
        sessionRecorder.afterGuiSettingsChanged(guiSettings);
    }

    @Override
    public Optional<SessionData> getSessionSnapshotIfDirty() {
        if (!sessionRecorder.isAddressBookDirty(model.getAddressBook())) {
            return Optional.empty();
        }
        return Optional.of(sessionRecorder.buildSnapshot(model.getAddressBook(), model.getGuiSettings()));
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void markSessionSnapshotPersisted() {
        sessionRecorder.markSnapshotPersisted();
    }

    @Override
    public Optional<SessionData> getSessionSnapshotIfAnyDirty() {
        if (!sessionRecorder.isAnyDirty(model.getAddressBook(), model.getGuiSettings())) {
            return Optional.empty();
        }
        return Optional.of(sessionRecorder.buildSnapshot(model.getAddressBook(), model.getGuiSettings()));
    }

    @Override
    public CommandHistory getCommandHistorySnapshot() {
        return new CommandHistory(model.getCommandHistory().getEntries());
    }

    private void restoreSessionState(SessionData sessionData) {
        if (!sessionData.getSearchKeywords().isEmpty()) {
            model.updateFilteredPersonList(new FieldContainsKeywordsPredicate(sessionData.getSearchKeywords()));
        }
    }
}
