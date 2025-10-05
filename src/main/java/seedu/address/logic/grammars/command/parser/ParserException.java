package seedu.address.logic.grammars.command.parser;

/**
 * Exception for parsing errors (failures to apply production rules).
 */
public class ParserException extends Exception {
    private final ParserError parserError;

    ParserException(ParserError parserError) {
        super(parserError.toString());
        this.parserError = parserError;
    }
}
