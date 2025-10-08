package seedu.address.logic.grammars.command.parser;

/**
 * Exception for internal use only by the parser. Used to unwind the stack in the recursive descent to trace the path of
 * production rules taken at the point of encountering a parsing error.
 */
class ProductionApplicationException extends Exception {
    private final ParserError parserError;

    public ProductionApplicationException(ParserError parserError) {
        this.parserError = parserError;
    }

    public ParserError getParserError() {
        return parserError;
    }
}
