package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
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
        input.put("company", "Goldman Sachs");
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), input, List.of());

        String feedback = cmd.execute(model).getFeedbackToUser();

        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
    }

    @Test
    void executeIndexOutOfBoundsThrows() throws Exception {
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(2), Map.of("k", "v"), List.of());
        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(model));
        assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ex.getMessage());
    }

    @Test
    void executeMessageFormatsWithCommaAndOrder() throws Exception {
        Map<String, String> ordered = new LinkedHashMap<>();
        ordered.put("asset-class", "gold");
        ordered.put("company", "GS");
        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), ordered, List.of());

        String msg = cmd.execute(model).getFeedbackToUser();

        assertTrue(msg.startsWith("Added/updated field(s): "));
        assertTrue(msg.contains("asset-class:gold, company:GS"));
        assertTrue(msg.endsWith("for Alex Yeoh"));
    }

    @Test
    void executeRemovesExistingField_success() throws Exception {
        Person withField = model.getFilteredPersonList().get(0)
                .withCustomFields(Map.of("company", "GS"));
        model.setPerson(model.getFilteredPersonList().get(0), withField);

        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), Map.of(), List.of("company"));

        String msg = cmd.execute(model).getFeedbackToUser();

        assertTrue(msg.contains("Removed field(s): company"));
        Person edited = model.getFilteredPersonList().get(0);
        assertTrue(edited.getCustomFields().isEmpty());
    }

    @Test
    void executeRemovalCaseMismatch_noChangeMessage() throws Exception {
        Person withField = model.getFilteredPersonList().get(0)
                .withCustomFields(Map.of("Intern", "Yes"));
        model.setPerson(model.getFilteredPersonList().get(0), withField);

        FieldCommand cmd = new FieldCommand(Index.fromOneBased(1), Map.of(), List.of("intern"));

        String msg = cmd.execute(model).getFeedbackToUser();

        assertEquals("No field changes applied for Alex Yeoh", msg);
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Yes", edited.getCustomFields().get("Intern"));
    }

    @Test
    void constructorNullIndexThrows() {
        Map<String, String> pairs = Map.of("k", "v");
        assertThrows(NullPointerException.class, () -> new FieldCommand(null, pairs, List.of()));
    }

    @Test
    void constructorNullPairsThrows() {
        assertThrows(NullPointerException.class, () -> new FieldCommand(Index.fromOneBased(1), null, List.of()));
    }

    @Test
    void constructorNullRemovalsThrows() {
        assertThrows(NullPointerException.class, () -> new FieldCommand(Index.fromOneBased(1), Map.of("k", "v"), null));
    }
}

