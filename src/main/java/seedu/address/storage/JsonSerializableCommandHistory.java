package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.model.history.CommandHistory;

/**
 * An immutable representation of the command history that is serializable to JSON format.
 */
@JsonRootName(value = "commandhistory")
class JsonSerializableCommandHistory {

    private final List<String> commands = new ArrayList<>();

    @JsonCreator
    JsonSerializableCommandHistory(@JsonProperty("commands") List<String> commands) {
        if (commands != null) {
            this.commands.addAll(commands);
        }
    }

    JsonSerializableCommandHistory(CommandHistory source) {
        commands.addAll(source.getEntries());
    }

    CommandHistory toModelType() {
        return new CommandHistory(commands);
    }
}
