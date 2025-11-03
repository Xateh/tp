---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

AssetSphere builds upon the AddressBook-Level3 project maintained by the CS2103T teaching team and community contributors.
We also acknowledge the JavaFX and PlantUML projects for providing the UI framework and diagram tooling used throughout this guide.

AI was used for the following purposes:
- Generating test cases
- Debugging errors / issues

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Command recall (Up/Down arrow keys)

The command box supports keyboard-based command recall: when the command box is focused the user can press the Up and Down arrow keys to navigate previously entered commands. This behaviour is implemented in `CommandBox` (see `src/main/java/seedu/address/ui/CommandBox.java`) which registers a key event filter for `KeyCode.UP` and `KeyCode.DOWN` and uses `HistoryNavigator` (`src/main/java/seedu/address/ui/HistoryNavigator.java`) to walk the history entries supplied by a `HistorySupplier` (typically `logic::getCommandHistorySnapshot`).

Implementation notes for developers:

- `HistoryNavigator` keeps a pointer that is reset to the end of the entries snapshot; `previous()` returns the most recent entry and moves the pointer backwards; `next()` moves the pointer forward and returns the newer entry or empty if the navigator reaches the end (the command box should be cleared in that case).
- Tests covering this behaviour can be found in `src/test/java/seedu/address/ui/CommandBoxTest.java`.


### Logic component

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to `Decoder` which performs a full assembly process involving parsing, resolution, and validation. This process is described below in greater detail.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

#### Command Assembly (User Input to Execution)

<box type="info" seamless>

See [Detailed Command Assembly Documentation](CommandAssembly.md) for a very detailed piece of documentation on the command assembly process.

</box>

Command assembly in AssetSphere is handled as shown in the following activity diagram.

<puml src="diagrams/assembly/AssemblyActivity.puml" width=1080 />

Before a command can be executed, a sequence of steps assemble the `Command` object to be called. This process, which we call **assembly**, is split into four distinct phases. Any of these phases may produce an exception, which will terminate this process.
1. Lexing - User Input: `String` → Tokenised Command: `TokenisedCommand`
2. Parsing - Tokenised Command: `TokenisedCommand` → AST: `ASTNode.Command`
3. Resolution - Imperative: `String` → Command Extractor: `CommandExtractor`
4. Validation - Bare Command: `BareCommand` → Assembled Command: `Command`

We jointly refer to:
- (1) and (2) together as **recognition**, and
- (3) and (4) together as **decoding**.

**Recognition**

This segment describes the lexer and parser. The lexer and parser are interfaced with through the `BareCommand` facade.

<puml src="diagrams/assembly/RecognitionStructure.puml" width=1080 />

This diagram shows a truncated architecture (with auxiliary classes hidden) organized into five packages:

- **Top-Level Package**: Contains the user-facing `Command` class and its nested `CommandBuilder`
- **Lexer Package**: Contains `CommandLexer`, `TokenisedCommand`, `Token`, and related types for tokenisation
- **Parser Package**: Contains `CommandParser` and exception types for syntactic analysis
    - **AST Package**: Shows all AST node types in the hierarchy (Command, Imperative, ParameterList, etc.)
        - **Visitor Package**: Contains the `AstVisitor` interface and `CommandExtractor` implementation

<puml src="diagrams/assembly/RecognitionSequence.puml" width=1080 />

This sequence diagram traces the high-level flow of execution (omitting exceptions and various internal implementation details) starting from `BareCommand.parse(String)`:

1. **Lexing Phase**: Shows how the lexer processes the string character-by-character using peek/advance/munch operations, creating tokens.
2. **Parsing Phase**: Demonstrates recursive descent parsing with the parser calling various parse methods that mirror the grammar structure.
3. **Extraction Phase** (not an actual distinguished phase): Illustrates the Visitor pattern in action, with the CommandExtractor traversing the AST and populating a BareCommandBuilder. Note that this is an extractor from the AST, and is distinct from extractors from BareCommand (which are discussed later).

The diagram includes notes explaining key concepts at each phase and shows both the normal flow and error handling paths. It clearly shows the three-stage transformation: String → Tokens → AST → BareCommand.

**Decoding**

This segment describes resolution and validation. The components involved are interfaced with through the `Decoder` facade.

<puml src="diagrams/assembly/DecodingStructure.puml" width=1080 />

This diagram shows a truncated architecture (with auxiliary classes hidden) with the classes responsible for resolution and validation demarcated in their own packages.

<puml src="diagrams/assembly/DecodingSequence.puml" width=1080 />

