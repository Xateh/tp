package seedu.address.logic.grammars.command;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import seedu.address.logic.grammars.command.lexer.CommandLexer;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.lexer.TokenisedCommand;
import seedu.address.logic.grammars.command.parser.CommandParser;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.logic.grammars.command.parser.ast.AstNode;
import seedu.address.logic.grammars.command.parser.ast.visitors.CommandExtractor;

/**
 * Command class that stores the various command tokens in an easily-queryable manner. Immutable class.
 */
public class BareCommand {
    private final String imperative;
    private final List<Parameter> parameters;
    private final Map<String, List<String>> options;

    private BareCommand(String imperative, List<Parameter> parameters, Map<String, List<String>> options) {
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
        private final HashMap<String, ArrayList<String>> options = new HashMap<>();

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
         * @param optionKey Key of option.
         * @return This builder with the set option.
         */
        public BareCommandBuilder setOption(String optionKey) {
            if (!this.options.containsKey(optionKey)) {
                this.options.put(optionKey, new ArrayList<>());
            }
            return this;
        }

        /**
         * Sets an option in the command to be built. Typically used for key-value options.
         *
         * @param optionKey   Key of option.
         * @param optionValue Value of option.
         * @return This builder with the set option.
         */
        public BareCommandBuilder setOption(String optionKey, String optionValue) {
            if (!this.options.containsKey(optionKey)) {
                this.options.put(optionKey, new ArrayList<>());
            }
            this.options.get(optionKey).add(optionValue);
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
            Map<String, List<String>> options = new HashMap<>();
            for (Map.Entry<String, ArrayList<String>> option : this.options.entrySet()) {
                options.put(option.getKey(), option.getValue().stream().toList());
            }

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
     * Returns an {@code Optional} containing the value associated with the key, if provided as an option. If the option
     * was specified multiple times, this returns the first specified option value. For this to return a nonempty
     * Optional, the option must have been specified at least once with a value.
     *
     * @param key The option key to look for.
     * @return An {@code Optional} containing the first value associated with the given key if it exists.
     */
    public Optional<String> getOptionValue(String key) {
        requireNonNull(key);

        List<String> values = this.options.get(key);
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(values.get(0));
    }

    /**
     * Returns an {@code Optional} containing all values associated with the key, if provided as an option. Callers must
     * be prepared to accept any number (including 0) of values, if the option was defined. For this to return a
     * nonempty Optional, the option must have been specified at least once, regardless of the number of values
     * specified.
     *
     * @param key The option key to look for.
     * @return An {@code Optional} containing a {@code List} of all the values associated with the given key.
     */
    public Optional<List<String>> getOptionAllValues(String key) {
        requireNonNull(key);

        List<String> values = this.options.get(key);
        if (values == null) {
            return Optional.empty();
        }

        return Optional.of(values);
    }

    /**
     * Returns whether the key was defined as an option; typically used for boolean flags. For this to return true, the
     * option must have been specified at least once, regardless of the number of values specified.
     *
     * @param key The option key to look for.
     * @return True if the option key was specified/defined, else returns false.
     */
    public boolean hasOption(String key) {
        requireNonNull(key);
        return this.options.containsKey(key);
    }

    /**
     * Returns the multiplicity of the option. The multiplicity of an option is the number of values specified together
     * with the option, or -1 if it was never specified as both a boolean and a key-value option.
     *
     * @param key The option key to look for.
     * @return The multiplicity of the option.
     */
    public int getOptionMultiplicity(String key) {
        requireNonNull(key);

        List<String> values = this.options.get(key);

        if (values == null) {
            return -1;
        } else {
            return values.size();
        }
    }

    /*
     * Returns a read-only view of all option key→value pairs.
     */
    public Map<String, List<String>> getAllOptions() {
        return this.options;
    }
}
