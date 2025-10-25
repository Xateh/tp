package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;
    private final List<Warning> warnings;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, Collections.emptyList());
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with warnings, formatting the feedback accordingly.
     */
    public CommandResult(String feedbackToUser, List<Warning> warnings) {
        this(formatFeedbackWithWarnings(feedbackToUser, warnings), false, false, warnings);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, List<Warning> warnings) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.warnings = List.copyOf(requireNonNull(warnings));
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && warnings.equals(otherCommandResult.warnings)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, warnings, showHelp, exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("warnings", warnings)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

    private static String formatFeedbackWithWarnings(String feedbackToUser, List<Warning> warnings) {
        requireNonNull(feedbackToUser);
        requireNonNull(warnings);
        if (warnings.isEmpty()) {
            return feedbackToUser;
        }

        StringBuilder sb = new StringBuilder(feedbackToUser);
        sb.append(System.lineSeparator()).append(System.lineSeparator()).append("Warnings:");
        for (int i = 0; i < warnings.size(); i++) {
            sb.append(System.lineSeparator())
                    .append(i + 1)
                    .append(". ")
                    .append(warnings.get(i).formatForDisplay());
        }
        return sb.toString();
    }

}
