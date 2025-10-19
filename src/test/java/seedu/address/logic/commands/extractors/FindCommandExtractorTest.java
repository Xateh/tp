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
                        true, false, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alice /name"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_nameAndEmail() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alex"),
                        true, false, true, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alex /name /email"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_tagsAlias() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("friend"),
                        false, false, false, false, true, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actualTag = FindCommandExtractor.extract(BareCommand.parse("find friend /tag"));
        FindCommand actualTags = FindCommandExtractor.extract(BareCommand.parse("find friend /tags"));

        assertEquals(expected, actualTag);
        assertEquals(expected, actualTags);
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
                        false, true, false, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find 9123 /phone"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_addressOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("jurong"),
                        false, false, false, true, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find jurong /address"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_emailOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alden@gmail.com"),
                        false, false, true, false, false, Set.of());

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find \"alden@gmail.com\" /email"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_customFieldsOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("gold"),
                        false, false, false, false, false, Set.of("assetclass"));
        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find gold /assetclass"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_customAndName() throws LexerException, ParserException, ValidationException {
        // Mix of built-in (/name) and custom (/company)
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("Alpha"), true, false, false, false, false,
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
                        false, false, false, false, false,
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
                        false, false, false, false, false,
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
                        false, false, false, false, true, // tag=true because /tags
                        Set.of("department", "office"));

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(
                BareCommand.parse("find friend /tags /department /office"));

        assertEquals(expected, actual);
    }
}
