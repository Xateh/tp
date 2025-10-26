package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.model.person.FieldContainsKeywordsPredicate;

public class FindCommandExtractorTest {

    @Test
    public void parse_validArgs_allFields() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alice", "bob"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alice bob"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_nameOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alice"),
                        true, false, false, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alice /name"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_nameAndEmail() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alex"),
                        true, false, true, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alex /name /email"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_tagsAlias() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("friend"),
                        false, false, false, false, true, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actualTag = FindCommandExtractor.extract(BareCommand.parse("find friend /tag"));

        assertEquals(expected, actualTag);
    }

    @Test
    public void parse_invalidArgs_noKeywords() throws LexerException, ParserException {
        assertThrows(ValidationException.class, () ->
                FindCommandExtractor.extract(BareCommand.parse("find")));
        assertThrows(ValidationException.class, () ->
                FindCommandExtractor.extract(BareCommand.parse("find /name")));
    }

    @Test
    public void parse_validArgs_phoneOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("9123"),
                        false, true, false, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find 9123 /phone"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_addressOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("jurong"),
                        false, false, false, true, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find jurong /address"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_emailOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alden@gmail.com"),
                        false, false, true, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find \"alden@gmail.com\" /email"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_customFieldsOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("gold"),
                        false, false, false, false, false, false, false, Set.of("assetclass"));
        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find gold /assetclass"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_customAndName() throws LexerException, ParserException, ValidationException {
        // Mix of built-in (/name) and custom (/company)
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("Alpha"), true, false, false, false, false, false, false,
                        Set.of("company"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find Alpha /name /company"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_multipleCustom() throws LexerException, ParserException, ValidationException {
        // Multiple custom keys should all be included
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("Asia"),
                        false, false, false, false, false, false, false,
                        Set.of("region", "assetclass"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find Asia /region /assetclass"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_customCaseInsensitive() throws LexerException, ParserException, ValidationException {
        // Custom option names should be treated case-insensitively and normalized to lowercase
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("Growth"),
                        false, false, false, false, false, false, false,
                        Set.of("strategy"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find Growth /StrateGy"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_builtInsAndCustoms() throws LexerException, ParserException, ValidationException {
        // Built-in alias /tags plus two custom keys
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("friend"),
                        false, false, false, false, true, false, false, // tag=true because /tags
                        Set.of("department", "office"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find friend /tag /department /office"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_linkerOnly() throws LexerException, ParserException, ValidationException {
        // find all people who are the linker for links named "lawyer"
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("lawyer"),
                        false, false, false, false, false,
                        /*searchLinker*/ true,
                        /*searchLinkee*/ false,
                        Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find lawyer /from"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_linkeeOnly() throws LexerException, ParserException, ValidationException {
        // find all people who are the linkee for links named "client"
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("client"),
                        false, false, false, false, false,
                        /*searchLinker*/ false,
                        /*searchLinkee*/ true,
                        Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find client /to"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_linkerAndLinkee() throws LexerException, ParserException, ValidationException {
        // both /from and /to flags set
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("mentor"),
                        false, false, false, false, false,
                        /*searchLinker*/ true,
                        /*searchLinkee*/ true,
                        Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find mentor /from /to"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_nameAndLinker() throws LexerException, ParserException, ValidationException {
        // mix a built-in flag with /from
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("advisor"),
                        /*name*/ true, /*phone*/ false, /*email*/ false, /*address*/ false, /*tag*/ false,
                        /*from*/ true, /*to*/ false,
                        Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find advisor /name /from"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validQuotedMultiWordLink_linker() throws LexerException, ParserException, ValidationException {
        // quoted keyword with /from
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("best" , "friend"),
                        false, false, false, false, false,
                        /*from*/ true, /*to*/ false,
                        Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual =
                FindCommandExtractor.extract(BareCommand.parse("find best friend /from"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_linksAndCustom() throws LexerException, ParserException, ValidationException {
        // combine /to with a custom key; both should be honored
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("partner"),
                        false, false, false, false, false,
                        /*from*/ false, /*to*/ true,
                        Set.of("department"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual =
                FindCommandExtractor.extract(BareCommand.parse("find partner /to /department"));

        assertEquals(expected, actual);
    }
}
