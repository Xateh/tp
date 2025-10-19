package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

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
                        true, false, false, false, false);

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alice /name"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_nameAndEmail() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alex"),
                        true, false, true, false, false);

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find alex /name /email"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_tagsAlias() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("friend"),
                        false, false, false, false, true);

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
                        false, true, false, false, false);

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find 9123 /phone"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_addressOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("jurong"),
                        false, false, false, true, false);

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find jurong /address"));

        assertEquals(expected, actual);
    }

    @Test
    public void parse_validArgs_emailOnly() throws LexerException, ParserException, ValidationException {
        FieldContainsKeywordsPredicate expectedPredicate =
                new FieldContainsKeywordsPredicate(List.of("alden@gmail.com"),
                        false, false, true, false, false);

        FindCommand expected = new FindCommand(expectedPredicate);
        FindCommand actual = FindCommandExtractor.extract(BareCommand.parse("find \"alden@gmail.com\" /email"));


        assertEquals(expected, actual);
    }
}
