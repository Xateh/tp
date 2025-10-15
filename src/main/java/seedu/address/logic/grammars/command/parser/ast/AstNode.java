package seedu.address.logic.grammars.command.parser.ast;

import java.util.ArrayList;

import seedu.address.logic.grammars.command.lexer.Token;
import seedu.address.logic.grammars.command.parser.ast.visitors.AstVisitor;

/**
 * Abstract class for all AST nodes.
 */
public abstract class AstNode {
    public abstract <R> R accept(AstVisitor<R> visitor);

    /**
     * Command AST node.
     */
    public static class Command extends AstNode {
        private final Imperative imperative;
        private final ParameterList parameterList;
        private final OptionList optionList;

        /**
         * Constructs a new Command node.
         *
         * @param imperative    Imperative node.
         * @param parameterList ParameterList node.
         * @param optionList    OptionList node.
         */
        public Command(Imperative imperative, ParameterList parameterList, OptionList optionList) {
            this.imperative = imperative;
            this.parameterList = parameterList;
            this.optionList = optionList;
        }

        public Imperative getImperative() {
            return imperative;
        }

        public ParameterList getParameterList() {
            return parameterList;
        }

        public OptionList getOptionList() {
            return optionList;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitCommand(this);
        }
    }

    /**
     * Imperative AST node.
     */
    public static class Imperative extends AstNode {
        private final Word word;

        /**
         * Constructs a new Imperative node.
         *
         * @param word Word node.
         */
        public Imperative(Word word) {
            this.word = word;
        }

        public Word getWord() {
            return word;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitImperative(this);
        }
    }

    /**
     * Parameter List AST node.
     */
    public static class ParameterList extends AstNode {
        private final ArrayList<Parameter> parameters;

        /**
         * Constructs a new ParameterList node.
         *
         * @param parameters ArrayList of Parameter, an ordered collection of parameters.
         */
        public ParameterList(ArrayList<Parameter> parameters) {
            this.parameters = parameters;
        }

        public ArrayList<Parameter> getParameters() {
            return parameters;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitParameterList(this);
        }
    }

    /**
     * Parameter AST node.
     */
    public static class Parameter extends AstNode {
        private final Text text;

        /**
         * Constructs a new Parameter node.
         *
         * @param text Text node.
         */
        public Parameter(Text text) {
            this.text = text;
        }

        public Text getText() {
            return text;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitParameter(this);
        }
    }

    /**
     * OptionList AST node.
     */
    public static class OptionList extends AstNode {
        private final ArrayList<Option> options;

        /**
         * Constructs a new OptionList node.
         *
         * @param options ArrayList of Option, an ordered collection of options.
         */
        public OptionList(ArrayList<Option> options) {
            this.options = options;
        }

        public ArrayList<Option> getOptions() {
            return options;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitOptionList(this);
        }
    }

    /**
     * Option AST node.
     */
    public static class Option extends AstNode {
        private final OptionName optionName;
        private final OptionValue optionValue;

        /**
         * Constructs a new Option node.
         *
         * @param optionName  OptionName node.
         * @param optionValue OptionValue node.
         */
        public Option(OptionName optionName, OptionValue optionValue) {
            this.optionName = optionName;
            this.optionValue = optionValue;
        }

        public OptionName getOptionName() {
            return optionName;
        }

        public OptionValue getOptionValue() {
            return optionValue;
        }

        public boolean hasOptionValue() {
            return optionValue != null;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitOption(this);
        }
    }

    /**
     * Option Name AST node.
     */
    public static class OptionName extends AstNode {
        private final Text text;

        /**
         * Constructs a new OptionName node.
         *
         * @param word Word node.
         */
        public OptionName(Text text) {
            this.text = text;
        }

        public Text getText() {
            return text;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitOptionName(this);
        }
    }

    /**
     * Option Value AST node.
     */
    public static class OptionValue extends AstNode {
        private final Text text;

        /**
         * Constructs a new OptionValue node.
         *
         * @param text Text node.
         */
        public OptionValue(Text text) {
            this.text = text;
        }

        public Text getText() {
            return text;
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitOptionValue(this);
        }
    }

    /**
     * Text AST node.
     */
    public static class Text extends AstNode {
        private final Token token;

        /**
         * Constructs a new Text node.
         *
         * @param token Token from lexer accepted by this node (one of WORD, TEXT).
         */
        public Text(Token token) {
            this.token = token;
        }

        public String getText() {
            return this.token.getLiteral();
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitText(this);
        }
    }

    /**
     * Word AST node.
     */
    public static class Word extends AstNode {
        private final Token token;

        /**
         * Constructs a new Word node.
         *
         * @param token Token from lexer accepted by this node (one of WORD).
         */
        public Word(Token token) {
            this.token = token;
        }

        public String getWord() {
            return this.token.getLiteral();
        }

        @Override
        public <R> R accept(AstVisitor<R> visitor) {
            return visitor.visitWord(this);
        }
    }
}
