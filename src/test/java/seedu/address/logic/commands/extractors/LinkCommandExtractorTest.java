package seedu.address.logic.commands.extractors;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;

/**
 * Tests for {@link LinkCommandExtractor}.
 */
public class LinkCommandExtractorTest {

    @Test
    public void extract_validArgs_success() throws Exception {
        BareCommand bare = BareCommand.parse("link 1 mentor 2");
        LinkCommand cmd = LinkCommandExtractor.extract(bare);

        LinkCommand expected = new LinkCommand(Index.fromOneBased(1), "mentor", Index.fromOneBased(2));
        assertEquals(expected, cmd);
    }

    @Test
    public void extract_withQuotedLinkName_success() throws Exception {
        BareCommand bare = BareCommand.parse("link 3 \"best friend\" 5");
        LinkCommand cmd = LinkCommandExtractor.extract(bare);

        LinkCommand expected = new LinkCommand(Index.fromOneBased(3), "best friend", Index.fromOneBased(5));
        assertEquals(expected, cmd);
    }

    @Test
    public void extract_missingParameters_throwsValidationException() throws LexerException, ParserException {
        BareCommand bare = BareCommand.parse("link 1 mentor");
        assertThrows(ValidationException.class, () -> LinkCommandExtractor.extract(bare));
    }

    @Test
    public void extract_nonNumericIndexes_throwsValidationException() throws LexerException, ParserException {
        BareCommand bare = BareCommand.parse("link one mentor two");
        assertThrows(ValidationException.class, () -> LinkCommandExtractor.extract(bare));
    }

    @Test
    public void extract_emptyLinkName_throwsValidationException() throws LexerException, ParserException {
        BareCommand bare = BareCommand.parse("link 1 \"\" 2");
        assertThrows(ValidationException.class, () -> LinkCommandExtractor.extract(bare));
    }

    @Test
    public void extract_sameIndexes_throwsValidationException()
            throws LexerException, ParserException {
        // "link 2 friend 2" → both indexes are the same
        BareCommand bare = BareCommand.parse("link 2 friend 2");

        assertThrows(ValidationException.class, () -> LinkCommandExtractor.extract(bare),
                "Cannot link a person to themselves.");
    }
    @Test
    public void extract_additiveParamKind_throwsValidationException()
            throws LexerException, ParserException {
        // “+1” should be ADDITIVE kind → rejected
        assertThrows(ValidationException.class, () ->
                LinkCommandExtractor.extract(BareCommand.parse("link +1 mentor 2")));
    }

    @Test
    public void extract_subtractiveParamKind_throwsValidationException()
            throws LexerException, ParserException {
        // “-2” should be SUBTRACTIVE kind → rejected
        assertThrows(ValidationException.class, () ->
                LinkCommandExtractor.extract(BareCommand.parse("link 1 mentor -2")));
    }

    @Test
    public void extract_tooManyParams_doesNotThrowValidationException()
            throws LexerException, ParserException {
        assertDoesNotThrow(() ->
                LinkCommandExtractor.extract(BareCommand.parse("link 1 mentor 2 3")));
    }

    @Test
    public void extract_zeroIndex_throwsValidationException()
            throws LexerException, ParserException {
        assertThrows(ValidationException.class, () ->
                LinkCommandExtractor.extract(BareCommand.parse("link 0 mentor 2")));
    }

    @Test
    public void extract_negativeIndex_throwsValidationException()
            throws LexerException, ParserException {
        assertThrows(ValidationException.class, () ->
                LinkCommandExtractor.extract(BareCommand.parse("link -1 mentor 2")));
    }

    @Test
    public void extract_invalidLinkNameChars_throwsValidationException()
            throws LexerException, ParserException {
        // Depends on your Link.isValidLinkName(), “@@@” should fail
        assertThrows(ValidationException.class, () ->
                LinkCommandExtractor.extract(BareCommand.parse("link 1 \"@@@\" 2")));
    }

    @Test
    public void extract_validLinkNameChars_success() throws Exception {
        BareCommand bare = BareCommand.parse("link 3 \"lawyer #1\" 5");
        LinkCommand cmd = LinkCommandExtractor.extract(bare);
        LinkCommand expected = new LinkCommand(Index.fromOneBased(3), "lawyer #1", Index.fromOneBased(5));
        assertEquals(cmd, expected);
    }
}
