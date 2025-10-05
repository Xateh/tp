package seedu.address.logic.grammars.command.lexer;

import java.util.ArrayList;

/**
 * Lexed command, representing a linear stream of tokens.
 */
public class TokenisedCommand {
    private final String ingest;
    private final ArrayList<Token> tokens;

    /**
     * Constructs a new TokenisedCommand.
     *
     * @param ingest Original string input.
     * @param tokens Tokens lexed by the command lexer.
     */
    TokenisedCommand(String ingest, ArrayList<Token> tokens) {
        this.ingest = ingest;
        this.tokens = tokens;
    }

    public String getIngest() {
        return this.ingest;
    }

    public Token getAtIndex(int index) {
        return this.tokens.get(index);
    }

    @Override
    public String toString() {
        String[] tokens = this.tokens.stream().map(t -> t.toString()).toArray(String[]::new);
        return String.join("\n", tokens);
    }
}
