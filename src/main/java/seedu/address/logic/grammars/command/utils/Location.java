package seedu.address.logic.grammars.command.utils;

/**
 * Simple record storing the location of a token or AST node (contiguous indices [start, end)) in some ingested string.
 */
public record Location(int start, int end) {
}
