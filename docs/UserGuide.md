---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AssetSphere User Guide

AssetSphere is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, AssetSphere can get your contact management tasks done faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar addressbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

#### Basic Command Structure

All commands follow the same simple format:

`command <parameters…> <options…>`

1. **Command:** The action you want to perform (e.g., `add`, `list`, `delete`).
2. **Parameters:** Inputs the command _needs_ to work. These are usually **required**, and their **order matters**.
3. **Options:** Optional settings that change _how_ the command works. They always start with a slash (`/`) and can be in **any order**, but must come _after_ all parameters.

**Parameters**

Parameters are the main inputs for a command.
- **Order is Key:** You must provide them in the specific order shown in the command's help text.
- **Handling Spaces:** If your parameter includes spaces, you **must** wrap it in straight quotes (`""`).
    - `add "New task"` (This is one parameter: "New task")
    - `add New task` (This is two parameters: "New" and "task")
- **Parameter Types:** Some commands use special prefixes for parameters:
    - **Normal:** `"My task"`
    - **Additive:** `+tag` (Used to add an item, like a tag)
    - **Subtractive:** `-tag` (Used to remove an item, like a tag)

**Options**

Options are optional settings to customise your command. They always come _after_ all parameters. There are two types of options:
1. **Name-only (Flag):** Used to turn a setting on.
    - **Example:** `list /all` (The `/all` flag might show completed items)
2. **Name-Value Pair:** Used to provide a specific value for a setting. Use a colon (`:`) to separate the name and value.
    - **Example:** `add "Finish report" /priority:high`
    - If the value has spaces, wrap the value in quotes: `add "New event" /due:"tomorrow at 5"`

When you look at the help for a command, you'll see this notation:

- **Field Types:**
    - `<string>`: Text that can be a single `word` or `"text with spaces"`.
    - `<word>`: Text that must be a single `word` (without quotes).
    - `<index>`: A positive number (like `1`, `2`, `3`) corresponding to an item in a list.
- **Multiplicity (How many?):**
    - `<item>`: Exactly one is required.
    - `<item>?`: Zero or one (it's optional).
    - `<item>+`: One or more are required.
    - `<item>*`: Zero or more (it's optional and you can provide many).

**Example:** `tag <index>+ <tag>+` means you must provide at least one index, followed by at least one tag.

**Extraneous Parameters and Options**

By default:
- the number of parameters are fixed for commands and should be strictly adhered to. Using an number of parameters that does not conform to the requirements of the command format is **undefined behaviour**. Some commands may gracefully handle extraneous parameters if it is sensible to do so, but this behaviour _should not be relied on_.
- extraneous options are *always* ignored.

</box>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<box type="tip" seamless>

**Tip:** A person can have any number of tags (including 0)
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person : `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Adding a tag : `tag`

Adds a single tag to an existing person in the address book.

Format: `tag INDEX TAG+`

* Adds the specified `TAG` to the person at the given `INDEX` in the displayed list.
* The index refers to the index number shown in the displayed person list and **must be a positive integer** 1, 2, 3, ...
* There can be multiple tags added at one time, the tags are separated by a whitespace

Examples:
* `list` followed by `tag 2 friend cool` will add `friend` and `cool` to the 2nd person in the address book.

### Removing a tag : `untag`

Removes a single tag from an existing person in the address book.

Format: `untag INDEX t/TAG`

* Removes the specified `TAG` from the person at the given `INDEX` in the displayed list.
* The index refers to the index number shown in the displayed person list and **must be a positive integer** 1, 2, 3, …​
* If the contact does not have the specified tag, the command will report an error and no changes will be made.

Examples:
* `list` followed by `untag 2 t/friends` removes the `friends` tag from the 2nd person in the address book.

### Adding information to a person: `infoedit`

Edits information about a person given its index.

Format: `infoedit INDEX`

* Displays an editable text box for the person at the given `INDEX` in the displayed list.
* The index refers to the index number shown in the displayed person list and **must be a positive integer** 1, 2, 3, ...
* If there is existing information attached to the person, it will be shown and editable in the text box.

Examples:
* `list` followed by `infoedit 2` will bring up an editable text box for the 2nd person in the address book.

### Viewing information of a person: `infoview`

View information about a person given its index.

Format: `infoview INDEX`

* Displays all the given information of the person at the `INDEX` specified.
* The index refers to the index number shown in the displayed person list and **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `infoview 2` will display available information about the 2nd person in the address book.

### Locating persons by name: `find`

Finds persons whose fields contain any of the given keywords.

Format: `find <keyword>+ [/<field>]*`

If no specific field is provided, all built-in fields will be searched.
* The search is case-insensitive. e.g `hans` will match `Hans`.
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`.
* All the fields are searched.
* Only full words will be matched e.g. `Han` will not match `Hans`.
* Persons matching at least one keyword on any one field will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`.
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)
* `find 99999999` returns all persons whose phone number is `99999999`.
* `find test.dummy@gmail.com` returns all persons whose email is `test.dummy@gmail.com`.
* `find friend` returns all persons tagged with `"friend"`.

You can limit the search to specific fields by adding options after your keywords.
Each field option starts with / followed by the field name.
* The same rules for searching applies as per the case of searching all built in fields. (see above)
* Now, only those persons matching at least one keyword on any one specified field will be returned.

Examples:
* `find John /name` returns persons whose name contains `john`.
* `find gold /assetclass` returns all persons with custom field called `assetclass` and value contains the word `gold`.
* `find 99999999 /phone` returns all persons whose phone number is `99999999`.
* `find test /name /email` returns all persons whose name or email contains the word `test`.

### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

AddressBook data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, AddressBook will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Tag**    | `tag INDEX TAG+` <br> e.g., `tag 2 friend cool`
**Edit Info** | `infoedit INDEX` <br> e.g., `infoedit 2`
**View Info** | `infoview INDEX` <br> e.g., `infoview 2`
**Remove tag** | `untag INDEX t/TAG` <br> e.g., `untag 2 t/friends`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Help**   | `help`
