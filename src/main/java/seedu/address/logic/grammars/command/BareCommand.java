package seedu.address.logic.grammars.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final List<Parameter> parameters;
    private final Map<String, String> options;

    private BareCommand(String imperative, List<Parameter> parameters, Map<String, String> options) {
        this.imperative = imperative;
        this.parameters = parameters;
        this.options = options;
    }

    /**
     * Parameter class that stores the parameter kind and value.
     */
    public static class Parameter {
        private final ParameterKind kind;
        private final String value;

        /**
         * Enumeration of all possible parameter kinds.
         */
        public enum ParameterKind {
            NORMAL, ADDITIVE, SUBTRACTIVE
        }

        /**
         * Constructs a new {@code Parameter}.
         *
         * @param kind  Kind of parameter.
         * @param value Value of parameter.
         */
        public Parameter(ParameterKind kind, String value) {
            this.kind = kind;
            this.value = value;
        }

        public ParameterKind getKind() {
            return kind;
        }

        public String getValue() {
            return value;
        }

        public boolean isNormal() {
            return this.kind == ParameterKind.NORMAL;
        }

        public boolean isAdditive() {
            return this.kind == ParameterKind.ADDITIVE;
        }

        public boolean isSubtractive() {
            return this.kind == ParameterKind.SUBTRACTIVE;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (other instanceof Parameter param) {
                return this.value.equals(param.value) && this.kind.equals(param.kind);
            }

            return false;
        }
    }

    /**
     * Builder for commands.
     */
    public static class BareCommandBuilder {
        private static final String MESSAGE_UNDECLARED_IMPERATIVE = "Imperative not declared; build illegal.";

        private String imperative;
        private final ArrayList<Parameter> parameters = new ArrayList<>();
        private final Map<String, String> options = new LinkedHashMap<>();

        public BareCommandBuilder() {
        }

        /**
         * Sets the imperative of the command to be built.
         *
         * @param imperative Imperative to set.
         * @return This builder with the set imperative.
         */
        public BareCommandBuilder setImperative(String imperative) {
            this.imperative = imperative;
            return this;
        }

        /**
         * Adds a parameter to the command to be built.
         *
         * @param parameterKind  Kind of parameter.
         * @param parameterValue Value of parameter.
         * @return This builder with the added parameter.
         */
        public BareCommandBuilder addParameter(Parameter.ParameterKind parameterKind, String parameterValue) {
            this.parameters.add(new Parameter(parameterKind, parameterValue));
            return this;
        }

        /**
         * Adds a normal parameter to the command to be built.
         *
         * @param parameterValue Value of normal parameter.
         * @return This builder with the added normal parameter.
         */
        public BareCommandBuilder addParameter(String parameterValue) {
            this.addParameter(Parameter.ParameterKind.NORMAL, parameterValue);
            return this;
        }

        /**
         * Sets an option in the command to be built. Typically used for boolean options.
         *
         * @param optionName Name of option.
         * @return This builder with the set option.
         */
        public BareCommandBuilder setOption(String optionName) {
            this.options.put(optionName, null);
            return this;
        }

        /**
         * Sets an option in the command to be built. Typically used for key-value options.
         *
         * @param optionName  Name of option.
         * @param optionValue Value of option.
         * @return This builder with the set option.
         */
        public BareCommandBuilder setOption(String optionName, String optionValue) {
            this.options.put(optionName, optionValue);
            return this;
        }

        /**
         * Builds the final command.
         *
         * @return Built command.
         */
        public BareCommand build() {
            if (this.imperative == null) {
                throw new IllegalStateException(MESSAGE_UNDECLARED_IMPERATIVE);
            }

            String imperative = this.imperative;
            List<Parameter> parameters = this.parameters.stream().toList();
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

    public Parameter getParameter(int index) {
        return this.parameters.get(index);
    }

    public List<Parameter> getAllParameters() {
        return this.parameters;
    }

    public int parameterCount() {
        return this.parameters.size();
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
