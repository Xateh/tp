package seedu.address.logic.grammars.command.lexer;

import static seedu.address.logic.grammars.command.utils.Utils.makeVisualDelimiter;

import java.util.ArrayList;

import seedu.address.logic.grammars.command.utils.Location;

/**
 * Errors encountered by the lexer.
 */
class LexerError {
    private final LexerErrorType lexerErrorType;
    private final String ingest;
    private final String offendingLiteral;
    private final Location location;

    /**
     * Constructs a new LexerError, encapsulating details of an error occurring during lexing.
     *
     * @param lexerErrorType   Lexer error type.
     * @param ingest           Original string input the lexer was lexing at the point of the error.
     * @param offendingLiteral The offending literal the lexer was attempting to lex when the error occurred.
     * @param location         Location of the offending literal in the original string input.
     */
    LexerError(LexerErrorType lexerErrorType, String ingest, String offendingLiteral, Location location) {
        this.lexerErrorType = lexerErrorType;
        this.ingest = ingest;
        this.offendingLiteral = offendingLiteral;
        this.location = location;
    }

    @Override
    public String toString() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("Error occurred during lexing.");

        lines.add(this.ingest);

        lines.add(makeVisualDelimiter(this.location.start(), this.location.end()));

        lines.add(String.format("%s: %s", this.lexerErrorType.getGenericDescription(), this.offendingLiteral));

        return String.join("\n", lines);
    }
}
