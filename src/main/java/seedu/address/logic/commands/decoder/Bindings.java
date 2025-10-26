package seedu.address.logic.commands.decoder;

import java.util.Arrays;
import java.util.function.Predicate;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.exceptions.ResolutionException;
import seedu.address.logic.commands.extractors.ClearCommandExtractor;
import seedu.address.logic.commands.extractors.CommandExtractor;
import seedu.address.logic.commands.extractors.DeleteCommandExtractor;
import seedu.address.logic.commands.extractors.EditCommandExtractor;
import seedu.address.logic.commands.extractors.ExitCommandExtractor;
import seedu.address.logic.commands.extractors.FieldCommandExtractor;
import seedu.address.logic.commands.extractors.FindCommandExtractor;
import seedu.address.logic.commands.extractors.HelpCommandExtractor;
import seedu.address.logic.commands.extractors.HistoryCommandExtractor;
import seedu.address.logic.commands.extractors.ListCommandExtractor;
import seedu.address.logic.commands.extractors.TagCommandExtractor;

/**
 * Enumeration containing bindings for all imperatives and their respective command extractors.
 */
public enum Bindings {
    CLEAR("clear", ClearCommandExtractor::extract),
    DELETE("delete", DeleteCommandExtractor::extract),
    EDIT("edit", EditCommandExtractor::extract),
    EXIT("exit", ExitCommandExtractor::extract),
    FIELD("field", FieldCommandExtractor::extract),
    FIND("find", FindCommandExtractor::extract),
    HELP("help", HelpCommandExtractor::extract),
    HISTORY("history", HistoryCommandExtractor::extract),
    LIST("list", ListCommandExtractor::extract),
    TAG("tag", TagCommandExtractor::extract);

    private static final String MESSAGE_NO_MATCHING_BINDING = "Unable to find a valid matching command.";
    private static final String MESSAGE_AMBIGUOUS_BINDING = "Resolved command is ambiguous.";

    private final String imperative;
    private final CommandExtractor<?> extractor;

    private <T extends Command> Bindings(String imperative, CommandExtractor<T> extractor) {
        this.imperative = imperative;
        this.extractor = extractor;
    }

    /**
     * Returns {@code CommandExtractor}s corresponding to commands whose imperatives match a provided predicate.
     *
     * @param predicate Predicate used to test for matching imperatives.
     * @return {@code CommandExtractor}s corresponding to the imperatives that match the predicate.
     */
    public static CommandExtractor<?>[] resolveBindings(Predicate<String> predicate) {
        return Arrays.stream(Bindings.values())
                .filter(binding -> predicate.test(binding.imperative))
                .map(binding -> binding.extractor).toArray(CommandExtractor<?>[]::new);
    }

    /**
     * Returns a single {@code CommandExtractor} corresponding to the command whose imperative matches a provided
     * predicate.
     *
     * @param predicate Predicate used to test for matching imperative.
     * @return {@code CommandExtractor} corresponding to the imperatives that match the predicate.
     * @throws ResolutionException no commands are resolved to or there are multiple resolved commands.
     */
    public static CommandExtractor<?> resolveExactBinding(Predicate<String> predicate) throws ResolutionException {
        CommandExtractor<?>[] extractors = Arrays.stream(Bindings.values())
                .filter(binding -> predicate.test(binding.imperative))
                .map(binding -> binding.extractor).toArray(CommandExtractor<?>[]::new);

        if (extractors.length == 0) {
            throw new ResolutionException(MESSAGE_NO_MATCHING_BINDING);
        }

        if (extractors.length > 1) {
            throw new ResolutionException(MESSAGE_AMBIGUOUS_BINDING);
        }

        return extractors[0];
    }
}
