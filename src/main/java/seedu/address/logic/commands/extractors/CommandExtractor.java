package seedu.address.logic.commands.extractors;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

/**
 * Functional interface for command extractors, that accept a {@code BareCommand} and extracts information out of that
 * {@code BareCommand} to construct the final {@code Command} to be run.
 *
 * @param <T>
 */
@FunctionalInterface
public interface CommandExtractor<T extends Command> {
    T extract(BareCommand bareCommand) throws ValidationException;
}
