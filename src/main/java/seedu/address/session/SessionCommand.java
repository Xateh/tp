package seedu.address.session;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single command invocation within a session.
 */
public class SessionCommand {

    private final Instant timestamp;
    private final String commandText;

    public SessionCommand(Instant timestamp, String commandText) {
        this.timestamp = requireNonNull(timestamp);
        this.commandText = requireNonNull(commandText);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCommandText() {
        return commandText;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof SessionCommand)) {
            return false;
        }

        SessionCommand otherCommand = (SessionCommand) other;
        return timestamp.equals(otherCommand.timestamp)
                && commandText.equals(otherCommand.commandText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, commandText);
    }

    @Override
    public String toString() {
        return "SessionCommand{" + "timestamp=" + timestamp + ", commandText='"
                + commandText + "'" + '}';
    }
}
