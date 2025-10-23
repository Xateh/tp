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
        // single added tag
        Set<Tag> expectedSingleAddedTag = Set.of(new Tag("friend"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedSingleAddedTag, Set.of()),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 +friend")));

        // multiple added tags
        Set<Tag> expectedMultipleAddedTags = Set.of(new Tag("friend"), new Tag("colleague"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedMultipleAddedTags, Set.of()),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 +friend +colleague")));

        // single removed tag
        Set<Tag> expectedSingleRemovedTag = Set.of(new Tag("friend"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, Set.of(), expectedSingleRemovedTag),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 -friend")));

        // multiple removed tags
        Set<Tag> expectedMultipleRemovedTags = Set.of(new Tag("friend"), new Tag("colleague"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, Set.of(), expectedMultipleRemovedTags),
                TagCommandExtractor.extract(BareCommand.parse("tag 1 -friend -colleague")));

        // mixed tags
        Set<Tag> expectedMultipleAddedMixedTags = Set.of(new Tag("friend"), new Tag("colleague"));
        Set<Tag> expectedMultipleRemovedMixedTags = Set.of(new Tag("villain"), new Tag("enemy"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedMultipleAddedMixedTags,
                expectedMultipleRemovedMixedTags), TagCommandExtractor.extract(BareCommand.parse(
                "tag 1 +friend -villain +colleague -enemy")));
    }

    @Test
    public void extract_invalidIndex_throwsException() {
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
    public void extract_invalidTags_throwsException() {
        // no tags supplied
        assertThrows(ValidationException.class, () -> TagCommandExtractor.extract(
                BareCommand.parse("tag 1")));
    }

    @Test
    public void extract_duplicateTags_removed() throws LexerException, ParserException, ValidationException {
        // duplicate tags should be handled by Set automatically
        Set<Tag> expectedAddedTags = Set.of(new Tag("friend"));
        Set<Tag> expectedRemovedTags = Set.of(new Tag("enemy"));
        assertEquals(new TagCommand(INDEX_FIRST_PERSON, expectedAddedTags, expectedRemovedTags),
                TagCommandExtractor.extract(BareCommand.parse(
                        "tag 1 -enemy +friend -enemy +friend +friend -enemy")));
    }
}
