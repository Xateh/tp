package seedu.address.logic.grammars.command.parser.ast.visitors;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.grammars.command.parser.ast.AstNode;

/**
 * Traverses an AST and builds a formatted string representation.
 */
public class AstPrinter implements AstVisitor<String> {
    /**
     * Returns a pretty-printed string representation of the given AST.
     *
     * @param node Root of the AST.
     * @return Pretty-printed string representation of given AST.
     */
    public String print(AstNode node) {
        return node.accept(this);
    }

    @Override
    public String visitCommand(AstNode.Command node) {
        StringBuilder builder = new StringBuilder("Command");

        List<AstNode> children = new ArrayList<>();
        children.add(node.getImperative());
        children.add(node.getParameterList());
        children.add(node.getOptionList());

        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            String childString = children.get(i).accept(this);
            builder.append("\n").append(addPrefixes(childString, isLast));
        }
        return builder.toString();
    }

    /**
     * Adds the correct tree-like prefixes to each line of a child's output.
     *
     * @param childOutput The fully-rendered string of a child node.
     * @param isLast      True if this is the last child of its parent.
     * @return The prefixed string.
     */
    private String addPrefixes(String childOutput, boolean isLast) {
        StringBuilder builder = new StringBuilder();
        String[] lines = childOutput.split("\n");

        String firstLinePrefix = isLast ? "└─ " : "├─ ";
        builder.append(firstLinePrefix).append(lines[0]);

        String restLinesPrefix = isLast ? "   " : "│  ";
        for (int i = 1; i < lines.length; i++) {
            builder.append("\n").append(restLinesPrefix).append(lines[i]);
        }
        return builder.toString();
    }

    @Override
    public String visitImperative(AstNode.Imperative node) {
        StringBuilder builder = new StringBuilder("Imperative");
        String wordString = node.getWord().accept(this);
        builder.append("\n").append(addPrefixes(wordString, true)); // Always the last/only child
        return builder.toString();
    }

    @Override
    public String visitParameterList(AstNode.ParameterList node) {
        StringBuilder builder = new StringBuilder("ParameterList");
        List<AstNode.Parameter> params = node.getParameters();
        for (int i = 0; i < params.size(); i++) {
            boolean isLast = (i == params.size() - 1);
            String paramString = params.get(i).accept(this);
            builder.append("\n").append(addPrefixes(paramString, isLast));
        }
        return builder.toString();
    }

    @Override
    public String visitParameter(AstNode.Parameter node) {
        StringBuilder builder = new StringBuilder("Parameter");
        String variant = node.getParameterVariant().accept(this);
        builder.append("\n").append(addPrefixes(variant, true));
        return builder.toString();
    }

    @Override
    public String visitNormalParameter(AstNode.NormalParameter node) {
        StringBuilder builder = new StringBuilder("NormalParameter");
        String textString = node.getText().accept(this);
        builder.append("\n").append(addPrefixes(textString, true));
        return builder.toString();
    }

    @Override
    public String visitAdditiveParameter(AstNode.AdditiveParameter node) {
        StringBuilder builder = new StringBuilder("AdditiveParameter");
        String textString = node.getText().accept(this);
        builder.append("\n").append(addPrefixes(textString, true));
        return builder.toString();
    }

    @Override
    public String visitSubtractiveParameter(AstNode.SubtractiveParameter node) {
        StringBuilder builder = new StringBuilder("SubtractiveParameter");
        String textString = node.getText().accept(this);
        builder.append("\n").append(addPrefixes(textString, true));
        return builder.toString();
    }

    @Override
    public String visitOptionList(AstNode.OptionList node) {
        StringBuilder builder = new StringBuilder("OptionList");
        List<AstNode.Option> options = node.getOptions();
        for (int i = 0; i < options.size(); i++) {
            boolean isLast = (i == options.size() - 1);
            String optionString = options.get(i).accept(this);
            builder.append("\n").append(addPrefixes(optionString, isLast));
        }
        return builder.toString();
    }

    @Override
    public String visitOption(AstNode.Option node) {
        StringBuilder builder = new StringBuilder("Option");
        List<AstNode> children = new ArrayList<>();
        children.add(node.getOptionName());
        if (node.getOptionValue() != null) {
            children.add(node.getOptionValue());
        }

        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            String childString = children.get(i).accept(this);
            builder.append("\n").append(addPrefixes(childString, isLast));
        }
        return builder.toString();
    }

    @Override
    public String visitOptionName(AstNode.OptionName node) {
        StringBuilder builder = new StringBuilder("OptionName");
        String wordString = node.getWord().accept(this);
        builder.append("\n").append(addPrefixes(wordString, true));
        return builder.toString();
    }

    @Override
    public String visitOptionValue(AstNode.OptionValue node) {
        StringBuilder builder = new StringBuilder("OptionValue");
        String textString = node.getText().accept(this);
        builder.append("\n").append(addPrefixes(textString, true));
        return builder.toString();
    }

    @Override
    public String visitText(AstNode.Text node) {
        return "Text (\"" + node.getText() + "\")";
    }

    @Override
    public String visitWord(AstNode.Word node) {
        return "Word (\"" + node.getWord() + "\")";
    }
}
