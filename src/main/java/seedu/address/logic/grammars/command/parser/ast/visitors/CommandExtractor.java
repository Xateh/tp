package seedu.address.logic.grammars.command.parser.ast.visitors;

import seedu.address.logic.grammars.command.Command;
import seedu.address.logic.grammars.command.parser.ast.AstNode;

/**
 * Extractor for commands from a complete AST.
 */
public class CommandExtractor implements AstVisitor<String> {
    private final Command.CommandBuilder commandBuilder = new Command.CommandBuilder();

    /**
     * Returns a Command populated with the given AST's items.
     *
     * @param node Root of the AST to populate Command with.
     * @return Populated Command.
     */
    public Command extract(AstNode node) {
        node.accept(this);
        return this.commandBuilder.build();
    }

    @Override
    public String visitCommand(AstNode.Command node) {
        visitImperative(node.getImperative());
        visitParameterList(node.getParameterList());
        visitOptionList(node.getOptionList());
        return null;
    }

    @Override
    public String visitImperative(AstNode.Imperative node) {
        this.commandBuilder.setImperative(visitWord(node.getWord()));
        return null;
    }

    @Override
    public String visitParameterList(AstNode.ParameterList node) {
        for (AstNode.Parameter parameter : node.getParameters()) {
            visitParameter(parameter);
        }
        return null;
    }

    @Override
    public String visitParameter(AstNode.Parameter node) {
        this.commandBuilder.addParameter(visitText(node.getText()));
        return null;
    }

    @Override
    public String visitOptionList(AstNode.OptionList node) {
        for (AstNode.Option option : node.getOptions()) {
            visitOption(option);
        }
        return null;
    }

    @Override
    public String visitOption(AstNode.Option node) {
        if (node.hasOptionValue()) {
            this.commandBuilder.setOption(visitOptionName(node.getOptionName()),
                    visitOptionValue((node.getOptionValue())));
        } else {
            this.commandBuilder.setOption(visitOptionName(node.getOptionName()));
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
