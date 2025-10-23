# Session Save Format

This document specifies a JSON-based format for persisting an AddressBook session. A *session* captures the transient state that is normally lost when the application exits, such as command history, the most recent search filter, and the last window layout. The information is complementary to the existing `addressbook.json` data file and `data/preferences.json` user preferences file.

## File Location and Naming

* Session files are stored inside the data directory, under a dedicated `sessions/` subfolder (e.g. `data/sessions/`).
* Each snapshot is written as a unique file named `session-<timestamp>-<zone>.json`, where `<timestamp>` is the local time formatted as `yyyy-MM-dd'T'HH-mm-ss-SSS` and `<zone>` is the system zone ID with slashes replaced by hyphens (e.g. `session-2025-10-14T12-45-00-123-Asia-Singapore.json`).
* At startup, the application picks the snapshot with the latest `savedAt` value automatically.

## Top-Level Structure

```json
{
  "formatVersion": "1.0",
  "savedAt": "2025-10-14T04:45:00Z",
  "addressBookPath": "data/addressbook.json",
  "searchKeywords": ["Alex", "Yang"],
  "commandHistory": [
    {
      "timestamp": "2025-10-14T04:40:12Z",
      "commandText": "list"
    },
    {
      "timestamp": "2025-10-14T04:41:05Z",
      "commandText": "find Alex"
    }
  ],
  "guiSettings": {
    "windowWidth": 1024.0,
    "windowHeight": 768.0,
    "windowX": 120,
    "windowY": 80
  }
}
```

Each property is detailed below.

### `formatVersion`
Identifies the schema revision. Increment when the format changes. Stored as a string to allow semantic versions (e.g. `"1.1"`).

### `savedAt`
The ISO-8601 timestamp (UTC) when the session was persisted. Used to order or purge old sessions.

### `addressBookPath`
Relative or absolute path to the address book file used during the session. Enables reloading the same dataset when restoring.

### `searchKeywords`
Keywords applied by the last successful `find` command. The array is empty when no search is active.

### `commandHistory`
Chronological array of commands entered by the user:

* `timestamp`: ISO-8601 UTC timestamp when the command executed.
* `commandText`: Raw command string accepted by the parser. The array order is the execution order.

### `guiSettings`
Captures the last known window layout:

* `windowWidth` / `windowHeight`: Stored as double precision values.
* `windowX` / `windowY`: Screen coordinates of the window's top-left corner. When the window position is not known, these values default to `0`.

## Validation Rules

* All timestamps must be ISO-8601 strings with timezone information.
* Arrays may be empty but must be present. Missing arrays imply an outdated file.
* Window dimensions must be positive numbers.
* Unknown fields should be ignored to allow forward compatibility.

## Extensibility

* New optional properties may be added at the top level or within objects if consumers follow the ignore-unknown rule.
* Breaking changes require a new `formatVersion`. Implementations should refuse to load files with unsupported versions and surface a clear error.

## Security Considerations

* Session files may contain sensitive command history; store them with user-only permissions (`chmod 600`).
* Do not execute commands directly from the session file without validation to avoid command injection risks.
