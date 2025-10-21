# BareCommand API Reference

## Overview

`BareCommand` is the intermediate representation produced by the parser that your command extractors will work with. It provides a clean, type-safe interface for accessing the parsed components of a command: the imperative, parameters, and options.

## Core Concepts

After the lexer and parser process a command string, they produce a `BareCommand` object containing:

- **Imperative**: The command verb (e.g., `"add"`, `"delete"`, `"find"`)
- **Parameters**: An ordered array of positional arguments
- **Options**: A map of named flags and key-value pairs

Your command extractor receives this `BareCommand` and is responsible for:
1. Extracting the relevant parameters and options
2. Validating them according to your command's requirements
3. Converting them to appropriate domain types
4. Constructing the final executable `Command` object

## Accessing Parameters

### Getting Individual Parameters

```java
String getParameter(int index)
```

Retrieves the parameter at the specified zero-based index. Throws `ArrayIndexOutOfBoundsException` if the index is invalid.

**Example:**
```java
// Command: "add John Doe"
String firstName = bareCommand.getParameter(0);  // "John"
String lastName = bareCommand.getParameter(1);   // "Doe"
```

### Getting All Parameters

```java
String[] getAllParameters()
```

Returns an array containing all parameters in order. Use this when you need to validate the parameter count or iterate through all parameters.

**Example:**
```java
// Command: "tag 1 friend colleague"
String[] params = bareCommand.getAllParameters();  // ["1", "friend", "colleague"]

if (params.length < 2) {
    throw new ValidationException("At least two parameters required");
}
```

## Accessing Options

### Checking Option Presence

```java
boolean hasOption(String key)
```

Returns `true` if the option was specified at least once, regardless of whether it has a value. Use this for boolean flags.

**Example:**
```java
// Command: "delete 1 /force"
if (bareCommand.hasOption("force")) {
    // Execute without confirmation
}
```

### Getting a Single Option Value

```java
Optional<String> getOptionValue(String key)
```

Returns an `Optional` containing the first value associated with the option key. Returns `Optional.empty()` if:
- The option was not specified, or
- The option was specified as a flag without a value (e.g., `/force`)

**Example:**
```java
// Command: "add John /priority:high"
String priority = bareCommand.getOptionValue("priority")
    .orElse("medium");  // Default to "medium" if not specified
```

### Getting Multiple Option Values

```java
Optional<List<String>> getOptionAllValues(String key)
```

Returns an `Optional` containing all values for an option that may be specified multiple times. Returns `Optional.empty()` only if the option was never specified.

**Example:**
```java
// Command: "add Task /tag:work /tag:urgent"
List<String> tags = bareCommand.getOptionAllValues("tag")
    .orElse(List.of());  // Empty list if no tags

// tags = ["work", "urgent"]
```

**Important**: If an option is specified multiple times without values (e.g., `/flag /flag`), this returns an empty list wrapped in an `Optional`, not `Optional.empty()`.

### Getting All Options

```java
Map<String, List<String>> getAllOptions()
```

Returns a read-only view of all option key→value pairs. Useful for debugging or when you need to iterate through all provided options.

## Common Validation Patterns

### Validating Parameter Count

```java
String[] params = bareCommand.getAllParameters();

if (params.length == 0) {
    throw new ValidationException("At least one parameter required");
}

if (params.length < 2) {
    throw new ValidationException("Expected at least 2 parameters, got " + params.length);
}
```

### Parsing Typed Parameters

Note that the following is just an example; index validation is provided as a static method in `Validation::validateIndex`.

```java
public class TagCommandExtractor {
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";
    
    // ...
    
    public static TagCommand extract(BareCommand bareCommand) throws ValidationException {
        // ...
        
        // Parsing an index parameter
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException e) {
            // only thrown by Integer::parseInt
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        } catch (IndexOutOfBoundsException e) {
            // only thrown by Index::fromOneBased
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, params[0]));
        }
        
        // ...
    }
}
```

### Validating Required Options

```java
if (!bareCommand.hasOption("email")) {
    throw new ValidationException("Email option is required");
}

String email = bareCommand.getOptionValue("email")
    .orElseThrow(() -> new ValidationException("Email option must have a value"));
```

### Handling Optional Options with Defaults

```java
String priority = bareCommand.getOptionValue("priority").orElse("medium");

boolean force = bareCommand.hasOption("force");

int limit = bareCommand.getOptionValue("limit")
    .map(Integer::parseInt)
    .orElse(10);  // Default limit
```

### Validating Option Values

```java
String priority = bareCommand.getOptionValue("priority").orElse("medium");

if (!Set.of("low", "medium", "high").contains(priority)) {
    throw new ValidationException(
        "Invalid priority: must be 'low', 'medium', or 'high', got '" + priority + "'"
    );
}
```

## Complete Extractor Example

Here's a complete example showing typical usage patterns:

```java
public class TagCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_TAGS_UNSPECIFIED = "At least one tag must be specified.";

    private TagCommandExtractor() {}

    /**
     * Extracts command parameters from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters from.
     * @return TagCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static TagCommand extract(BareCommand bareCommand) throws ValidationException {
        String[] params = bareCommand.getAllParameters();

        // extract index
        if (params.length <= 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }
        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException e) {
            // only thrown by Integer::parseInt
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        } catch (IndexOutOfBoundsException e) {
            // only thrown by Index::fromOneBased
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, params[0]));
        }

        // extract tags
        if (params.length <= 1) {
            throw new ValidationException(MESSAGE_TAGS_UNSPECIFIED);
        }
        Set<Tag> tags = new HashSet<>();
        for (int i = 1; i < params.length; i++) {
            tags.add(new Tag(params[i]));
        }

        return new TagCommand(index, tags);
    }
}
```

## Best Practices

1. **Always validate parameter count** before accessing individual parameters to avoid `ArrayIndexOutOfBoundsException`.

2. **Provide clear error messages** that tell users exactly what went wrong and what was expected.

3. **Use `Optional` methods effectively**: Prefer `orElse()`, `orElseThrow()`, or `map()` over explicit `isPresent()` checks.

4. **Handle type conversion errors gracefully**: Catch `NumberFormatException`, `DateTimeParseException`, etc., and wrap them in `ValidationException` with helpful messages.

5. **Document your parameter and option requirements** using the standard notation described in the "Documenting Command Syntax" section.

6. **Keep validation logic in the extractor**: Don't leak validation responsibilities into your `Command` class—it should receive only validated, typed inputs.
