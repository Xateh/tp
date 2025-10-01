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

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

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

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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

**Target User Profile**: This is primarily for **technologically-inclined asset managers for high-net-worth individuals (HWNIs)** that require managing **complex** relationships and hierarchies between large groups of people.

In addition, they:
- prefer desktop apps over other types
- can type fast
- prefers typing to mouse interactions
- is reasonably comfortable using CLI apps

**Value Proposition**: **AssetSphere** strives in streamlining contact tracking, and managing complex networks with business partners and critical contacts within and outside the company. This application is valuable for asset managers of multinational companies, improving reliability, navigability and efficiency of their contact chain, one contact at a time.

### User Stories

**User type/role**<br />_As a_ | **Function**<br />_I can_                                               | **Benefit**<br />_so that_ | Priority
-|-------------------------------------------------------------------------| - | -
Asset manager | save data between sessions                                              | I don’t have to re-enter information after relaunching | Must-have (✓✓)
Organised asset manager | categorise contacts based on asset classes                              | I can quickly identify relevant people per portfolio | Must-have (✓✓)
Detailed asset manager | customise client profiles with custom fields                            | I can track unique client details/needs | Must-have (✓✓)
Asset manager | define links between clients and their contacts                         | I can navigate complex relationship networks | Must-have (✓✓)
Asset manager | view all clients linked to a specific asset class                       | I can understand dependencies within each asset | Must-have (✓✓)
Tech-savvy asset manager | save my data in a portable, human-readable format                       | I can edit/process data in other apps and move between devices | Must-have (✓✓)
Asset manager | have tiers/priority flags in my client list                             | I can track who the big players are | Must-have (✓✓)
Asset manager | have custom notes for each contact                                      | I can attach longer pieces of information to contacts | Must-have (✓✓)
Tech-savvy asset manager | make custom aliases for commands                                        | I can use shortcuts that match my preferences | Nice-to-have (✓)
Clumsy asset manager | have my commands be inferred (string matching, autocorrect, fuzzy find) | I don’t have to retype or I can use shorter variants | Nice-to-have (✓)
Considerate asset manager | see the time zone for a contact                                         | I can schedule sensibly | Nice-to-have (✓)
Asset manager | delete contacts                                                         | the app remains clean and easy to find things | Nice-to-have (✓)
Asset manager | have my address book flag important dates (e.g., birthdays)             | I can remember important dates and maintain relationships | Nice-to-have (✓)
Clumsy asset manager | merge/handle duplicate records                                          | the network remains clean and accurate | Nice-to-have (✓)
Asset manager | view my command history                                                 | I can redo regularly used commands | Nice-to-have (✓)
Tech-savvy asset manager | save custom workflows (sequences of commands)                           | I can quickly execute common workflows | Nice-to-have (✓)
Tech-savvy asset manager | have my commands be tab-completable                                     | I can quickly fill in command information | Nice-to-have (✓)
Asset manager | have syntax highlighting and diagnostics for commands                   | I can more easily spot mistakes as I type | Nice-to-have (✓)
Asset manager | see detailed, decorated error messages                                  | I can quickly understand and fix mistyped commands | Nice-to-have (✓)
Asset manager | have command syntax hints show up as I type                             | I can see what I need to fill in | Nice-to-have (✓)
Busy asset manager | segment users via predefined metrics (e.g., geography, sector)          | I can find useful contacts faster for different scenarios | Nice-to-have (✓)
Tech-savvy asset manager | use shell-like expansion for commands                                   | I can batch-run commands | Nice-to-have (✓)
Tech-savvy asset manager | use command pipes (\|) to chain commands                                | I can create complex workflows | Nice-to-have (✓)
Asset manager | recursively delete contacts                                             | related links/notes are cleaned up safely | Nice-to-have (✓)
Asset manager | set (scoped) default values for common command flags/inputs             | I can type shorter commands by omitting defaults | Nice-to-have (✓)
Asset manager | have the system flag potential conflicts                                | I can proactively see clashes in notes/tags/tiers | Rejected (✗)
Asset manager | navigate past commands quickly                                          | I can scroll through previous commands | Rejected (✗)
Asset manager | hide or expire old contacts automatically                               | I can keep my list tidy without manual effort | Rejected (✗)
Tech-savvy asset manager | use a custom search syntax                                              | I can craft advanced queries beyond basic filters | Rejected (✗)
Asset manager | store key files (e.g., NDAs, letters) with contacts                     | I can keep documents alongside records | Rejected (✗)
Asset manager | assign follow-ups to team members                                       | I can distribute work across the team from the app | Rejected (✗)


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

**Use case: Find set of contacts that fit a specific filter**

**MSS**

1. User keys command to specify criteria on which to match contacts/records.
2. System searches for records that match the provided criteria.
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

* 3a. User releases that they made an incorrect link with invalid fields.
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

### Non-Functional Requirements

**Performance**:
- The system should respond within **1 second** for each command.

**Portability**:
- The product should work on all machines (**Windows, Linux, Mac**).
- The system should only require Java 17 or higher.

**Security**:
- All data must be stored **locally inside a save file**.

**Scalability**:
- The system should remain usable with up to **1000 contacts**, with command execution time not exceeding **3 seconds**.

**Usability**:
- Error messages must pinpoint exact error and suggest fixes

**Speed**:
- A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