This sequence diagram traces the high-level flow of execution (omitting exceptions and various internal implementation details) starting from `Decoder.decode(BareCommand)`:
1. **Resolution Phase**: Shows how the exact command extractor (exact: no ambiguity) is identified given the imperative.
2. **Validation Phase**: Validates all parameters and options to finish assembling the final `Command`.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components).

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AssetSphere`, which `Person` references. This allows `AssetSphere` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-T13-2/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage`, `UserPrefStorage`, `SessionStorage`, and `CommandHistoryStorage` which means it can be treated as any one of them (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`).

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how new features should be implemented and how certain existing features are implemented.

This guide details the command syntax, parsing behaviour, and design considerations for developers adding new commands.

### Commands

The command framework is built on a **uniform, regular structure**. The parser handles the low-level tokenizing and structural validation (lexing, parsing `+`/`-` prefixes, distinguishing parameters from options). Your command implementation is responsible for **semantic validation** and execution.

**Command Structure**

The parser validates all commands against this high-level grammar:

`imperative parameter_list option_list`

1. **Imperative:** A single `<word>` that identifies the command (e.g., `add`).
2. **Parameter List:** A sequence of parameters that appears _immediately_ after the imperative.
3. **Option List:** A sequence of options that appears _only after_ the entire parameter list.

The parser strictly enforces this order. Any options found before the end of the parameter list will cause a parsing error.

**Designing Parameters**

Parameters are the primary, positional inputs for your command.

- **Semantics:** Parameters are **positional** (order matters), **unnamed**, and generally **required**.
- **Implementation:** Your command will receive an _ordered list_ of parameter "variants." You are responsible for validating this list.
- **Validation:** Your implementation **must** validate:
    - **Count:** Does the number of received parameters match your command's needs (e.g., `add` requires at least one)?
    - **Type:** Is a parameter meant to be an `<index>` actually a positive integer?
    - **Variant:** Does the command accept the variants it received (e.g., does `find` accept an `ADDITIVE` parameter? Probably not.)
- **Parameter Variants:** The parser distinguishes between three parameter types and passes them to your implementation as distinct variants (e.g., as different enum types or subclasses):
    - **`NORMAL`:** A standard text value (e.g., `item` or `"new item"`).
    - **`ADDITIVE`:** A text value prefixed with `+` (e.g., `+tag`). The parser provides the _intent_ (add) and the _value_ ("tag").
    - **`SUBTRACTIVE`:** A text value prefixed with `-` (e.g., `-tag`). The parser provides the _intent_ (remove) and the _value_ ("tag").
- **Overloading:** A single command `imperative` _can_ support multiple parameter signatures (e.g., `find <index>` and `find <string>`). Your implementation must inspect the received parameter list (count, types) to determine which behavior to execute.
- **Whitespace:** The lexer handles whitespace. `param1 "param two"` is tokenized as two `text` parameters: "param1" and "param two". `param1 param two` is tokenized as three `text` parameters: "param1", "param", and "two".

**Designing Options**

Options are optional, named inputs that modify command behavior.
- **Semantics:** Options are **optional**, **non-positional** (order does not matter), and **named**.
- **Implementation:** Options are provided to your command as a collection (e.g., a map) of key-value pairs or keys. You **must not** rely on the order in which they were provided by the user.
- **Default Behaviour:** Because all options are by definition optional, your command implementation **must provide a default behaviour** for every option it supports.
- **Minimum Multiplicity:** Even though all options are optional as described above, it is valid to require a **minimum multiplicity**; that is, require at least a certain number of options to be supplied for the command to work.
- **Formats:** The parser supports two formats and delivers them as such:
    1. **Name-only (Flag):** e.g. `/force`. Your code should check for the _presence_ of the "force" key.
    2. **Name-Value Pair:** e.g. `/priority:high`. Your code should retrieve the "priority" key and its associated _value_ ("high").

**Error Handling**

Error handling is a two-part responsibility:
1. **Parser Errors:** The parser/lexer handles all **syntax errors** (e.g., unbalanced quotes, invalid tokens like `/` in a parameter, options before parameters). If parsing fails, your command will _not_ be executed.
2. **Semantic Errors:** Your command implementation is responsible for all **semantic validation**. This includes:
    - Wrong number of parameters.
    - Invalid parameter _type_ (e.g., `delete "abc"` when an `<index>` was expected).
    - Invalid parameter _variant_ (e.g., `list +tag`).
    - Invalid option _value_ (e.g., `/priority:urgent` when only `low`/`medium`/`high` are allowed).
    - Unknown options (e.g., `/nonexistent`). _Note: The base framework may ignore excess/unknown options by default._

**Documenting Command Syntax**

When writing help text or documentation for your new command, you **must** use the following standard notation.

- **Field Definition:** `<field-name> (<type>): <description>`
    - `<field-name>`: The placeholder in the syntax (e.g., `<index>`).
    - `<type>`: The expected data type.
    - `<description>`: A human-readable explanation.
- **Data Types:**
    - `word`: A single word of text without quotes or whitespace.
    - `string`: Text that can be a single `word` or quoted text `"with spaces"`.
    - `index`: A `word` that must be a positive integer (1, 2, …).
- **Multiplicity Notation:**
    - `item`: Exactly one.
    - `item?`: Zero or one (optional).
    - `item+`: One or more (at least one required).
    - `item*`: Zero or more (optional).

**Adding New Commands**

To add a new command to the system,

1. **Create the Command class** (e.g., `DeleteCommand extends Command`)
2. **Implement a CommandExtractor** (e.g., `DeleteCommandExtractor` with static `extract()` method)
3. **Register in Bindings enum**: Add one line like `DELETE("delete", DeleteCommandExtractor::extract)`.

The system automatically handles routing and dispatch.

**Designing Extractors**

<box type="info" seamless>

Relevant APIs:

- [**`BareCommand` API**](https://github.com/AY2526S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/logic/grammars/command/BareCommand.java)
- [**`Validation` API**](https://github.com/AY2526S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/logic/commands/extractors/Validation.java)

</box>

Extractors are where you extract parameters and options from a given input `BareCommand` and perform all your validation. `BareCommand` is a facade for the output of the lexer and parser, designed to insulate command implementors from concerns regarding the unified command grammar.

To help command implementors, `Validation` is a utility class that exposes numerous common validation methods.

Command implementors should look at these two classes when implementing their command extractors.

When validation fails, command implementors should throw `ValidationException` with appropriate error messages from within each extractor detailing the exact reason for failing validation. All methods in `Validation` already throw automatically, but extractors may want to _enrich_ the error messages by catching and rethrowing.

### Application lifecycle and testability

Lifecycle-specific behaviours are extracted from `MainApp` into a small helper class called `MainAppLifecycleManager` located at `src/main/java/seedu/address/MainAppLifecycleManager.java`.

Purpose:
* Reduce the amount of JavaFX/Platform-specific behaviour inside `MainApp` to make the lifecycle logic easier to unit-test.
* Centralise session directory derivation, session loading, and stop-time persistence (command-history + session snapshot) in a single testable class.

Key points for developers:
* `MainApp` now delegates to `MainAppLifecycleManager` for:
    - Creating `CommandHistoryStorage` and `SessionStorage` instances (previously created inline in `MainApp`).
    - Loading the most recent session snapshot safely (exceptions are caught and logged).
    - Initialising the `Model` either from a restored session snapshot or from storage (with sample data or empty fallback).
    - Persisting command history and session snapshot on stop; failures to save command history do not prevent attempting to persist the session snapshot.
* The helper is deliberately small and designed to be exercised with pure unit tests. See `src/test/java/seedu/address/MainAppLifecycleManagerTest.java` for example tests and expected behaviours (including failure paths).

Additional note about session metadata persistence:
* Session metadata such as GUI settings are now tracked as "session metadata" and marked dirty when they change. These metadata-only changes do not trigger immediate disk writes; instead they are recorded by `SessionRecorder` and a lifecycle-specific API (`Logic#getSessionSnapshotIfAnyDirty()`, implemented in `LogicManager`) exposes a session snapshot when either the address book or session metadata are dirty. `MainAppLifecycleManager#persistOnStop` consumes that snapshot so that shutdown-time persistence includes metadata-only changes (for example, window size/position). See `src/main/java/seedu/address/logic/session/SessionRecorder.java`, `src/main/java/seedu/address/logic/LogicManager.java`, and `src/main/java/seedu/address/MainAppLifecycleManager.java`.

Important refinement — what actually triggers a session file write:

* The app now persists a session snapshot on stop only when the information that will be written to the session file has changed compared to the last persisted snapshot. Concretely, this comparison ignores the snapshot timestamp (`savedAt`) — i.e., a different `savedAt` value alone will *not* cause a new file to be written. Only changes to the address book contents or the GUI settings (window size/position) will make the session "dirty" for persistence.

* As a consequence, transient UI-only changes that do not affect any of the persisted session attributes (for example, the textual feedback displayed in the command-result box) do not mark the session as dirty and will not cause an extra session file to be created on exit.

This behaviour is implemented by maintaining a persisted "session signature" (a snapshot of address book contents and GUI settings) and comparing prospective snapshots against that signature before saving; see `SessionRecorder` for details.

Testing tip: Add unit tests that mutate only GUI settings (for example, window size/position) and assert that `getSessionSnapshotIfAnyDirty()` returns a snapshot (while `getSessionSnapshotIfDirty()` remains reserved for address-book-only dirty checks). This ensures the lifecycle save-on-stop behaviour remains correct.

Developer notes when modifying lifecycle behaviour:
* Keep `MainApp` changes minimal: prefer delegating to `MainAppLifecycleManager` rather than pulling JavaFX logic into the helper. This preserves production behaviour while improving testability.
* When changing where session files are stored, update `JsonSessionStorage#createFileName` and the relevant docs in the User Guide.
* For any change that affects persistence ordering (e.g., command history vs session snapshot), add unit tests that simulate the storage throwing IOExceptions to ensure the app continues to try saving the session snapshot.

Testing guidance:
* Unit tests should avoid launching JavaFX. Use the `MainAppLifecycleManagerTest` as a template — it provides several storage/logic stubs that exercise:
    - `deriveSessionDirectory(Path)` behaviour for files with and without parent directories.
    - `loadSession(Storage)` safe-loading semantics including handling `DataLoadingException`.
    - `initModel(Storage, UserPrefs, Optional<SessionData>)` paths: restored session preferred; storage read used otherwise; fallback to empty address book on read errors.
    - `persistOnStop(Storage, Logic)` behaviour for saving command history and session snapshot, and the proper handling of IO failures.

Small API contract (summary):
* Inputs: `Storage`, `ReadOnlyUserPrefs`, `Optional<SessionData>`, `Logic` for the persistence call.
* Outputs: `Model` (initialised), optional persisted files on stop.
* Error modes: catches `DataLoadingException` and `IOException` and logs warnings/errors rather than throwing; this mirrors previous `MainApp` behaviour.

Proactive follow-ups you may consider:
* If you add more lifecycle responsibilities (e.g., scheduled background persistence), provide an explicit interface to enable/disable it for tests.
* Add integration tests that run the JavaFX application in headless mode for end-to-end verification if CI supports it.


### Info Command Feature

The `info` command provides a way to add or edit a multi-line, free-text note for a specific person. Unlike most commands that directly manipulate the model upon execution, the `info` command has a unique two-part workflow that involves direct interaction with the UI component to provide a rich editing experience.

#### Implementation

The mechanism is split into two distinct phases: opening the editor and saving the changes.

1.  **Opening the Editor (Command Execution Phase)**:
    *   The user executes `info <index>`, for example, `info 1`.
    *   The `LogicManager` parses this via `Decoder` and creates an `InfoCommand` with a `targetIndex`.
    *   When `InfoCommand#execute(model)` is called, it validates the index and retrieves the person, but does **not** immediately modify the model. Instead, it:
        *   Validates the index against the filtered person list
        *   Retrieves the person at that index
        *   Calls `UiManager#showInfoEditor(person, index)` to display the editor
        *   Returns a `CommandResult` indicating the editor was opened
    *   The `UiManager` wraps the dialog creation in `Platform.runLater()` to ensure it executes on the JavaFX Application Thread.
    *   A JavaFX `Dialog` is created containing:
        *   A `TextArea` pre-filled with the person's existing info
        *   A `VBox` container for layout
        *   OK and CANCEL buttons
        *   A result converter that captures the edited text when OK is clicked

2.  **Saving the Changes (UI Callback Phase)**:
    *   The user edits the text in the dialog and clicks the "OK" button.
    *   The dialog's result converter captures the edited text.
    *   The `UiManager#savePersonInfo(personIndex, infoText)` method is invoked, which:
        *   Creates an `Info` object from the text
        *   Calls the static method `InfoCommand.saveInfo(model, logic, index, newInfo)`
    *   This static method:
        *   Validates the index again against the current filtered list
        *   Creates a new `Person` object with the updated `Info` using `PersonBuilder`
        *   Uses `model.setPerson(...)` to replace the old person in the address book
        *   Calls `logic.markAddressBookDirty()` to ensure the session snapshot will be persisted
        *   Returns a `CommandResult` with success feedback
    *   The change triggers the model's change listeners, updating the UI.
    *   The `UiManager` calls `mainWindow.showFeedback()` to display the success message in the result display.

The following sequence diagram illustrates the process of opening the info editor:
<puml src="diagrams/InfoSequenceDiagram.puml" width="574" />

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product Scope

**Target User Profile**: This is primarily for **technologically-inclined asset managers for high-net-worth individuals (HNWIs)** that require managing **complex** relationships and hierarchies between large groups of people.

In addition, they:
- prefer desktop apps over other types
- can type fast
- prefers typing to mouse interactions
- is reasonably comfortable using CLI apps

**Value Proposition**: **AssetSphere** strives in streamlining contact tracking, and managing complex networks with business partners and critical contacts within and outside the company. This application is valuable for asset managers of multinational companies, improving reliability, navigability and efficiency of their contact chain, one contact at a time.

### User Stories

**User type/role**<br />_As a_ | **Function**<br />_I can_                                                      | **Benefit**<br />_so that_ | Priority
-|--------------------------------------------------------------------------------| - | -
Asset manager | find for contacts using keywords based on all fields                           | I can cast wider searches | Must-have (✓✓)
Asset manager | additively add tags                                                            | I do not have to waste time retyping a full list of tags whenever I wish to update a user's tags | Must-have (✓✓)
Asset manager | remove existing tags                                                           | I do not have to waste time retyping a full list of tags whenever I wish to update a user's tags | Must-have (✓✓)
Asset manager | save data between sessions                                                     | I do not have to re-enter information after relaunching | Must-have (✓✓)
Organised asset manager | categorise contacts based on asset classes                                     | I can quickly identify relevant people per portfolio | Must-have (✓✓)
Detailed asset manager | customise client profiles with custom fields                                   | I can track unique client details/needs | Must-have (✓✓)
Asset manager | define links between clients and their contacts                                | I can navigate complex relationship networks | Must-have (✓✓)
Asset manager | view all clients linked to a specific asset class                              | I can understand dependencies within each asset | Must-have (✓✓)
Tech-savvy asset manager | save my data in a portable, human-readable format                              | I can edit/process data in other apps and move between devices | Must-have (✓✓)
Asset manager | have tiers/priority flags in my client list                                    | I can track who the big players are | Must-have (✓✓)
Asset manager | have custom notes for each contact                                             | I can attach longer pieces of information to contacts | Must-have (✓✓)
Tech-savvy asset manager | make custom aliases for commands                                               | I can use shortcuts that match my preferences | Nice-to-have (✓)
Clumsy asset manager | have my commands be inferred                                                   | I do not have to retype or I can use shorter variants | Nice-to-have (✓)
Considerate asset manager | see the time zone for a contact                                                | I can schedule sensibly | Nice-to-have (✓)
Asset manager | delete contacts                                                                | the app remains clean and easy to find things | Nice-to-have (✓)
Asset manager | have my address book flag important dates (e.g., birthdays)                    | I can remember important dates and maintain relationships | Nice-to-have (✓)
Clumsy asset manager | merge/handle duplicate records                                                 | the network remains clean and accurate | Nice-to-have (✓)
Asset manager | view my command history                                                        | I can redo regularly used commands | Nice-to-have (✓)
Tech-savvy asset manager | save custom workflows (sequences of commands)                                  | I can quickly execute common workflows | Nice-to-have (✓)
Tech-savvy asset manager | have my commands be tab-completable                                            | I can quickly fill in command information | Nice-to-have (✓)
Asset manager | have syntax highlighting and diagnostics for commands                          | I can more easily spot mistakes as I type | Nice-to-have (✓)
Asset manager | see detailed, decorated error messages                                         | I can quickly understand and fix mistyped commands | Nice-to-have (✓)
Asset manager | have command syntax hints show up as I type                                    | I can see what I need to fill in | Nice-to-have (✓)
Busy asset manager | segment users via predefined metrics (e.g., geography, sector)                 | I can find useful contacts faster for different scenarios | Nice-to-have (✓)
Tech-savvy asset manager | use shell-like expansion for commands                                          | I can batch-run commands | Nice-to-have (✓)
Tech-savvy asset manager | chain commands by passing outputs from previous commands as inputs to the next | I can create complex workflows | Nice-to-have (✓)
Asset manager | recursively delete contacts                                                    | related links/notes are cleaned up safely | Nice-to-have (✓)
Asset manager | set (scoped) default values for common command flags/inputs                    | I can type shorter commands by omitting defaults | Nice-to-have (✓)
Asset manager | have the system flag potential conflicts                                       | I can proactively see clashes in notes/tags/tiers | Rejected (✗)
Asset manager | navigate past commands quickly                                                 | I can scroll through previous commands | Rejected (✗)
Asset manager | hide or expire old contacts automatically                                      | I can keep my list tidy without manual effort | Rejected (✗)
Tech-savvy asset manager | use a custom search syntax                                                     | I can craft advanced queries beyond basic filters | Rejected (✗)
Asset manager | store key files (e.g., NDAs, letters) with contacts                            | I can keep documents alongside records | Rejected (✗)
Asset manager | assign follow-ups to team members                                              | I can distribute work across the team from the app | Rejected (✗)


### Use Cases

(For all use cases below, the **System** is `AssetSphere` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add set of contacts**

**MSS**

1. User keys command for adding a contact and specifies all necessary data fields for that contact.
2. System validates command and input data fields.
3. System adds contact with populated data fields to records.
4. System displays to the user information about the most recently added contact.

   Steps 1-4 are repeated until all contacts have been added.

   Use case ends.

**Extensions**

* 2a. System is unable to parse or decode command.
    * 2a1. System informs user of exact error and hints at how to fix the error.
    * 2a2. User corrects and reenters the command.

  Steps 2a1-2a2 are repeated until a valid command is entered.

* 4a. User realises that they added an invalid contact or a contact with invalid data fields.
    * 4a1. User keys command for deleting that contact.

  Use case ends.

**Use case: Comprehensive find set of contacts that fit a specific filter**

**MSS**

1. User keys command to specify criteria on which to match contacts/records.
2. System searches for records for any fields that match the provided criteria.
3. System displays to user the records found.

   Steps 1-3 are repeated with the user refining their criteria until they are satisfied with    the located records.

   Use case ends.

**Use case: Add links between contacts**

**MSS**

1. User keys command to specify a named link between contacts saved.
2. System searches for the contacts specified by index number and adds a named link between them.
3. System displays to the user the link formed.

   Steps 1-3 are repeated for all links that are to be added.

   Use case ends.

**Extensions**

* 3a. User realises that they made an incorrect link with invalid fields.
    * 3a1. User keys command for deleting the affected contacts.
    * 3a2. User adds the deleted contacts in 3a1.
    * 3a3. User keys in link command with the correct fields.

**Use case: Mass import data from external source**

**MSS**

1. User locates local data file containing all existing records.
2. User converts their external source into the same format as that expected by the system.
3. User injects their formatted external data into the local data file.
4. User starts the system.
5. System validates the data file.

   Use case ends.

**Extensions**

* 5a. Data file format is invalid after manual user addition.
    * 5a1. System informs user that the data file may have been corrupted.
    * 5a2. User closes the system and fixes the error in the data file.

  Use case resumes from step 4.

**Use case: Add tags to contacts**

**MSS**

1. User keys command to add tags to a contact.
2. System searches for contact specified by index number and adds the specified tags to the contact.
3. System displays to user the tags added to the specified contact.

   Steps 1 - 3 are repeated for all tags that are to be added.

   Use case ends.

**Extensions**

* 2a. System is unable to parse or decode command.
    * 2a1. System informs user of exact error and hints at how to fix the error.
    * 2a2. User corrects and reenters the command.

  Steps 2a1-2a2 are repeated until a valid command is entered.

* 3a. User realises he added the tags wrongly.
    * 3a1. User keys command for untagging the wrongly added tags.
    * 3a2. User repeats steps 1-3 to add the correct tags.

  Use case ends.

**Use case: Remove tags from contacts**

**MSS**

1. User keys command to remove tags from a contact
2. System searches for contact specified by index number and removes the specified tags from the contact
3. System displays to user the tags removed from the specified contact

   Steps 1–3 are repeated until the user is satisfied with the tags remaining on their contacts.

   Use case ends

**Extensions**

* 2a. System is unable to parse or decode command.
    * 2a1. System informs user of exact error and hints at how to fix the error.
    * 2a2. User corrects and reenters the command.

  Steps 2a1-2a2 are repeated until a valid command is entered.

* 2b. System is unable to find the specified tags to remove
    * 2b1. System informs user that the tag is not found
    * 2b2. User corrects and reenters the command

  Steps 2b1-2b2 are repeated until a valid tag to be removed is entered.

* 3a. User realises he removed the tags wrongly
    * 3a1. User keys command for re-tagging the wrongly removed tags
    * 3a2. User repeats steps 1-3 to remove the correct tags

  Use case ends.

**Use case: Add fields to contacts**

**MSS**

1. User keys command to add fields with a corresponding value to a contact.
2. System searches for contact specified by index number and adds the specified field with the corresponding value to the contact
3. System displays to user the field and associated value added to the specified contact.

    Steps 1-3 are repeated until all desired fields have been
added.

    Use case ends.

**Extensions**

* 2a. System is unable to parse or decode command.
    * 2a1. System informs user of exact error and hints at how to fix the error.
    * 2a2. User corrects and reenters the command.

  Steps 2a1-2a2 are repeated until a valid command is entered.

* 2b. The field to be added already exists.
    * 2b1. System updates the already existent field with the new value passed in.

* 3a. The user realised he added the fields wrongly.
    * 3a1. User keys command to delete the wrongly added field

  Use case resumes from step 1

**Use case: Delete fields from contacts**

**MSS**

1. User keys command to delete field from a contact.
2. System searches for contact specified by index number and removes specified field.
3. System displays to user the field removed from the specified contact.

   Steps 1-3 are repeated until all desired fields have been removed.

   Use case ends.

**Extensions**

* 2a. System is unable to parse or decode command.
    * 2a1. System informs user of exact error and hints at how to fix the error.
    * 2a2. User corrects and reenters the command.

  Steps 2a1-2a2 are repeated until a valid command is entered.

* 2b. The field to be removed does not exist.
    * 2b1. System informs user that the field is not found.
    * 2b2. User corrects and reenters the command.

* 3a. User realises he removed the wrong field.
    * 3a1. User keys command to add the wrongly deleted field.

  Use case resumes from step 1

### Non-Functional Requirements

**Performance**:
- The system should respond within **1 second** for each command.
- App cold start to first command within **3 second**.
- App shutdown should not take more than **5 second**.

**Portability**:
- The product should work on all machines (**Windows, Linux, Mac**).
- The system should only require Java 17 or higher.

**Security**:
- All data must be stored **locally inside a save file**.

**Scalability**:
- The system should remain usable with up to **1000 contacts**, with command execution time not exceeding **3 seconds**.

**Usability**:
- Error messages must pinpoint exact error and suggest fixes.

**Speed**:
- A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

* **Refactor**: Changing the internal structure of the code (how it's written) to make it clearer, simpler, or easier to maintain — without changing what the application does for users.
* **Session snapshot**: A saved copy of the app’s current working state used to restore window view and settings after closing and reopening the app (for example: address book contents, and window size/position).
* **Stop-time persistence**: The process that runs when the application is closing which saves the session snapshot and related data (for example: command history, and window size/position) to disk so the app can restore the same state when reopened.
* **API**: Short for Application Programming Interface — a set of rules and named operations that lets different parts of the program (or other programs) ask for data or request actions from a component.
* **GUI (Graphical User Interface)**: The visual parts of the app you interact with — windows, buttons, lists and menus — as opposed to typing commands in a text console.
* **JavaFX**: A Java toolkit used to build the app's graphical interface (windows, controls, and layouts). The app's UI is implemented using JavaFX.
* **PlantUML**: A text-based tool that generates diagrams (architecture, sequence, etc.) from simple textual descriptions; used to produce the diagrams in this guide.
* **JSON**: A lightweight, human-readable text format used to store structured data (for example, the address book and user preferences are saved in JSON files).
* **ObservableList**: A list type that notifies the user interface when items are added, removed or changed so the display updates automatically (e.g., the person list refreshes when you add a contact).
* **Undo/redo**: The ability to revert the most recent change (undo) or reapply a reverted change (redo), usually implemented by keeping past copies of the data so you can move backward or forward through them.
* **CLI (Command-Line Interface)**: An alternative to the GUI where you control the app by typing commands into a console or terminal.

**Assembly-Related**

* **Command Assembly**: The logic subsystem that manages the full process from parsing a user command to constructing the executable command object.
* **AST**: Abstract syntax tree; this is the tree-like structure generated after parsing a string that conforms to a formal grammar defined in terms of production rules.
* **Lexing**: The process of converting text into meaningful lexical tokens belong to specific categories. As an analogy, English sentences can be lexed into nouns, verbs, adjectives, etc. The list of lexical tokens used by the command grammar can be found [here](CommandAssembly.md).
* **Token**: A small, meaningful piece of input produced by the lexer (for example, a single word, a number, or a quoted phrase); tokens are the basic units the parser uses to understand a command.
* **Parser**: The component that analyses a sequence of tokens and determines their grammatical structure (often producing an AST) so the application can decide what action to perform.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Covering individual commands

For command-specific walkthroughs (inputs, expected outputs, and edge cases), refer to the examples in the [User Guide](UserGuide.md#features). Each command subsection there documents the canonical success path, common variations, and the validation rules enforced by AssetSphere.

### Launch and shutdown

1. Initial launch

   1. Download the jar file `assetsphere.jar` and copy into an empty folder `./dir/`.

   1. Launch the jar file with `java -jar ./dir/assetsphere.jar`.<br>
      Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app with `java -jar ./dir/assetsphere.jar`.<br>
      Expected: The most recent window size and location is retained.

### Scenario-based end-to-end testing

Use the following extended scenarios to exercise multiple features together. They are written so that the cases, when executed
in order, collectively cover the command set exposed in the User Guide.

#### Onboarding new clients during a strategy meeting

1. Preparation

   1. Run `clear` to start from an empty address book.<br>
      Expected: The contact list becomes empty and the status bar shows an updated sync timestamp.

   1. Use `add` to register five new contacts (example data shown below). Repeat until five names appear in the list.
      ```
      add "Angeline Tan" 91234567 "1 Raffles Place" "angeline@example.com" /tag:prospect
      add "Bernard Chua" 92345678 "20 Cecil Street" "bernard@example.com"
      add "Clara Goh" 93456789 "8 Marina Boulevard" "clara@example.com"
      add "Dinesh Rao" 94567890 "15 Scotts Road" "dinesh@example.com"
      add "Evelyn Lim" 95678901 "9 Battery Road" "evelyn@example.com"
      ```
      Expected: After each command, the new person card appears in the list and the command result confirms the addition.

   1. Enter `list` to ensure all five contacts are visible.<br>
      Expected: The person list displays the five newly added entries in insertion order.

2. Capture evolving context during the meeting

   1. Correct an earlier typo using `edit 2 /phone:98887776 /email:"bernard.c@example.com"`.<br>
      Expected: Bernard's phone and email fields update; the command feedback summarises the edited fields.

   1. Invoke `info 3` and type a short note such as "Leads regional partnerships" before saving.<br>
      Expected: A modal text editor opens. After saving (by closing the modal text editor), re-opening `info 3` reveals the saved text.

   1. Attach structured metadata with `field 4 /specialty:"Regulatory advisory" /assistant:"Mei"`.<br>
      Expected: Dinesh's card shows the newly added custom fields under the contact details panel.

   1. Tag interested parties in bulk via `tag 1 +priority +client` and `tag 5 +interested`.<br>
      Expected: The specified tags appear on the respective person cards without affecting other tags.

   1. Highlight referral relationships with `link 1 mentor 3`.<br>
      Expected: Angeline shows Clara as a mentee, and Clara lists Angeline as her mentor in the links section.

   1. Surface everyone marked as interested stakeholders by running `find interested /tag`.<br>
      Expected: The filtered list displays contacts tagged `interested`; removing the filter with `list` restores the full view.

   1. Verify the command log using `history`.<br>
      Expected: Recent commands (including `add`, `edit`, `field`, `tag`, `link`, `find`) appear in chronological order.

3. Cleanup and persistence

   1. Remove an erroneous duplicate, if any, with `delete <index>` (e.g. `delete 5`).<br>
      Expected: The specified card disappears and the command result shows the deleted person's details.

   1. Exit the app with `exit` or by closing the window.<br>
      Expected: AssetSphere saves the latest address book, command history, and a session snapshot under `data/sessions/`.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Effort**

Given below are the efforts put in by the team for Asset Sphere.

Overall, this project was fairly difficult to implement up to its current state.

Of the implemented features, we highlight the following significant achievements and their technical difficulty:

**Command Assembly**

The system we term 'Command Assembly' is a major reimplementation effort to significantly improve abstraction and cohesion and reduce coupling throughout the entire logic component. The system and its motivations are described in detail here[link to docs].

It involves a near-complete redesign of the architecture of the logic component. Major efforts include:
- using established and mathematically-proven techniques (FSM lexing and recursive-descent parsing) to write a common parser for all commands to avoid ad-hoc parsing and parsing errors or inconsistencies,
- insulating command implementers from common command syntax to make newer commands easier to implement (by ridding implementers of having to worry about significant parsing), and
- accommodating significant extensibility in the entire process from parsing and decoding an input command to creating the final executable command object, by isolating each stage as far as reasonably expectable.

**Command History with Keyboard Navigation**

The main challenges were defining precise pointer semantics, keeping the navigator synchronized with live history, and preserving testability for UI-driven flows.

Major effort includes:
- History Navigator pointer behavior is defined to ensure exact pointer semantics and synchronization so that the recalling is consistent when new commands are added.
- UI integration is required to detect the UP / DOWN keystrokes in the command box. Logic is extracted from UI for unit-testing compatibility. Unit tests are subsequently used to check for navigator edge cases, command box navigation flows and storage error handling to ensure robustness.

**Save mechanism with session snapshots**

The key aspect for this feature is ensuring accurate dirty detection, avoiding unnecessary disk writes, and safe shutdown ordering when IO failures occur. As we implement more features like fields, and links, this feature needs to capture the new information in an adaptable yet reliable manner.

Major efforts include:
- Implementing dirty-checking and snapshot equality to compare deep copies of persisted attributes while ignoring transient values like timestamps.
- Linking and coordinating session information between multiple aspects of the system ie Model, Logic, Ui, to a central session recorder, and integrating shutdown persistence in a lifecycle manager so that ordering and IO failure handling is safe.
- Requires serialization of session information through serialization and ensuring compatibility between multiple session save versions.
- Tests were tricky as many changes were originally made to the MainApp class itself, and this was refactored to the lifecycle manager so that maximum LOCs are available for testing.

**New UI elements**

- Info command that introduced a new textbox was tricky to implement due to the changes to be made in UiManager and other Ui components. This, alongside the saving of session logic required multiple iterations to get right.

- Field command that introduced a new container-like “pill”, similar to that of the existing Tag command, just in a separate colour scheme. The spacing between each field “pill”, and spacing between each row of content represented on the CLI, was difficult to implement for a consistent UI.

**Custom Field command for contacts**

This feature allows users to establish their own custom fields for each contact on top of the already built-in custom fields (like name, email, address etc.). This was particularly tricky, especially in the later stages, when we realised that there were more considerations that had to be taken into account, such as banning particular keywords as custom fields (like ‘to’, ‘from’, and already built in field names) and how the user is able to edit and remove these custom fields. On top of this, differentiating between removals from empty custom field values also posed as a hindrance.

**Link command between contacts**

This feature allows users to establish a named link between contacts in the address book. Implementing the visuals of that link proved to be fairly difficult as to show the respective directions of the link in the UI was tough to implement. After implementing the bare functional part, the major bug that was the hardest to fix was implementing link refactoring when the user edits/deletes certain pre-linked contacts in the address book as this meant having to change both the UI side and the logic side where the entire link instance stored have to be refactored for every persons in the current address book.

**Comprehensive search while allowing specified search filters**

The find feature that was given was initially a very simple find that only searched on contact’s name. Given that our app was meant for wealth managers who we recognise require searching on specified filters, we wanted to improve on find by implementing a customised enhanced search system that allows users to specify specific fields to search on. Even with simple built in fields, it was a pretty large refactoring, but the hard part came when we had to implement searching on custom fields as well as the links with direction.



