package seedu.address.logic.grammars.command.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.grammars.command.Command;

class CommandLexerHyphenTest {

    @Test
    void dashedOptionNameSupported() throws Exception {
        Command c = Command.parse("field 1 /asset-class:gold");
        assertEquals("gold", c.getOptionValue("asset-class"));
    }

    @Test
    void spacesAroundColonAllowed() throws Exception {
        Command c = Command.parse("field 1 /asset-class : gold");
        assertEquals("gold", c.getOptionValue("asset-class"));
    }
}

