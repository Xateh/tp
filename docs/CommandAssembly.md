# Command Assembly

This subguide details the full command assembly strategy used by AssetSphere.

### Lexer Architecture

The `CommandLexer` is implemented a single finite state machine. It ingests an input string, and scans characters sequentially. At any point, it maintains a window representing the current token it is scanning.

The lexer operates through a single forward pass over the input string, maintaining two position indices:

- start: Marks the beginning of the current lexeme being scanned
- current: Marks the position of the next character to examine

**Core Methods**

`peek()`: Returns the character at the current position without advancing. This allows the lexer to examine what's ahead before committing to a state transition.

`advance()`: Returns the character at current and increments the position by one. This consumes the character and moves the lexer forward in the input stream.

`munch`: A family of methods (munchWord(), munchText()) that consume multiple consecutive characters matching a specific pattern. "Munching" refers to the greedy consumption of characters: the lexer keeps calling advance() as long as characters match the expected pattern, maximizing the length of each token.

**Usage**

The lexer is accessed through the static factory method:

```java
TokenisedCommand result = CommandLexer.lexCommand(inputString);
```

This returns a TokenisedCommand object containing the original input and the complete token sequence, ready for parsing.

**Theoretical Foundation**

The lexer is a lexical analyzer that tokenizes command strings according to a simple regular grammar. It transforms raw input strings into a sequence of tokens that can be used by a parser downstream.

This lexer implements a single finite state machine (FSM) that recognizes a Level 3 regular grammar. The grammar defines five token types: `WORD`, `TEXT`, `SLASH`, `COLON`, and `TERMINAL`.

As a finite state machine, the lexer maintains a current state (represented by its position in the input) and transitions between states based on the characters it encounters. The FSM operates deterministically: for any given character, the lexer knows exactly which state to transition to and which token type to emit.

The regular nature of the grammar means the lexer requires no lookahead beyond a single character and no backtracking, making it efficient and straightforward to implement.

### Parser Architecture

The `CommandParser` is an recursive descent parser that transforms a stream of tokens produced by the `CommandLexer` into an abstract syntax tree (AST). Each nonterminal (defined in the grammar by production rules of the form `A → α`) in the grammar corresponds to a parsing method. These methods call each other recursively, mirroring the grammar's hierarchical structure.

**Core Methods**

`peek()`: Returns the token at the current position without advancing. This provides the one-token lookahead that characterizes LL(1) parsing, allowing the parser to decide which production rule to apply.

`advance()`: Returns the current token and increments the position by one. This consumes the token and moves the parser forward in the token stream.

`eat(TokenType... types)`: The fundamental consuming operation that combines checking and advancing. It verifies that the current token matches one of the expected types, then consumes it via advance(). If the token doesn't match, it throws an exception. This method enforces the grammar rules at each step.

`check(TokenType... types)`: A non-consuming lookahead operation that returns true if the current token matches any of the specified types. This is used in decision points to determine which production to apply or whether to continue looping.

**Error Handling and Recovery**

The parser uses exception-based error propagation:

- **`ProductionApplicationException`**: Thrown when `eat()` encounters an unexpected token. The exception carries a `ParserError` containing:
    - The original input string
    - The offending token
    - The expected token types
- **Error Enrichment**: As exceptions propagate up the call stack, each parsing method catches them, adds its non-terminal name to the error (building a derivation trace), and re-throws. This creates a complete picture of which production rule failed and where in the grammar hierarchy the error occurred.
- **`ParserException`**: The final exception type thrown to the caller, wrapping the enriched parser error with full diagnostic information.

**Usage**

The parser is accessed through the static factory method:

```java
AstNode.Command ast = CommandParser.parseCommand(tokenisedCommand);
```

This returns the root of the AST, which can then be traversed for semantic analysis, validation, and execution.

**Theoretical Foundation**

This parser implements an LL(1) parsing strategy, where:

- _LL_ stands for "Left-to-right, Leftmost derivation"
- _(1)_ indicates one token of lookahead

The parser recognises a Level 2 context-free grammar that defines the hierarchical structure of commands. Unlike the lexer's regular grammar, this context-free grammar can express nested and recursive structures, making it more powerful and suitable for parsing syntactic constructs.

All production rules in a context-free grammar are of the form: `A → α`.

The LL(1) property means the parser can determine which production rule to apply by examining only the current token, without backtracking. This is possible because the grammar is carefully designed so that each production has a distinct FIRST set (tokens that can begin that production) and FOLLOW set (tokens that can legally appear after that production).

### AST Processing

**Overview**

The `AstVisitor<R>` interface defines the contract for implementing the **Visitor pattern** on command Abstract Syntax Trees. It enables traversal and processing of AST nodes without modifying the node classes themselves, providing a clean separation between the tree structure and the operations performed on it.

This interface is generic over the return type produced by visiting each node. This generic parameter provides flexibility for different visitor use cases.

