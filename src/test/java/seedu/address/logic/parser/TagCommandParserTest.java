package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {

    private TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_validArgs_returnsTagCommand() {
        // single tag
        Set<Tag> expectedSingleTag = Set.of(new Tag("friend"));
        assertParseSuccess(parser, "1 friend", new TagCommand(INDEX_FIRST_PERSON, expectedSingleTag));

        // multiple tags
        Set<Tag> expectedMultipleTags = Set.of(new Tag("friend"), new Tag("colleague"));
        assertParseSuccess(parser, "1 friend colleague", new TagCommand(INDEX_FIRST_PERSON, expectedMultipleTags));

        // tags with extra whitespace
        assertParseSuccess(parser, " 1  friend   colleague ", new TagCommand(INDEX_FIRST_PERSON, expectedMultipleTags));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no arguments
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // only whitespace
        assertParseFailure(parser, "   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // no index
        assertParseFailure(parser, "friend", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // invalid index
        assertParseFailure(parser, "0 friend", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "-1 friend", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                TagCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "a friend", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // no tags after valid index
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        // invalid tag names (non-alphanumeric)
        assertParseFailure(parser, "1 friend@123", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 friend-colleague", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 friend colleague!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateTags_removedAutomatically() {
        // duplicate tags should be handled by Set automatically
        Set<Tag> expectedTags = Set.of(new Tag("friend"));
        assertParseSuccess(parser, "1 friend friend", new TagCommand(INDEX_FIRST_PERSON, expectedTags));
    }

    @Test
    public void parse_emptyTagNames_throwsParseException() {
        // empty strings should be filtered out, but if all are empty, should fail
        assertParseFailure(parser, "1  ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
    }
}
