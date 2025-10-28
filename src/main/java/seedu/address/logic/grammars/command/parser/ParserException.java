package seedu.address.logic.grammars.command.parser;

import seedu.address.logic.exceptions.AssemblyException;

/**
 * Exception for parsing errors (failures to apply production rules).
 */
public final class ParserException extends AssemblyException {
    private final ParserError parserError;

    ParserException(ParserError parserError) {
        super(parserError.toString());
        this.parserError = parserError;
    }
}