**Visitor Pattern**

The Visitor pattern solves a common problem in compiler design: how to perform different operations on an AST (extraction, validation, transformation, optimization) without cluttering the node classes with operation-specific code.

This interface declares one `visit` method for each AST node type in the command grammar. When a visitor traverses the tree, each node calls the appropriate `visit` method on the visitor, passing itself as an argument. This technique, called **double dispatch**, allows the visitor to execute type-specific logic for each node.

**Visitor Methods**

Each method corresponds to one AST node type and follows the naming convention `visit[NodeType]`.

The complete set of methods ensures visitors can handle every node type in the grammar, providing exhaustive coverage of the AST structure.

**Implementing Custom Visitors**

To create a custom AST processor, implement the `AstVisitor<R>` interface with your desired return type:

```java
public class MyCustomVisitor implements AstVisitor<MyResultType> {
    @Override
    public MyResultType visitCommand(AstNode.Command node) {
        // Process command node
        // Typically calls accept() on child nodes to traverse deeper
        MyResultType imperativeResult = node.getImperative().accept(this);
        MyResultType paramsResult = node.getParameterList().accept(this);
        MyResultType optionsResult = node.getOptionList().accept(this);

        // Combine results and return
        return combineResults(imperativeResult, paramsResult, optionsResult);
    }

    @Override
    public MyResultType visitWord(AstNode.Word node) {
        // Process leaf node
        // Extract token data and convert to result type
        String tokenValue = node.getToken().getLiteral();
        return processWord(tokenValue);
    }

    // Implement remaining visit methods...
}
```

**Traversal Pattern**

Visitors typically follow a **recursive descent** pattern:

1. Each `visit` method processes the current node
2. For non-terminal nodes, the visitor calls `accept(this)` on child nodes to continue traversal
3. For terminal nodes (Word, Text), the visitor extracts token data directly
4. Results from child visits are combined to produce the parent's result

**Provided Visitors**
- Extraction (`CommandExtractor`): Builds a `Command` object by accumulating data as the visitor descends through the tree.
- Pretty-Printing (`AstPrinter`): Returns a formatted `String` visualisation of a tree.

### Lexer/Parser Interface: Commands

**Overview**

The `BareCommand` class is a high-level facade that provides a simple, queryable interface for working with parsed commands. It serves as the primary entry point for users of the lexer/parser package, abstracting away the complexities of tokenisation, parsing, and AST traversal behind a clean, intuitive API.

**Design Philosophy**

This class embodies the **Facade pattern**, hiding the multi-stage processing pipeline (lexing → parsing → AST extraction) behind a single static factory method. Implementors do not need to understand tokens, ASTs, or visitor patterns, but should just call `BareCommand.parse()` and receive a structured representation of their command string.

The `BareCommand` class represents the **semantic model** of a command, distilled from the syntactic AST into three fundamental components:

- **Imperative**: The command verb (e.g., `add`, `delete`, `edit`)
- **Parameters**: Ordered positional arguments
- **Options**: Named flags with optional values (e.g., `/email:john@example.com` or `/force`)

#### Architecture

**Processing Pipeline**

The `parse()` method orchestrates a three-stage transformation:

1. **Lexical Analysis** (`CommandLexer.lexCommand()`): Converts the raw command string into a stream of tokens, recognising the basic lexical elements (words, text, slashes, colons).

2. **Syntactic Analysis** (`CommandParser.parseCommand()`): Transforms the token stream into an Abstract Syntax Tree that captures the hierarchical grammatical structure of the command.

3. **Semantic Extraction** (`CommandExtractor.extract()`): Traverses the AST using the visitor pattern to extract semantic information, populating a `CommandBuilder` with the command's meaningful components.

This pipeline separates concerns cleanly: lexing handles character-level details, parsing handles grammar structure, and extraction handles meaning. The `BareCommand` class receives only the final, distilled result.

**Data Model**

The internal representation uses three data structures optimised for different access patterns:

- **`imperative`** (`String`): A single command verb, accessed via `getImperative()`
- **`parameters`** (`String[]`): An ordered array for positional arguments, supporting indexed access via `getParameter(int)` or bulk retrieval via `getAllParameters()`
- **`options`** (`HashMap<String, String>`): A key-value map for named options, enabling fast lookup via `getOptionValue(String)` and existence checks via `hasOption(String)`

This design reflects typical command usage patterns: imperatives are always present and unique, parameters are order-dependent, and options are queried by name.

**Builder Pattern**

The nested `BareCommandBuilder` class implements the **Builder pattern** to construct `Command` instances incrementally. This is particularly useful for the `CommandExtractor` visitor, which discovers command components as it traverses the AST:

- **`setImperative(String)`**: Sets the command verb (called once)
- **`addParameter(String)`**: Appends a positional parameter (called zero or more times, preserving order)
- **`setOption(String)`** and **setOption(String, String)**: Adds flag-style or value-bearing options (called zero or more times)
- **`build()`**: Produces the immutable `BareCommand` instance

