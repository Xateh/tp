package seedu.address.logic.grammars.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class BareCommand {
    private final String imperative;
    private final String[] parameters;
    private final Map<String, String> options;

    private BareCommand(String imperative, String[] parameters, Map<String, String> options) {
        this.imperative = imperative;
        this.parameters = parameters;
        this.options = options;
    }

    /**
     * Builder for commands.
     */
    public static class BareCommandBuilder {
        private String imperative;
        private final ArrayList<String> parameters = new ArrayList<>();
        private final Map<String, String> options = new LinkedHashMap<>();

        public BareCommandBuilder() {
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
        public BareCommand build() {
            String imperative = this.imperative;
            String[] parameters = this.parameters.toArray(String[]::new);
            // Allows nulls (for flag-style options), but prevents external mutation
            Map<String, String> options =
                Collections.unmodifiableMap(new LinkedHashMap<>(this.options));
            return new BareCommand(imperative, parameters, options);
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
    public static BareCommand parse(String commandString) throws LexerException, ParserException {
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

    /**
     * Returns whether the key was defined as an option; typically used for boolean flags.
     *
     * @param key The option key to look for.
     * @return True if the option key was specified/defined, else returns false.
     */
    public boolean hasOption(String key) {
        return this.options.containsKey(key);
    }

    /*
     * Returns a read-only view of all option key→value pairs.
     */
    public Map<String, String> getAllOptions() {
        return this.options;
    }
}
