package seedu.address.logic.grammars.command.parser;

import static seedu.address.logic.grammars.command.utils.Utils.makeVisualDelimiter;
import static seedu.address.logic.grammars.command.utils.Utils.reverseArrayList;

import java.util.ArrayList;
import java.util.Arrays;

import seedu.address.logic.grammars.command.lexer.Token;
import seedu.address.logic.grammars.command.lexer.TokenType;
import seedu.address.logic.grammars.command.utils.Location;

class ParserError {
    private final ArrayList<String> productionNonterminalStack = new ArrayList<>();
    private final String ingest;
    private final Token offendingToken;
    private final TokenType[] expectedTokenTypes;

    ParserError(String ingest, Token offendingToken, TokenType... expectedTokenTypes) {
        this.ingest = ingest;
        this.offendingToken = offendingToken;
        this.expectedTokenTypes = expectedTokenTypes;
    }

    void addProductionNonterminal(String productionRule) {
        this.productionNonterminalStack.add(productionRule);
    }

    @Override
    public String toString() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("Error occurred during parsing.");

        String productionRuleStack = String.join(" > ", reverseArrayList(this.productionNonterminalStack));
        lines.add(String.format("Error occurred while applying production rules: %s", productionRuleStack));

        lines.add(String.format("Expected token types: one of {%s}",
                String.join(",", Arrays.stream(this.expectedTokenTypes)
                        .map(Enum::toString).toArray(String[]::new))));

        lines.add(String.format("Found: %s", offendingToken.getType().getDescription()));

        return String.join("\n", lines);
    }

    public String getLogString() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("Error occurred during parsing.");

        lines.add(this.ingest);

        Location location = this.offendingToken.getLocation();
        lines.add(makeVisualDelimiter(location.start(), location.end()));

        String productionRuleStack = String.join(" > ", reverseArrayList(this.productionNonterminalStack));
        lines.add(String.format("Error occurred while applying production rules: %s", productionRuleStack));

        lines.add(String.format("Expected token types: one of {%s}",
                String.join(",", Arrays.stream(this.expectedTokenTypes)
                        .map(Enum::toString).toArray(String[]::new))));

        lines.add(String.format("Found: %s", offendingToken.getType().getDescription()));

        return String.join("\n", lines);
    }
}
