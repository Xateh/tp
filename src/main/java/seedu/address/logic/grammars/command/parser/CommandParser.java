package seedu.address.logic.grammars.command.parser;

import java.util.ArrayList;

import seedu.address.logic.grammars.command.lexer.Token;
import seedu.address.logic.grammars.command.lexer.TokenType;
import seedu.address.logic.grammars.command.lexer.TokenisedCommand;
import seedu.address.logic.grammars.command.parser.ast.AstNode;

/**
 * LL(1) parser for commands. Recognises the following context-free grammar (Level 2) consisting of tokens obtained from
 * the lexer (tokens from lexer are in CAPITAL):
 * <pre>
 * {@code
 * command          → imperative parameter_list option_list TERMINAL
 * imperative       → WORD
 * parameter_list   → ( parameter )+
 * parameter        → text
 * option_list      → ( option )+
 * option           → SLASH option_name ( COLON option_value )*
 * option_name      → word
 * option_value     → text
 * text             → TEXT | WORD
 * word             → WORD
 * }
 * </pre>
 */
public class CommandParser {
    private final TokenisedCommand tokenisedCommand;
    private int currentTokenIndex = 0;

    private CommandParser(TokenisedCommand tokenisedCommand) {
        this.tokenisedCommand = tokenisedCommand;
    }

    /**
     * Parses an input command.
     *
     * @param command Tokenised command to be parsed.
     * @return Command AST node (root).
     * @throws ParserException If the command fails to be parsed.
     */
    public static AstNode.Command parseCommand(TokenisedCommand command) throws ParserException {
        CommandParser parser = new CommandParser(command);

        AstNode.Command root;
        try {
            root = parser.parseCommand();
        } catch (ProductionApplicationException e) {
            throw new ParserException(e.getParserError());
        }

        return root;
    }

    private AstNode.Command parseCommand() throws ProductionApplicationException {
        try {
            AstNode.Imperative imperative = this.parseImperative();
            AstNode.ParameterList parameterList = this.parseParameterList();
            AstNode.OptionList optionList = this.parseOptionList();
            this.eat(TokenType.TERMINAL);
            return new AstNode.Command(imperative, parameterList, optionList);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("command");
            throw e;
        }
    }

    private AstNode.Imperative parseImperative() throws ProductionApplicationException {
        try {
            AstNode.Word word = this.parseWord();
            return new AstNode.Imperative(word);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("imperative");
            throw e;
        }
    }

    private AstNode.ParameterList parseParameterList() throws ProductionApplicationException {
        try {
            ArrayList<AstNode.Parameter> parameters = new ArrayList<>();

            // FOLLOW(parameter_list) = { SLASH, TERMINAL }
            while (!this.check(TokenType.SLASH, TokenType.TERMINAL)) {
                AstNode.Parameter parameter = this.parseParameter();
                parameters.add(parameter);
            }

            return new AstNode.ParameterList(parameters);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("parameter-list");
            throw e;
        }
    }

    private AstNode.Parameter parseParameter() throws ProductionApplicationException {
        try {
            AstNode.Word word = this.parseWord();
            return new AstNode.Parameter(word);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("parameter");
            throw e;
        }
    }

    private AstNode.OptionList parseOptionList() throws ProductionApplicationException {
        try {
            ArrayList<AstNode.Option> options = new ArrayList<>();

            // FOLLOW(option_list) = { TERMINAL }
            // FIRST(option) = { SLASH }
            while (!this.check(TokenType.TERMINAL)) {
                this.eat(TokenType.SLASH);
                AstNode.Option option = this.parseOption();
                options.add(option);
            }

            return new AstNode.OptionList(options);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("option-list");
            throw e;
        }
    }

    private AstNode.Option parseOption() throws ProductionApplicationException {
        try {
            AstNode.OptionName optionName = this.parseOptionName();
            AstNode.OptionValue optionValue;
            if (this.check(TokenType.COLON)) {
                this.advance();
                optionValue = this.parseOptionValue();
            } else {
                optionValue = null;
            }
            return new AstNode.Option(optionName, optionValue);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("option");
            throw e;
        }
    }

    private AstNode.OptionName parseOptionName() throws ProductionApplicationException {
        try {
            AstNode.Word word = this.parseWord();
            return new AstNode.OptionName(word);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("option-name");
            throw e;
        }
    }

    private AstNode.OptionValue parseOptionValue() throws ProductionApplicationException {
        try {
            AstNode.Text text = this.parseText();
            return new AstNode.OptionValue(text);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("option-value");
            throw e;
        }
    }

    private AstNode.Word parseWord() throws ProductionApplicationException {
        try {
            Token token = this.eat(TokenType.WORD);
            return new AstNode.Word(token);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("word");
            throw e;
        }
    }

    private AstNode.Text parseText() throws ProductionApplicationException {
        try {
            Token token = this.eat(TokenType.TEXT, TokenType.WORD);
            return new AstNode.Text(token);
        } catch (ProductionApplicationException e) {
            ParserError error = e.getParserError();
            error.addProductionNonterminal("text");
            throw e;
        }
    }

    private Token eat(TokenType type) throws ProductionApplicationException {
        if (!this.check(type)) {
            ParserError parserError = new ParserError(this.tokenisedCommand.getIngest(), this.peek(), type);
            throw new ProductionApplicationException(parserError);
        }

        return this.advance();
    }

    /**
     * Advances the parser by one token unconditionally, unless it is already at the end.
     *
     * @return Token advanced past.
     */
    private Token advance() {
        Token token = this.peek();

        if (!this.isAtEnd()) {
            this.currentTokenIndex += 1;
        }

        return token;
    }

    /**
     * Indicates if the parser is already at the end of the token stream.
     *
     * @return Boolean corresponding to whether the parser is already at the end of the token stream.
     */
    private boolean isAtEnd() {
        return this.peek().getType() == TokenType.TERMINAL;
    }

    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (this.peek().getType() == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the token currently pointed to by the parser, without advancing past it.
     *
     * @return Token currently being scanned.
     */
    private Token peek() {
        return this.tokenisedCommand.getAtIndex(this.currentTokenIndex);
    }
}
