package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents a non-fatal warning produced during command execution.
 * A warning communicates that the command succeeded, but user input was adjusted.
 */
public final class Warning {

    /**
     * Enumerates the supported warning types.
     */
    public enum Type {
        DUPLICATE_INPUT_IGNORED("Duplicate input ignored"),
        /**
         * Indicates custom field values were overwritten during a command.
         */
        FIELD_OVERWRITTEN("Field value overwritten"),
        /**
         * Indicates one or more provided keywords were blank and were ignored.
         */
        IGNORED_BLANK_KEYWORDS("Ignored blank keywords"),
        /**
         * Indicates one or more provided keywords were multi-word tokens and were ignored.
         */
        IGNORED_MULTIWORD_KEYWORDS("Ignored multi-word keywords");

        private final String displayName;

        Type(String displayName) {
            String normalized = requireNonNull(displayName).trim();
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Warning type display name cannot be blank.");
            }
            this.displayName = normalized;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Type type;
    private final String message;

    private Warning(Type type, String message) {
        this.type = requireNonNull(type);
        String normalized = requireNonNull(message).trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Warning message cannot be blank.");
        }
        this.message = normalized;
    }

    /**
     * Creates a warning indicating that duplicate user input was ignored.
     */
    public static Warning duplicateInputIgnored(String message) {
        return new Warning(Type.DUPLICATE_INPUT_IGNORED, message);
    }

    /**
     * Creates a warning indicating that one or more custom fields were overwritten.
     */
    public static Warning fieldOverwritten(String message) {
        return new Warning(Type.FIELD_OVERWRITTEN, message);
    }

    /**
     * Creates a warning indicating that one or more provided keywords were blank and ignored.
     */
    public static Warning ignoredBlankKeywords(String message) {
        return new Warning(Type.IGNORED_BLANK_KEYWORDS, message);
    }

    /**
     * Creates a warning indicating that one or more provided keywords were multi-word tokens and ignored.
     */
    public static Warning ignoredMultiwordKeywords(String message) {
        return new Warning(Type.IGNORED_MULTIWORD_KEYWORDS, message);
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Formats this warning for user-facing display.
     */
    public String formatForDisplay() {
        return type.getDisplayName() + ": " + message;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Warning)) {
            return false;
        }
        Warning otherWarning = (Warning) other;
        return type == otherWarning.type && message.equals(otherWarning.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }

    @Override
    public String toString() {
        return Warning.class.getCanonicalName() + "{"
                + "type=" + type
                + ", message='" + message + "'" + "}";
    }
}
