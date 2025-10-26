package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.extractors.AddCommandExtractor.OPTION_KEY_TAG;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind.ADDITIVE;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.BareCommand.BareCommandBuilder;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.builder.PersonBuilder;
import seedu.address.model.tag.Tag;

/**
 * Contains tests for {@code AddCommandExtractor}. This tests the extractor's ability to validate and parse positional
 * arguments from a {@code BareCommand} into a fully constructed {@code AddCommand}.
 */
public class AddCommandExtractorTest {

    // --- Test Data Constants ---
    private static final String VALID_NAME = "John Doe";
    private static final String VALID_PHONE = "87654321";
    private static final String VALID_ADDRESS = "123 Main Street";
    private static final String VALID_EMAIL = "john@example.com";
    private static final String VALID_TAG1 = "friend";
    private static final String VALID_TAG2 = "colleague";

    // Based on the given restrictions
    private static final String INVALID_NAME_EMPTY = "";
    private static final String INVALID_PHONE_SHORT = "12";
    private static final String INVALID_PHONE_LETTERS = "abcdefgh";
    private static final String INVALID_ADDRESS_EMPTY = "";
    private static final String INVALID_EMAIL_NO_DOMAIN = "john@d";

    // --- Positive Test Cases ---

    @Test
    public void extract_validArgs_returnsAddCommand() throws ValidationException {
        // --- Case 1: All valid parameters ---
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .build();

        Person expectedPerson = new PersonBuilder()
                .withName(new Name(VALID_NAME))
                .withPhone(new Phone(VALID_PHONE))
                .withAddress(new Address(VALID_ADDRESS))
                .withEmail(new Email(VALID_EMAIL))
                .build();

        assertEquals(new AddCommand(expectedPerson), AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_validArgsWithTags_returnsAddCommand() throws ValidationException {
        // --- Case 2: All valid parameters ---
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .setOption(OPTION_KEY_TAG, VALID_TAG1)
                .setOption(OPTION_KEY_TAG, VALID_TAG2)
                .build();

        Person expectedPerson = new PersonBuilder()
                .withName(new Name(VALID_NAME))
                .withPhone(new Phone(VALID_PHONE))
                .withAddress(new Address(VALID_ADDRESS))
                .withEmail(new Email(VALID_EMAIL))
                .withTags(Set.of(new Tag(VALID_TAG1), new Tag(VALID_TAG2)))
                .build();

        assertEquals(new AddCommand(expectedPerson), AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_extraValidArgs_returnsAddCommand() throws ValidationException {
        // --- Case 3: Extra parameters are ignored ---
        // The extractor only validates up to index 3.
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .addParameter("extra info") // This should be ignored
                .build();

        Person expectedPerson = new PersonBuilder()
                .withName(new Name(VALID_NAME))
                .withPhone(new Phone(VALID_PHONE))
                .withAddress(new Address(VALID_ADDRESS))
                .withEmail(new Email(VALID_EMAIL))
                .build();

        assertEquals(new AddCommand(expectedPerson), AddCommandExtractor.extract(cmd));
    }

    // --- Negative Test Cases ---

    @Test
    public void extract_missingParameters_throwsValidationException() {
        // --- Case 1: No parameters ---
        // Fails on Validation.validateParameter(bareCommand, 0, NORMAL)
        BareCommand cmd0 = new BareCommandBuilder().setImperative("add").build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd0));

        // --- Case 2: One parameter ---
        // Fails on Validation.validateParameter(bareCommand, 1, NORMAL)
        BareCommand cmd1 = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd1));

        // --- Case 3: Two parameters ---
        // Fails on Validation.validateParameter(bareCommand, 2, NORMAL)
        BareCommand cmd2 = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd2));

        // --- Case 4: Three parameters ---
        // Fails on Validation.validateParameter(bareCommand, 3, NORMAL)
        BareCommand cmd3 = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd3));
    }

    @Test
    public void extract_invalidParameterKind_throwsValidationException() {
        // Fails on Validation.validateParameter(bareCommand, 0, NORMAL)
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(ADDITIVE, VALID_NAME) // Not NORMAL
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_invalidName_throwsValidationException() {
        // Fails on Validation.validateName(...)
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(INVALID_NAME_EMPTY)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_invalidPhone_throwsValidationException() {
        // --- Case 1: Too short ---
        // Fails on Validation.validatePhone(...)
        BareCommand cmdShort = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(INVALID_PHONE_SHORT)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmdShort));

        // --- Case 2: Contains letters ---
        // Fails on Validation.validatePhone(...)
        BareCommand cmdLetters = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(INVALID_PHONE_LETTERS)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmdLetters));
    }

    @Test
    public void extract_invalidAddress_throwsValidationException() {
        // Fails on Validation.validateAddress(...)
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(INVALID_ADDRESS_EMPTY)
                .addParameter(VALID_EMAIL)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_invalidEmail_throwsValidationException() {
        // Fails on Validation.validateEmail(...)
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(INVALID_EMAIL_NO_DOMAIN)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd));
    }

    @Test
    public void extract_invalidTags_throwsValidationException() {
        // Fails on Validation.validateTags(...)
        BareCommand cmd = new BareCommandBuilder()
                .setImperative("add")
                .addParameter(VALID_NAME)
                .addParameter(VALID_PHONE)
                .addParameter(VALID_ADDRESS)
                .addParameter(VALID_EMAIL)
                .setOption(OPTION_KEY_TAG)
                .build();
        assertThrows(ValidationException.class, () -> AddCommandExtractor.extract(cmd));
    }
}