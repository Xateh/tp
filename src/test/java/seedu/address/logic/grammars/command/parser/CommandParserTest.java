package seedu.address.logic.grammars.command.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.grammars.command.lexer.CommandLexer;
import seedu.address.logic.grammars.command.parser.ast.AstNode;
import seedu.address.logic.grammars.command.parser.ast.visitors.AstPrinter;

public class CommandParserTest {
    @Test
    public void parse_imperativeOnly_success() {
        String ingest = "test";

        AstNode.Command root = assertDoesNotThrow(() -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));

        String expected = """
                Command
                ├─ Imperative
                │  └─ Word ("test")
                ├─ ParameterList
                └─ OptionList\
                """;

        String tree = new AstPrinter().print(root);

        assertEquals(expected, tree);
    }

    @Test
    public void parse_imperativeAndParameters_success() {
        String ingest = "test param1 param2 param3";

        AstNode.Command root = assertDoesNotThrow(() -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));

        String expected = """
                Command
                ├─ Imperative
                │  └─ Word ("test")
                ├─ ParameterList
                │  ├─ Parameter
                │  │  └─ Text ("param1")
                │  ├─ Parameter
                │  │  └─ Text ("param2")
                │  └─ Parameter
                │     └─ Text ("param3")
                └─ OptionList\
                """;

        String tree = new AstPrinter().print(root);

        assertEquals(expected, tree);
    }

    @Test
    public void parse_imperativeAndOptions_success() {
        String ingest = "test /boolopt /opt2:\"value2\"";

        AstNode.Command root = assertDoesNotThrow(() -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));

        String expected = """
                Command
                ├─ Imperative
                │  └─ Word ("test")
                ├─ ParameterList
                └─ OptionList
                   ├─ Option
                   │  └─ OptionName
                   │     └─ Word ("boolopt")
                   └─ Option
                      ├─ OptionName
                      │  └─ Word ("opt2")
                      └─ OptionValue
                         └─ Text ("value2")\
                """;

        String tree = new AstPrinter().print(root);

        assertEquals(expected, tree);
    }

    @Test
    public void parse_longCommand_success() {
        String ingest = "event create /important "
                + "/description:\"online quiz\" /from:\"2025-09-20 1000\" /to:\"2025-09-20 1100\"";

        AstNode.Command root = assertDoesNotThrow(() -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));

        String expected = """
                Command
                ├─ Imperative
                │  └─ Word ("event")
                ├─ ParameterList
                │  └─ Parameter
                │     └─ Text ("create")
                └─ OptionList
                   ├─ Option
                   │  └─ OptionName
                   │     └─ Word ("important")
                   ├─ Option
                   │  ├─ OptionName
                   │  │  └─ Word ("description")
                   │  └─ OptionValue
                   │     └─ Text ("online quiz")
                   ├─ Option
                   │  ├─ OptionName
                   │  │  └─ Word ("from")
                   │  └─ OptionValue
                   │     └─ Text ("2025-09-20 1000")
                   └─ Option
                      ├─ OptionName
                      │  └─ Word ("to")
                      └─ OptionValue
                         └─ Text ("2025-09-20 1100")\
                """;

        String tree = new AstPrinter().print(root);

        assertEquals(expected, tree);
    }

    @Test
    public void parse_emptyCommand_throwsException() {
        String ingest = "";

        assertThrows(ParserException.class, () -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));
    }

    @Test
    public void parse_invalidTokenWhenTextExpectedInParameter_throwsException() {
        String ingest = "test :";

        assertThrows(ParserException.class, () -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));
    }

    @Test
    public void parse_invalidTokenWhenWordExpectedInOptionName_throwsException() {
        String ingest = "test /\"opt1\":word";

        assertThrows(ParserException.class, () -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));
    }

    @Test
    public void parse_invalidTokenWhenTextExpectedInOptionValue_throwsException() {
        String ingest = "test /opt1::";

        assertThrows(ParserException.class, () -> CommandParser.parseCommand(CommandLexer.lexCommand(ingest)));
    }
}
