package seedu.address.logic.grammars.command.lexer;

/**
 * Exception for errors encountered during lexing.
 */
public class LexerException extends Exception {
    private final LexerError lexerError;

    /**
     * Constructs a new LexerException.
     *
     * @param lexerError LexerError containing details of the encountered error.
     */
    LexerException(LexerError lexerError) {
        super(lexerError.toString());
        this.lexerError = lexerError;
    }
}
