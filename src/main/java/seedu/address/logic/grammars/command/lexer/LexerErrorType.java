package seedu.address.logic.grammars.command.lexer;

/**
 * Enumeration of all error types the lexer may encounter.
 */
enum LexerErrorType {
    UNEXPECTED_CHARACTER("Unexpected character"),
    UNTERMINATED_STRING("Unterminated string");

    private final String genericDescription;

    LexerErrorType(String genericDescription) {
        this.genericDescription = genericDescription;
    }

    String getGenericDescription() {
        return genericDescription;
    }
}
