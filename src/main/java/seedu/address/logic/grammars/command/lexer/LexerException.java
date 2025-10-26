package seedu.address.logic.grammars.command.lexer;

import seedu.address.logic.exceptions.AssemblyException;

/**
 * Exception for errors encountered during lexing.
 */
public final class LexerException extends AssemblyException {
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