The builder accumulates components in mutable collections (`ArrayList` for parameters, `HashMap` for options), then converts them to the appropriate final representations during `build()`. This separation allows flexible construction while maintaining immutability in the final product.

#### Usage Patterns

**Basic Parsing**

The primary interface is the static factory method:

```java
BareCommand cmd = BareCommand.parse("add John Doe /email:john@example.com /force");
```

This single call handles all processing stages and returns a fully-populated `BareCommand` object.

**Querying BareCommands**

Once parsed, commands support intuitive queries:

```java
String verb = cmd.getImperative();           // "add"
String firstName = cmd.getParameter(0);      // "John"
String[] allParams = cmd.getAllParameters(); // ["John", "Doe"]
String email = cmd.getOptionValue("email");  // "john@example.com"
boolean forced = cmd.hasOption("force");     // true
```

The API distinguishes between:
- **`getOptionValue()`**: Returns the value (or null for flags/missing options)
- **`hasOption()`**: Tests for option presence (useful for boolean flags)

**Error Handling**

The `parse()` method declares two checked exceptions corresponding to the two stages where errors can occur:

- **`LexerException`**: Thrown when the input contains invalid characters or malformed tokens (e.g., unterminated strings)
- **`ParserException`**: Thrown when the token sequence doesn't conform to the command grammar (e.g., missing imperatives, unexpected token order)

Users should handle both exceptions to provide appropriate error feedback:

```java
try {
    BareCommand cmd = BareCommand.parse(userInput);
    // process command
} catch (LexerException e) {
    // handle tokenization errors
} catch (ParserException e) {
    // handle grammar errors
}
```

### Command Grammar

**Lexer Tokens**

The following regular grammar is recognised by the lexer.

```
WORD   ::= [A-z0-9]+
TEXT   ::= "[^"/:]*"
SLASH  ::= /
COLON  ::= :
```

The `TERMINAL` token denotes the end of input.

**Command Grammar**

The following command grammar is recognised by the parser, in EBNF notation.

```
command          → imperative parameter_list option_list TERMINAL
imperative       → word
parameter_list   → ( parameter )+
parameter        → text
option_list      → ( option )+
option           → SLASH option_name ( COLON option_value )*
option_name      → word
option_value     → text
text             → TEXT | WORD
word             → WORD
```

### Resolution Architecture

The resolution step involves identifying the right command to run. This step uses the imperative parsed previously and matches the imperative to exactly one `CommandExtractor` (explained later, in validation), which builds the final `Command` eventually.

#### Core Components

**`Decoder`**: The entry point that orchestrates command resolution. Given a `BareCommand`, it:
1. Extracts the imperative (command verb)
2. Queries `Bindings` to find the matching `CommandExtractor`
3. Delegates to that extractor to build the final `Command`

**`Bindings`**: An enumeration serving as the **command registry**. Each enum constant associates:
- An **imperative string** (e.g., `"tag"`)
- A **CommandExtractor** (method reference like `TagCommandExtractor::extract`)

This enum acts as the single source of truth for all available commands. Adding a new command requires adding one line to this enum.

**`BareCommand` to `Command` Transformation**

The system transforms generic `BareCommand` objects (containing raw imperative, parameters, and options) into specific, type-safe `Command` instances ready for execution. This separation allows the parser to remain generic while enabling domain-specific validation and construction logic for each command type.

**Exact Matching Strategy**

The `Decoder.decode()` method uses **exact matching** via `Bindings.resolveExactBinding()`:
1. A predicate tests each binding's imperative for equality with the input
2. If no matches found: throws `ResolutionException` ("Unable to find a valid matching command")
3. If multiple matches found: throws `ResolutionException` ("Resolved command is ambiguous")
4. If exactly one match: returns that binding's extractor

This strict resolution ensures deterministic command dispatch and catches configuration errors (duplicate imperatives) for now.

**Flexible Resolution Support**

The above also allows us to accommodate more flexible resolution for commands in the future.

For exact binding resolution, we can support more flexible matching strategies (prefix matching, aliases) in the future by simply modifying the predicate passed in by the decoder.

The `Bindings.resolveBindings()` method supports even more flexible matching strategies (fuzzy search) by returning all matching extractors. While not currently used by `Decoder`, this enables future features like command suggestions.

### Validation Architecture

The validation step involves assembling the final command by parsing all necessary parameters and options and constructing the final `Command` executor object.

#### Core Components

**`CommandExtractor<T>`**: A functional interface defining the contract for command-specific extraction logic. Each extractor:
- Accepts a `BareCommand` (generic parsed representation)
- Validates parameters and options and transforms them into valid constructor inputs for their respective `Command` constructors according to command-specific rules
- Constructs and returns a typed `Command` instance (e.g., `TagCommand`)
- Throws `ValidationException` for invalid inputs
