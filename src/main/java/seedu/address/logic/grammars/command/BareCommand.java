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
    private final String[] parameters;
    private final Map<String, List<String>> options;

    private BareCommand(String imperative, String[] parameters, Map<String, List<String>> options) {
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
        private final HashMap<String, ArrayList<String>> options = new HashMap<>();

        public BareCommandBuilder() {
        }

        public void setImperative(String imperative) {
            this.imperative = imperative;
        }

        public void addParameter(String parameter) {
            this.parameters.add(parameter);
        }

        public void setOption(String optionKey) {
            if (!this.options.containsKey(optionKey)) {
                this.options.put(optionKey, new ArrayList<>());
            }
        }

        public void setOption(String optionKey, String optionValue) {
            if (!this.options.containsKey(optionKey)) {
                this.options.put(optionKey, new ArrayList<>());
            }

            this.options.get(optionKey).add(optionValue);
        }

        /**
         * Builds the final command.
         *
         * @return Built command.
         */
        public BareCommand build() {
            String imperative = this.imperative;
            String[] parameters = this.parameters.toArray(String[]::new);
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

    public String getParameter(int index) {
        return this.parameters[index];
    }

    public String[] getAllParameters() {
        return this.parameters;
    }

    /**
     * Returns an {@code Optional} containing the value associated with the key, if provided as an option. If the option
     * was specified multiple times, this returns the first specified option value.
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
     * Returns an {@code Optional} containing all values associated with the key, if provided as an option.
     *
     * @param key The option key to look for.
     * @return An {@code Optional} containing a {@code List} of all the values associated with the given key, if they
     *         exist.
     */
    public Optional<List<String>> getOptionAllValues(String key) {
        requireNonNull(key);

        List<String> values = this.options.get(key);
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(values);
    }

    /**
     * Returns whether the key was defined as an option; typically used for boolean flags.
     *
     * @param key The option key to look for.
     * @return True if the option key was specified/defined, else returns false.
     */
    public boolean hasOption(String key) {
        requireNonNull(key);
        return this.options.containsKey(key);
    }

    /*
     * Returns a read-only view of all option key→value pairs.
     */
    public Map<String, List<String>> getAllOptions() {
        return this.options;
    }
}
