package seedu.address.logic.grammars.command.parser.ast.visitors;

import seedu.address.logic.grammars.command.parser.ast.AstNode;

/**
 * Visitor pattern interface for AST traversal and operations.
 *
 * @param <R> Return type of the visitor after it visits each node.
 */
public interface AstVisitor<R> {
    /**
     * Visits a Command AST node.
     *
     * @param node Command AST node.
     * @return Value produced after visiting.
     */
    R visitCommand(AstNode.Command node);

    /**
     * Visits an Imperative AST node.
     *
     * @param node Imperative AST node.
     * @return Value produced after visiting.
     */
    R visitImperative(AstNode.Imperative node);

    /**
     * Visits a ParameterList AST node.
     *
     * @param node ParameterList AST node.
     * @return Value produced after visiting.
     */
    R visitParameterList(AstNode.ParameterList node);

    /**
     * Visits a Parameter AST node.
     *
     * @param node Parameter AST node.
     * @return Value produced after visiting.
     */
    R visitParameter(AstNode.Parameter node);

    /**
     * Visits a NormalParameter AST node.
     *
     * @param node NormalParameter AST node.
     * @return Value produced after visiting.
     */
    R visitNormalParameter(AstNode.NormalParameter node);

    /**
     * Visits an AdditiveParameter AST node.
     *
     * @param node AdditiveParameter AST node.
     * @return Value produced after visiting.
     */
    R visitAdditiveParameter(AstNode.AdditiveParameter node);

    /**
     * Visits a SubtractiveParameter AST node.
     *
     * @param node SubtractiveParameter AST node.
     * @return Value produced after visiting.
     */
    R visitSubtractiveParameter(AstNode.SubtractiveParameter node);

    /**
     * Visits an OptionList AST node.
     *
     * @param node OptionList AST node.
     * @return Value produced after visiting.
     */
    R visitOptionList(AstNode.OptionList node);

    /**
     * Visits an Option AST node.
     *
     * @param node Option AST node.
     * @return Value produced after visiting.
     */
    R visitOption(AstNode.Option node);

    /**
     * Visits an OptionName AST node.
     *
     * @param node OptionName AST node.
     * @return Value produced after visiting.
     */
    R visitOptionName(AstNode.OptionName node);

    /**
     * Visits a OptionValue List AST node.
     *
     * @param node OptionValue AST node.
     * @return Value produced after visiting.
     */
    R visitOptionValue(AstNode.OptionValue node);

    /**
     * Visits a Text AST node.
     *
     * @param node Text AST node.
     * @return Value produced after visiting.
     */
    R visitText(AstNode.Text node);

    /**
     * Visits a Word AST node.
     *
     * @param node Word AST node.
     * @return Value produced after visiting.
     */
    R visitWord(AstNode.Word node);
}
