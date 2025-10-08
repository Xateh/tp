package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validArgsWithRemark_returnsRemarkCommand() {
        String remarkText = "Lunch on Friday";
        assertParseSuccess(parser, "1 r/" + remarkText,
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(remarkText)));
    }

    @Test
    public void parse_validArgsWithoutRemark_returnsRemarkCommand() {
        assertParseSuccess(parser, "1", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "a r/Lunch",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "r/Lunch",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateRemarkPrefix_throwsParseException() {
        assertParseFailure(parser, "1 r/First r/Second",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_REMARK));
    }
}
