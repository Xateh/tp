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
    void extract_duplicateOptionValues_generatesWarning() throws Exception {
        // build a bare command with duplicate option values for same key
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("company", "A");
        builder.setOption("company", "B");
        BareCommand bare = builder.build();

        FieldCommand command = FieldCommandExtractor.extract(bare);
        seedu.address.logic.commands.CommandResult result = command.execute(model);
        // first value should be used
        seedu.address.model.person.Person edited = model.getFilteredPersonList().get(0);
        assertEquals("A", edited.getCustomFields().get("company"));
        // warnings should be present
        assertEquals(1, result.getWarnings().size());
    }

    @Test
    void extract_invalidIndex_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field x /k:v");
        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(String.format(Validation.MESSAGE_INDEX_FAILED_TO_PARSE, "x"), ex.getMessage());
    }

    @Test
    void extract_noPairs_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field 1");
        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(FieldCommand.MESSAGE_AT_LEAST_ONE_PAIR, ex.getMessage());
    }

    @Test
    void extract_blankKey_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("   ", "value");
        BareCommand bare = builder.build();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(FieldCommand.MESSAGE_NAME_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void extract_blankValue_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("company", "   ");
        BareCommand bare = builder.build();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void extract_missingOptionValue_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("company");
        BareCommand bare = builder.build();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }

    @Test
    void extract_nullOptionValue_throwsValidationException() {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("company", null);
        BareCommand bare = builder.build();

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(FieldCommand.MESSAGE_VALUE_CANNOT_BE_BLANK, ex.getMessage());
    }
}
