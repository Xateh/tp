package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.tag.Tag;

/**
 * Contains tests for {@link UntagCommandParser}.
 */
public class UntagCommandParserTest {

    private static final String TAG_DESC_FRIENDS = " t/friends";
    private static final String INVALID_TAG = " t/friends!";

    private final UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_validArgs_returnsUntagCommand() {
        Index index = Index.fromOneBased(1);
        Tag tag = new Tag("friends");
        assertParseSuccess(parser, "1" + TAG_DESC_FRIENDS, new UntagCommand(index, tag));
    }

    @Test
    public void parse_missingTag_throwsParseException() {
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, TAG_DESC_FRIENDS, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "-1" + TAG_DESC_FRIENDS, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, "1" + INVALID_TAG, Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateTagPrefix_throwsParseException() {
        assertParseFailure(parser, "1" + TAG_DESC_FRIENDS + TAG_DESC_FRIENDS,
            Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TAG));
    }
}
