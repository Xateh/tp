package seedu.address.logic.grammars.command.parser.ast.visitors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.parser.ast.AstNode;

/**
 * Extractor for commands from a complete AST.
 */
public class CommandExtractor implements AstVisitor<String> {
    private final BareCommand.BareCommandBuilder bareCommandBuilder = new BareCommand.BareCommandBuilder();

    /**
     * Returns a Command populated with the given AST's items.
     *
     * @param node Root of the AST to populate Command with.
     * @return Populated Command.
     */
    public BareCommand extract(AstNode node) {
        node.accept(this);
        return this.bareCommandBuilder.build();
    }

    @Override
    public String visitCommand(AstNode.Command node) {
        node.getImperative().accept(this);
        node.getParameterList().accept(this);
        node.getOptionList().accept(this);
        return null;
    }

    @Override
    public String visitImperative(AstNode.Imperative node) {
        this.bareCommandBuilder.setImperative(node.getWord().accept(this));
        return null;
    }

    @Override
    public String visitParameterList(AstNode.ParameterList node) {
        for (AstNode.Parameter parameter : node.getParameters()) {
            parameter.accept(this);
        }
        return null;
    }

    @Override
    public String visitParameter(AstNode.Parameter node) {
        node.getParameterVariant().accept(this);
        return null;
    }

    @Override
    public String visitNormalParameter(AstNode.NormalParameter node) {
        this.bareCommandBuilder.addParameter(ParameterKind.NORMAL, node.getText().accept(this));
        return null;
    }

    @Override
    public String visitAdditiveParameter(AstNode.AdditiveParameter node) {
        this.bareCommandBuilder.addParameter(ParameterKind.ADDITIVE, node.getText().accept(this));
        return null;
    }

    @Override
    public String visitSubtractiveParameter(AstNode.SubtractiveParameter node) {
        this.bareCommandBuilder.addParameter(ParameterKind.SUBTRACTIVE, node.getText().accept(this));
        return null;
    }

    @Override
    public String visitOptionList(AstNode.OptionList node) {
        for (AstNode.Option option : node.getOptions()) {
            option.accept(this);
        }
        return null;
    }

    @Override
    public String visitOption(AstNode.Option node) {
        if (node.hasOptionValue()) {
            node.getOptionName().accept(this);
            this.bareCommandBuilder.setOption(
                    visitOptionName(node.getOptionName()),
                    visitOptionValue((node.getOptionValue()))
            );
        } else {
            this.bareCommandBuilder.setOption(visitOptionName(node.getOptionName()));
        }

        return null;
    }

    @Override
    public String visitOptionName(AstNode.OptionName node) {
        return visitWord(node.getWord());
    }

    @Override
    public String visitOptionValue(AstNode.OptionValue node) {
        return visitText(node.getText());
    }

    @Override
    public String visitText(AstNode.Text node) {
        return node.getText();
    }

    @Override
    public String visitWord(AstNode.Word node) {
        return node.getWord();
    }
}
