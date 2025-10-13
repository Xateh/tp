package seedu.address.logic.grammars.command.lexer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BareCommandLexerTest {
    @Test
    public void lex_word_success() {
        String ingest = "test Test test0";

        String expected = """
                [00:04] WORD : test
                [05:09] WORD : Test
                [10:15] WORD : test0
                [15:15] TERMINAL : \
                """;

        TokenisedCommand tc = assertDoesNotThrow(() -> CommandLexer.lexCommand(ingest));
        assertEquals(expected, tc.toString());
    }

    @Test
    public void lex_text_success() {
        String ingest = "\"test test test\"";

        String expected = "[01:15] TEXT : test test test\n"
                + "[16:16] TERMINAL : ";

        TokenisedCommand tc = assertDoesNotThrow(() -> CommandLexer.lexCommand(ingest));
        assertEquals(expected, tc.toString());
    }

    @Test
    public void lex_slash_success() {
        String ingest = "/";

        String expected = "[00:01] SLASH : /\n"
                + "[01:01] TERMINAL : ";

        TokenisedCommand tc = assertDoesNotThrow(() -> CommandLexer.lexCommand(ingest));
        assertEquals(expected, tc.toString());
    }

    @Test
    public void lex_colon_success() {
        String ingest = ":";

        String expected = "[00:01] COLON : :\n"
                + "[01:01] TERMINAL : ";

        TokenisedCommand tc = assertDoesNotThrow(() -> CommandLexer.lexCommand(ingest));
        assertEquals(expected, tc.toString());
    }

    @Test
    public void lex_longString_success() {
        String ingest = "event create /description:\"online quiz\" /from:\"2025-09-20 1000\" /to:\"2025-09-20 1100\"";

        String expected = """
                [00:05] WORD : event
                [06:12] WORD : create
                [13:14] SLASH : /
                [14:25] WORD : description
                [25:26] COLON : :
                [27:38] TEXT : online quiz
                [40:41] SLASH : /
                [41:45] WORD : from
                [45:46] COLON : :
                [47:62] TEXT : 2025-09-20 1000
                [64:65] SLASH : /
                [65:67] WORD : to
                [67:68] COLON : :
                [69:84] TEXT : 2025-09-20 1100
                [85:85] TERMINAL : \
                """;

        TokenisedCommand tc = assertDoesNotThrow(() -> CommandLexer.lexCommand(ingest));
        assertEquals(expected, tc.toString());
    }

    @Test
    public void lex_unexpectedCharacter_throwsException() {
        String ingest = "event create /description:\"online quiz\" /from:\"2025-09-20 1000\" % /to:\"2025-09-20 1100\"";

        assertThrows(LexerException.class, () -> CommandLexer.lexCommand(ingest));
    }

    @Test
    public void lex_unterminatedString_throwsException() {
        String ingest = "event create /description:\"online quiz\" /from:\"2025-09-20 1000 /to:\"2025-09-20 1100\"";

        assertThrows(LexerException.class, () -> CommandLexer.lexCommand(ingest));
    }

    @Test
    public void lex_illegalCharacterInString_throwsException() {
        String ingest = "event create /description:\"online quiz\" /from:\"2025-09-20:1000\" /to:\"2025-09-20 1100\"";

        assertThrows(LexerException.class, () -> CommandLexer.lexCommand(ingest));
    }
}
