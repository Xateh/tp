package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.FieldContainsKeywordsPredicate;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose fields contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    public static final String MESSAGE_DUPLICATE_KEYWORDS = "Duplicate keywords ignored: %1$s";

    private final FieldContainsKeywordsPredicate predicate;

    public FindCommand(FieldContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    public FieldContainsKeywordsPredicate getPredicate() {
        return predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        // analyse raw keywords for potential issues: blanks, multi-word tokens, duplicates
        List<String> rawKeywords = predicate.getKeywords();
        Set<String> seen = new LinkedHashSet<>();
        Set<String> duplicates = new LinkedHashSet<>();
        Set<String> blanks = new LinkedHashSet<>();
        Set<String> multiWords = new LinkedHashSet<>();

        for (String kw : rawKeywords) {
            String original = kw == null ? "" : kw;
            String trimmed = original.trim();
            String lower = trimmed.toLowerCase();

            if (trimmed.isEmpty()) {
                blanks.add(original);
                continue;
            }
            if (trimmed.split("\\s+").length > 1) {
                multiWords.add(trimmed);
                continue;
            }
            if (!seen.add(lower)) {
                duplicates.add(lower);
            }
        }

        model.updateFilteredPersonList(predicate);

        String feedback = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                model.getFilteredPersonList().size());

        List<Warning> warnings = new java.util.ArrayList<>();
        if (!duplicates.isEmpty()) {
            String dupList = duplicates.stream().sorted().collect(Collectors.joining(", "));
            warnings.add(Warning.duplicateInputIgnored(String.format(MESSAGE_DUPLICATE_KEYWORDS, dupList)));
        }
        if (!blanks.isEmpty()) {
            warnings.add(Warning.ignoredBlankKeywords("Blank keywords were ignored"));
        }
        if (!multiWords.isEmpty()) {
            String list = multiWords.stream().sorted().collect(Collectors.joining(", "));
            warnings.add(Warning.ignoredMultiwordKeywords(String.format("Multi-word keywords ignored: %s", list)));
        }

        if (warnings.isEmpty()) {
            return new CommandResult(feedback);
        }
        return new CommandResult(feedback, warnings);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
