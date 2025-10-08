package seedu.address.logic.grammars.command.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LexerErrorTypeTest {
    @Test
    public void lexerErrorType_getGenericDescriptions_success() {
        assertEquals("Unexpected character", LexerErrorType.UNEXPECTED_CHARACTER.getGenericDescription());
        assertEquals("Unterminated string", LexerErrorType.UNTERMINATED_STRING.getGenericDescription());
    }
}
