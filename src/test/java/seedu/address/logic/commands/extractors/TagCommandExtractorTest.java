package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.model.tag.Tag;

public class TagCommandExtractorTest {
    @Test
    public void extract_validArgs_returnsTagCommand() throws LexerException, ParserException, ValidationException {
        // single tag
        Set<Tag> expectedSingleTag = Set.of(new Tag("friend"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedSingleTag),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 friend")));

        // multiple tags
        Set<Tag> expectedMultipleTags = Set.of(new Tag("friend"), new Tag("colleague"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedMultipleTags),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 friend colleague")));
    }

    @Test
    public void extract_invalidArgs_throwsException() throws LexerException, ParserException, ValidationException {
        // no index
        assertThrows(ValidationException.class, () ->
                TagCommandExtractor.extract(BareCommand.parse("tag")));
        assertThrows(ValidationException.class, () ->
                TagCommandExtractor.extract(BareCommand.parse("tag friend")));

        // invalid index
        assertThrows(ValidationException.class, () ->
                TagCommandExtractor.extract(BareCommand.parse("tag 0 friend")));
        assertThrows(ValidationException.class, () ->
                TagCommandExtractor.extract(BareCommand.parse("tag a friend")));

        // no tags after valid index
        assertThrows(ValidationException.class, () ->
                TagCommandExtractor.extract(BareCommand.parse("tag 1")));
    }

    @Test
    public void extract_duplicateTags_removed() throws LexerException, ParserException, ValidationException {
        // duplicate tags should be handled by Set automatically
        Set<Tag> expectedTags = Set.of(new Tag("friend"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedTags),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 friend friend")));
    }
}
