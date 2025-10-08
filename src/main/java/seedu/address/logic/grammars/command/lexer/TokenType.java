package seedu.address.logic.grammars.command.lexer;

/**
 * Enumeration of all tokens types the lexer may produce.
 */
public enum TokenType {
    WORD("WORD"),
    TEXT("TEXT"),
    SLASH("SLASH"),
    COLON("COLON"),
    TERMINAL("TERMINAL");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
