package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.FieldCommand.MESSAGE_AT_LEAST_ONE_PAIR;
import static seedu.address.logic.commands.FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK;
import static seedu.address.logic.commands.FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class FieldCommandTest {

    private Model model;

    @BeforeEach
    void setup() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        // Add one person so index 1 is valid
        Person base = new PersonBuilder().withName("Alex Yeoh").build();
        model.addPerson(base);
    }

    @Test
    void executeSinglePairSuccess() throws Exception {
        Map<String, String> input = new LinkedHashMap<>();
        input.put(" company ", " Goldman Sachs ");
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), input);

        String feedback = cmd.execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
    }

    @Test
    void executeIndexOutOfBoundsThrows() throws Exception {
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(2), Map.of("k", "v"));
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ex.getMessage());
    }

    @Test
    void executeMessageFormatsWithCommaAndOrder() throws Exception {
        Map<String, String> ordered = new LinkedHashMap<>();
        ordered.put("asset-class", "gold");
        ordered.put("company", "GS");
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), ordered);

        String msg = cmd.execute(model).getFeedbackToUser();

        assertTrue(msg.startsWith("Added/updated field(s): "));
        assertTrue(msg.contains("asset-class:gold, company:GS"));
        assertTrue(msg.endsWith("for Alex Yeoh"));
    }

    @Test
    void constructorNullIndexThrows() {
        Map<String, String> pairs = Map.of("k", "v");
        assertThrows(NullPointerException.class, () -> new FieldCommand(null, pairs));
    }

    @Test
    void constructorNullPairsThrows() {
        assertThrows(NullPointerException.class, () -> new FieldCommand(Index.fromOneBased(1), null));
    }

    @Test
    void constructorEmptyPairsThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new FieldCommand(Index.fromOneBased(1), Map.of()));
        assertEquals(MESSAGE_AT_LEAST_ONE_PAIR, ex.getMessage());
    }

    @Test
    void constructorBlankKeyThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("   ", "v"); // trims to empty -> validate fails
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new FieldCommand(Index.fromOneBased(1), pairs));
        assertEquals(MESSAGE_NAME_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void constructorBlankValueThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("k", "   "); // trims to empty -> validate fails
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new FieldCommand(Index.fromOneBased(1), pairs));
        assertEquals(MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void constructorNullKeyThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put(null, "v"); // null -> normalizeKey returns ""
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new FieldCommand(Index.fromOneBased(1), pairs));
        assertEquals(MESSAGE_NAME_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void constructorNullValueThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("k", null); // null -> normalizeValue returns ""
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new FieldCommand(Index.fromOneBased(1), pairs));
        assertEquals(MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }
}

