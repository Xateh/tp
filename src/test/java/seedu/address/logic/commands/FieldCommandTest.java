package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK;
import static seedu.address.logic.commands.FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.grammars.command.BareCommand;
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
        BareCommand gc = BareCommand.parse("field 1 /company:\"Goldman Sachs\"");
        FieldCommand cmd = new FieldCommand(gc);

        String feedback = cmd.execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
    }

    /*
    @Test
    void executeMultiplePairsOverwriteAndOrder() throws Exception {
        BareCommand gc = BareCommand.parse("field 1 /\"asset-class\":gold /company:\"Goldman Sachs\" /company:GS");
        FieldCommand cmd = new FieldCommand(gc);
        cmd.execute(model);

        Person edited = model.getFilteredPersonList().get(0);
        // last write wins
        assertEquals("GS", edited.getCustomFields().get("company"));
        // order preserved
        assertArrayEquals(new String[]{"asset-class", "company"},
                edited.getCustomFields().keySet().toArray(new String[0]));
    }
    */

    @Test
    void constructorFromGrammarInvalidIndexThrows() throws Exception {
        BareCommand gc = BareCommand.parse("field x /k:v");
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(gc));
    }

    @Test
    void constructorFromGrammarNoPairsThrows() throws Exception {
        BareCommand gc = BareCommand.parse("field 1"); // no /k:v
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(gc));
    }

    @Test
    void executeIndexOutOfBoundsThrows() throws Exception {
        // There is only 1 person in the model
        BareCommand gc = BareCommand.parse("field 2 /k:v");
        FieldCommand cmd = new FieldCommand(gc);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ex.getMessage());
    }

    @Test
    void constructorWrongImperativeThrows() {
        // Build a Command with imperative != "field"
        BareCommand.BareCommandBuilder b = new BareCommand.BareCommandBuilder();
        b.setImperative("notfield");
        b.addParameter("1");
        b.setOption("k", "v");
        BareCommand c = b.build();

        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(c));
    }

    @Test
    void convenienceConstructorInvalidArgsThrow() {
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(-1, Map.of("k", "v")));
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(1, null));
        assertThrows(IllegalArgumentException.class, () -> new FieldCommand(1, Map.of()));
    }

    @Test
    void executeIndexZeroThrowsInvalidIndex() throws Exception {
        BareCommand gc = BareCommand.parse("field 0 /k:v");
        FieldCommand cmd = new FieldCommand(gc);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ex.getMessage());
    }

    @Test
    void executeMessageFormatsWithCommaAndOrder() throws Exception {
        Map<String, String> ordered = new LinkedHashMap<>();
        ordered.put("asset-class", "gold");
        ordered.put("company", "GS");
        FieldCommand cmd = new FieldCommand(1, ordered);

        String msg = cmd.execute(model).getFeedbackToUser();

        assertTrue(msg.startsWith("Added/updated field(s): "));
        assertTrue(msg.contains("asset-class:gold, company:GS"));
        assertTrue(msg.endsWith("for Alex Yeoh"));
    }

    @Test
    void executeBlankKeyThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("   ", "v"); // trims to empty -> validate fails
        FieldCommand cmd = new FieldCommand(1, pairs);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(MESSAGE_NAME_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void executeBlankValueThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("k", "   "); // trims to empty -> validate fails
        FieldCommand cmd = new FieldCommand(1, pairs);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void executeNullKeyNormalizesToEmptyThenThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put(null, "v"); // null -> normalizeKey returns ""
        FieldCommand cmd = new FieldCommand(1, pairs);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(MESSAGE_NAME_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void executeNullValueNormalizesToEmptyThenThrows() {
        Map<String, String> pairs = new LinkedHashMap<>();
        pairs.put("k", null); // null -> normalizeValue returns ""
        FieldCommand cmd = new FieldCommand(1, pairs);
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }
}

