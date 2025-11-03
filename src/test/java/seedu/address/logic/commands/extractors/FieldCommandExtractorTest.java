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
    void extract_trimsKeysAndValues_success() throws Exception {
        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption(" company ", " Goldman Sachs ");
        BareCommand bare = builder.build();

        FieldCommand command = FieldCommandExtractor.extract(bare);

        String feedback = command.execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("company:Goldman Sachs"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("Goldman Sachs", edited.getCustomFields().get("company"));
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
    void extract_disallowedLowercaseKey_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field 1 /name:value");

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(String.format(FieldCommand.MESSAGE_DISALLOWED_FIELD_NAME, "name"), ex.getMessage());
    }

    @Test
    void extract_disallowedMixedCaseKey_throwsValidationException() throws Exception {
        BareCommand bare = BareCommand.parse("field 1 /Email:value");

        ValidationException ex = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(bare));
        assertEquals(String.format(FieldCommand.MESSAGE_DISALLOWED_FIELD_NAME, "Email"), ex.getMessage());
    }

    @Test
    void extract_disallowedLinkReservedKeys_throwsValidationException() throws Exception {
        BareCommand toBare = BareCommand.parse("field 1 /to:value");
        ValidationException toEx = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(toBare));
        assertEquals(String.format(FieldCommand.MESSAGE_DISALLOWED_FIELD_NAME, "to"), toEx.getMessage());

        BareCommand fromBare = BareCommand.parse("field 1 /from:value");
        ValidationException fromEx = assertThrows(ValidationException.class, () ->
                FieldCommandExtractor.extract(fromBare));
        assertEquals(String.format(FieldCommand.MESSAGE_DISALLOWED_FIELD_NAME, "from"), fromEx.getMessage());
    }

    @Test
    void extract_findKey_success() throws Exception {
        BareCommand bare = BareCommand.parse("field 1 /Find:value");

        FieldCommand command = FieldCommandExtractor.extract(bare);

        String feedback = command.execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("Find:value"));
        Person edited = model.getFilteredPersonList().get(0);
        assertEquals("value", edited.getCustomFields().get("Find"));
    }

    @Test
    void extract_blankValue_throwsValidationException() throws Exception {
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
    void extract_missingOptionValue_removesField() throws Exception {
        Person withField = model.getFilteredPersonList().get(0)
                .withCustomFields(java.util.Map.of("company", "Existing"));
        model.setPerson(model.getFilteredPersonList().get(0), withField);

        BareCommand.BareCommandBuilder builder = new BareCommand.BareCommandBuilder();
        builder.setImperative("field");
        builder.addParameter("1");
        builder.setOption("company");
        BareCommand bare = builder.build();

        FieldCommand command = FieldCommandExtractor.extract(bare);

        String feedback = command.execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("Removed field(s): company"));
        Person edited = model.getFilteredPersonList().get(0);
        assertTrue(edited.getCustomFields().isEmpty());
    }

    @Test
    void extract_nullOptionValue_throwsValidationException() throws Exception {
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
