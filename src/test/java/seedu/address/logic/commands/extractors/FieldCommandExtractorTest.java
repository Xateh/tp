package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FieldCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class FieldCommandExtractorTest {

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        Person base = new PersonBuilder().withName("Alex Yeoh").build();
        model.addPerson(base);
    }

    @Test
    void extract_validCommand_success() throws Exception {
        BareCommand bare = BareCommand.parse("field 1 /company:\"Goldman Sachs\"");
        FieldCommand command = FieldCommandExtractor.extract(bare);

        String feedback = command.execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
    }

    @Test
    void extract_missingIndex_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field /company:\"Goldman Sachs\"");
        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals("Missing index. Usage: field <index> /key:value ...", ex.getMessage());
    }

    @Test
    void extract_invalidIndex_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field x /k:v");
        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals("Invalid index. Must be a positive integer.", ex.getMessage());
    }

    @Test
    void extract_noPairs_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field 1");
        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals("Provide at least one /key:value pair. Usage: field <index> /key:value ...", ex.getMessage());
    }

    @Test
    void extract_wrongImperative_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("notfield");
        builder.addParameter("1");
        builder.setOption("k", "v");
        BareCommand bare = builder.build();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals("Wrong imperative for FieldCommand", ex.getMessage());
    }
}
