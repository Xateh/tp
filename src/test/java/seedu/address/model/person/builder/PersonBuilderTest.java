package seedu.address.model.person.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class PersonBuilderTest {

    // --- Test Data ---
    private static final Name DEFAULT_NAME = new Name("Alice Pauline");
    private static final Phone DEFAULT_PHONE = new Phone("94351253");
    private static final Email DEFAULT_EMAIL = new Email("alice@example.com");
    private static final Address DEFAULT_ADDRESS = new Address("123, Jurong West Ave 6, #08-111");
    private static final Set<Tag> DEFAULT_TAGS = Set.of(new Tag("friend"));
    private static final Map<String, String> DEFAULT_CUSTOM_FIELDS = Map.of("Nickname", "Ali");

    // --- Test Cases ---

    @Test
    public void constructor_default_buildSuccess() {
        // Build a person from scratch using the default constructor
        Person person = new PersonBuilder()
                .withName(DEFAULT_NAME)
                .withPhone(DEFAULT_PHONE)
                .withEmail(DEFAULT_EMAIL)
                .withAddress(DEFAULT_ADDRESS)
                .withTags(DEFAULT_TAGS)
                .withCustomFields(DEFAULT_CUSTOM_FIELDS)
                .build();

        // Create an expected person
        Person expectedPerson = new Person(
                DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_ADDRESS,
                DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS
        );

        assertEquals(expectedPerson, person);
    }

    @Test
    public void constructor_default_minimalFieldsBuildSuccess() {
        // Build a person with only required fields
        Person person = new PersonBuilder()
                .withName(DEFAULT_NAME)
                .withPhone(DEFAULT_PHONE)
                .withEmail(DEFAULT_EMAIL)
                .withAddress(DEFAULT_ADDRESS)
                .build();

        // Expect tags and custom fields to be empty
        Person expectedPerson = new Person(
                DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_ADDRESS,
                new HashSet<>(), new LinkedHashMap<>()
        );

        assertEquals(expectedPerson, person);
        assertEquals(new HashSet<>(), person.getTags());
        assertEquals(new LinkedHashMap<>(), person.getCustomFields());
    }

    @Test
    public void constructor_copyPerson_buildSuccess() {
        // Create a person to copy
        Person originalPerson = new Person(
                DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL, DEFAULT_ADDRESS,
                DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS
        );

        // Build a new person by copying the original
        Person copiedPerson = new PersonBuilder(originalPerson).build();

        assertEquals(originalPerson, copiedPerson);
    }

    @Test
    public void build_modifyName_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Name newName = new Name("Bob Builder");
        Person modifiedPerson = new PersonBuilder(originalPerson).withName(newName).build();

        Person expectedPerson = new Person(newName, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        assertEquals(expectedPerson, modifiedPerson);
    }

    @Test
    public void build_modifyPhone_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Phone newPhone = new Phone("98765432");
        Person modifiedPerson = new PersonBuilder(originalPerson).withPhone(newPhone).build();

        Person expectedPerson = new Person(DEFAULT_NAME, newPhone, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        assertEquals(expectedPerson, modifiedPerson);
    }

    @Test
    public void build_modifyEmail_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Email newEmail = new Email("bob@builder.com");
        Person modifiedPerson = new PersonBuilder(originalPerson).withEmail(newEmail).build();

        Person expectedPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, newEmail,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        assertEquals(expectedPerson, modifiedPerson);
    }

    @Test
    public void build_modifyAddress_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Address newAddress = new Address("456, Clementi Ave 2, #02-02");
        Person modifiedPerson = new PersonBuilder(originalPerson).withAddress(newAddress).build();

        Person expectedPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                newAddress, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        assertEquals(expectedPerson, modifiedPerson);
    }

    @Test
    public void build_modifyTags_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Set<Tag> newTags = Set.of(new Tag("colleague"), new Tag("urgent"));
        Person modifiedPerson = new PersonBuilder(originalPerson).withTags(newTags).build();

        Person expectedPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, newTags, DEFAULT_CUSTOM_FIELDS);
        assertEquals(expectedPerson, modifiedPerson);
    }

    @Test
    public void build_modifyCustomFields_success() {
        Person originalPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, DEFAULT_CUSTOM_FIELDS);
        Map<String, String> newCustomFields = Map.of("LinkedIn", "bob-builder", "Nickname", "B");
        Person modifiedPerson = new PersonBuilder(originalPerson).withCustomFields(newCustomFields).build();

        Person expectedPerson = new Person(DEFAULT_NAME, DEFAULT_PHONE, DEFAULT_EMAIL,
                DEFAULT_ADDRESS, DEFAULT_TAGS, newCustomFields);
        assertEquals(expectedPerson, modifiedPerson);
    }

    // --- Failure Cases ---

    @Test
    public void build_missingName_throwsIllegalStateException() {
        PersonBuilder builder = new PersonBuilder()
                .withPhone(DEFAULT_PHONE)
                .withEmail(DEFAULT_EMAIL)
                .withAddress(DEFAULT_ADDRESS);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void build_missingPhone_throwsIllegalStateException() {
        PersonBuilder builder = new PersonBuilder()
                .withName(DEFAULT_NAME)
                .withEmail(DEFAULT_EMAIL)
                .withAddress(DEFAULT_ADDRESS);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void build_missingEmail_throwsIllegalStateException() {
        PersonBuilder builder = new PersonBuilder()
                .withName(DEFAULT_NAME)
                .withPhone(DEFAULT_PHONE)
                .withAddress(DEFAULT_ADDRESS);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void build_missingAddress_throwsIllegalStateException() {
        PersonBuilder builder = new PersonBuilder()
                .withName(DEFAULT_NAME)
                .withPhone(DEFAULT_PHONE)
                .withEmail(DEFAULT_EMAIL);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void build_allFieldsMissing_throwsIllegalStateException() {
        PersonBuilder builder = new PersonBuilder();
        assertThrows(IllegalStateException.class, builder::build);
    }
}
