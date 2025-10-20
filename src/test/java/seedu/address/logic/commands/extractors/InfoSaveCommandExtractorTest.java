package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.InfoSaveCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.model.person.Info;

public class InfoSaveCommandExtractorTest {

    @Test
    public void parse_validArgs_returnsInfoSaveCommand() throws LexerException, ParserException, ValidationException {
        // simple text - "Test" in hex
        String simpleHex = "54657374";
        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, new Info("Test")),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + simpleHex)));
        // different index
        assertEquals(new InfoSaveCommand(INDEX_SECOND_PERSON, new Info("Test")),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 2 " + simpleHex)));
    }

    @Test
    public void parse_complexInfo_returnsInfoSaveCommand() throws LexerException, ParserException, ValidationException {
        // multiline text - "Line1\nLine2" in hex (0A is newline in hex)
        String multilineHex = "4C696E65310A4C696E6532";
        Info expectedInfo = new Info("Line1\nLine2");
        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, expectedInfo),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + multilineHex)));

        // text with special characters - "Client: $100k" in hex
        String specialHex = "436C69656E743A20243130306B";
        Info expectedSpecialInfo = new Info("Client: $100k");
        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, expectedSpecialInfo),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + specialHex)));
    }

    @Test
    public void parse_invalidArgs_throwsValidationException() throws LexerException, ParserException {
        // no parameters
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave")));

        // only index, no info
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1")));

        // invalid index
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 0 54657374")));
        assertThrows(LexerException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave -1 54657374")));
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave a 54657374")));
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave abc 54657374")));
    }

    @Test
    public void parse_invalidHex_throwsValidationException() throws LexerException, ParserException {
        // odd length hex string
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 5465737")));

        // invalid hex characters
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 invalidhex")));
        assertThrows(ValidationException.class, () ->
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 GGHHII")));
    }

    @Test
    public void parse_unicodeContent_success() throws LexerException, ParserException, ValidationException {
        // Unicode content should be properly encoded/decoded
        // "Testing ταβ" in hex (UTF-8 encoded)
        String unicodeHex = "54657374696E6720CF84CEB1CEB2";
        Info expectedUnicodeInfo = new Info("Testing ταβ");
        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, expectedUnicodeInfo),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + unicodeHex)));
    }

    @Test
    public void parse_caseInsensitiveHex_success() throws LexerException, ParserException, ValidationException {
        // hex should work with both uppercase and lowercase
        String lowercaseHex = "54657374";
        String uppercaseHex = "54657374";
        Info expectedInfo = new Info("Test");

        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, expectedInfo),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + lowercaseHex)));
        assertEquals(new InfoSaveCommand(INDEX_FIRST_PERSON, expectedInfo),
                InfoSaveCommandExtractor.extract(BareCommand.parse("infosave 1 " + uppercaseHex)));
    }
}
