package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    public void parse_validArgs_customCaseSensitive() throws LexerException, ParserException, ValidationException {
        // Custom option names should be treated case-insensitively and normalized to lowercase
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("Growth"),
                        false, false, false, false, false, false, false,
                        Set.of("strategy"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find Growth /StrateGy"));

        assertNotEquals(expected, actual);
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
    @Test
    public void parse_quotedMultiWordToken_throws() throws LexerException, ParserException {
        // A single token containing a space must be rejected
        assertThrows(ValidationException.class, () ->
                FindCommandExtractor.extract(BareCommand.parse("find \"alex yeoh\" /name")));
    }

    @Test
    public void parse_emptyQuotedKeyword_throws() throws LexerException, ParserException {
        // Empty keyword after trimming is invalid
        assertThrows(ValidationException.class, () ->
                FindCommandExtractor.extract(BareCommand.parse("find \"\" /name")));
    }

    @Test
    public void parse_builtInFlags_caseSensitive()
            throws LexerException, ParserException, ValidationException {
        // Upper/mixed-case built-in flags are ignored, search all built in fields instead
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("ALICE"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find ALICE /NAME /eMaIl"));

        assertNotEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_duplicateFlagsIgnored()
            throws LexerException, ParserException, ValidationException {
        // Repeating the same flag shouldn't change semantics
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("bob"),
                        true, false, false, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find bob /name /name /NaMe"));

        assertNotEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_allBuiltInsEnabled()
            throws LexerException, ParserException, ValidationException {
        // Turn on every built-in selector, without custom keys
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("k"),
                        true, true, true, true, true, true, true, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find k /name /phone /email /address /tag /from /to"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_mixedBuiltInsAndCustom_onlyCustomInSet()
            throws LexerException, ParserException, ValidationException {
        // Built-ins must not appear inside the custom key set
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("john"),
                        true, false, false, false, true, false, false,
                        Set.of("department", "team"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find john /name /tag /department /team"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_repeatedCustomKeys_deduplicated()
            throws LexerException, ParserException, ValidationException {
        // The resulting Set should contain each custom key once
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("asia"),
                        false, false, false, false, false, false, false,
                        Set.of("region"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find asia /region /Region /REGION"));

        assertNotEquals(expected, actual);
    }

    @Test
    public void parse_keywordsSplitAcrossTokens_areAccepted()
            throws LexerException, ParserException, ValidationException {
        // Multiple single-word tokens are fine (no quotes)
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("best", "friend"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find best friend"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_leadingAndTrailingSpaces_valid()
            throws LexerException, ParserException, ValidationException {
        // Parser should yield trimmed tokens; extractor also trims defensively
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alice"),
                        true, false, false, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        // Multiple spaces between tokens should not matter
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find     alice      /name"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_linksAndBuiltInsAndCustoms_combined()
            throws LexerException, ParserException, ValidationException {
        // Ensure simultaneous mix sets the right booleans and custom set
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("partner"),
                        true, false, true, false, false, true, true,
                        Set.of("office", "department"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find partner /name /email /from /to /office /department"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_onlyCustoms_triggersSelectivePredicate()
            throws LexerException, ParserException, ValidationException {
        // No built-ins but at least one custom -> selective predicate (flags false, custom set non-empty)
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("growth"),
                        false, false, false, false, false, false, false, Set.of("strategy"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find growth /strategy"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_onlyWhitespaceKeyword_throws()
            throws LexerException, ParserException {
        // If BareCommand preserves a whitespace-only token via quotes, extractor must reject it
        assertThrows(ValidationException.class, () ->
                FindCommandExtractor.extract(BareCommand.parse("find \"   \" /name")));
    }
}
