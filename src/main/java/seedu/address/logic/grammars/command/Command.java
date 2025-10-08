package seedu.address.logic.grammars.command;

import java.util.ArrayList;
import java.util.HashMap;

import seedu.address.logic.grammars.command.lexer.CommandLexer;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.lexer.TokenisedCommand;
import seedu.address.logic.grammars.command.parser.CommandParser;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.logic.grammars.command.parser.ast.AstNode;
import seedu.address.logic.grammars.command.parser.ast.visitors.CommandExtractor;

/**
 * Command class that stores the various command tokens in an easily-queryable manner.
 */
public class Command {
    private final String imperative;
    private final String[] parameters;
    private final HashMap<String, String> options;

    private Command(String imperative, String[] parameters, HashMap<String, String> options) {
        this.imperative = imperative;
        this.parameters = parameters;
        this.options = options;
    }

    /**
     * Builder for commands.
     */
    public static class CommandBuilder {
        private String imperative;
        private final ArrayList<String> parameters = new ArrayList<>();
        private final HashMap<String, String> options = new HashMap<>();

        public CommandBuilder() {
        }

        public void setImperative(String imperative) {
            this.imperative = imperative;
        }

        public void addParameter(String parameter) {
            this.parameters.add(parameter);
        }

        public void setOption(String optionName) {
            this.options.put(optionName, null);
        }

        public void setOption(String optionName, String optionValue) {
            this.options.put(optionName, optionValue);
        }

        /**
         * Builds the final command.
         *
         * @return Built command.
         */
        public Command build() {
            String imperative = this.imperative;
            String[] parameters = this.parameters.toArray(String[]::new);
            HashMap<String, String> options = this.options;
            return new Command(imperative, parameters, options);
        }
    }

    /**
     * Parses an input command string into a Command.
     *
     * @param commandString Input command string.
     * @return Command.
     * @throws LexerException  If command string fails to lex.
     * @throws ParserException If command string fails to parse.
     */
    public static Command parse(String commandString) throws LexerException, ParserException {
        TokenisedCommand tokenisedCommand = CommandLexer.lexCommand(commandString);
        AstNode.Command rootCommandNode = CommandParser.parseCommand(tokenisedCommand);
        return new CommandExtractor().extract(rootCommandNode);
    }

    public String getImperative() {
        return this.imperative;
    }

    public String getParameter(int index) {
        return this.parameters[index];
    }

    public String[] getAllParameters() {
        return this.parameters;
    }

    /**
     * Returns the value associated with the key, if provided as an option.
     *
     * @param key The option key to look for.
     * @return The value associated with the given key, or returns null if the key was not specified.
     */
    public String getOptionValue(String key) {
        return this.options.get(key);
    }

    public boolean hasOption(String key) {
        return this.options.containsKey(key);
    }
}
