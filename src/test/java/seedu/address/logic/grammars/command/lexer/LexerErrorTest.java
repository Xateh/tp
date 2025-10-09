package seedu.address.logic.grammars.command.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.grammars.command.utils.Location;

public class LexerErrorTest {
    @Test
    public void lexerError_toString_success() {
        LexerError le = new LexerError(LexerErrorType.UNTERMINATED_STRING,
                "test *this range*", "*this range*", new Location(5, 17));

        String expected = """
                Error occurred during lexing.
                test *this range*
                     ^----------^
                Unterminated string: *this range*\
                """;

        assertEquals(expected, le.toString());
    }
}
