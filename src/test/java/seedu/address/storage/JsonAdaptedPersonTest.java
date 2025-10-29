package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Info;
import seedu.address.model.person.Link;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final List<JsonAdaptedLink> EMPTY_LINKS = Collections.emptyList();
    private static final String VALID_INFO = BENSON.getInfo().toString();

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType()); // BENSON should have no links in TypicalPersons
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                                                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                        VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_TAGS, EMPTY_LINKS, VALID_INFO);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                        invalidTags, EMPTY_LINKS, VALID_INFO);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_personWithLinks_deserializesWithoutResolving() throws Exception {
        // one outgoing link to someone-by-name (resolution happens later)
        List<JsonAdaptedLink> links = List.of(new JsonAdaptedLink("lawyer", "Bernice Yu"));

        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, links, VALID_INFO);

        // Should build a Person successfully; links are not materialized here
        assertDoesNotThrow(person::toModelType);
        assertEquals(0, person.toModelType().getLinks().size(),
                "Link resolution happens in JsonSerializableAddressBook (pass-2), not here.");
    }

    @Test
    public void toModelType_personWithInvalidLinkName_doesNotValidateHere() {
        // Bad linkName (validation deferred to Link construction in pass-2)
        List<JsonAdaptedLink> links = List.of(new JsonAdaptedLink("@@@", "Bernice Yu"));

        JsonAdaptedPerson person = new JsonAdaptedPerson(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, links, VALID_INFO);

        // This layer should NOT throw; invalid name will be caught when Links are constructed/resolved
        assertDoesNotThrow(person::toModelType);
    }

    // ===== Helpers for link tests =====
    private static Person mkPerson(String fullName, String phone, String email, String address) {
        return new Person(
                new Name(fullName),
                new Phone(phone),
                new Email(email),
                new Address(address),
                new HashSet<Tag>(),
                new java.util.LinkedHashMap<>(),
                new HashSet<Link>(),
                new Info("")
        );
    }

    // ===== resolveLinks() tests =====
    @Test
    public void resolveLinks_validSingleOutgoingLink_success() throws Exception {
        Person alice = mkPerson("Alice", "11111111", "alice@example.com", "1 Road");
        Person bob = mkPerson("Bob", "22222222", "bob@example.com", "2 Road");

        JsonAdaptedPerson japAlice = new JsonAdaptedPerson(
                "Alice", "11111111", "alice@example.com", "1 Road",
                VALID_TAGS,
                List.of(new JsonAdaptedLink("lawyer", "Bob")), VALID_INFO);

        Map<String, Person> byName = new HashMap<>();
        byName.put("Alice", alice);
        byName.put("Bob", bob);

        Set<Link> links = japAlice.resolveLinks(alice, byName::get);
        assertEquals(1, links.size());
        Link link = links.iterator().next();
        assertEquals("lawyer", link.getLinkName());
        assertEquals(alice.getName(), link.getLinker().getName());
        assertEquals(bob.getName(), link.getLinkee().getName());
    }

    @Test
    public void resolveLinks_ignoresInvalidLinkName() throws Exception {
        Person a = mkPerson("A", "11111111", "a@x.com", "addr");
        Person b = mkPerson("B", "22222222", "b@x.com", "addr");

        JsonAdaptedPerson japA = new JsonAdaptedPerson(
                "A", "11111111", "a@x.com", "addr",
                VALID_TAGS,
                List.of(new JsonAdaptedLink("@@@", "B")), VALID_INFO); // invalid per Link regex;

        Map<String, Person> byName = Map.of("A", a, "B", b);

        assertTrue(japA.resolveLinks(a, byName::get).isEmpty(), "Invalid link name should be ignored");
    }

    @Test
    public void resolveLinks_ignoresUnknownLinkee() throws Exception {
        Person a = mkPerson("A", "11111111", "a@x.com", "addr");

        JsonAdaptedPerson japA = new JsonAdaptedPerson(
                "A", "11111111", "a@x.com", "addr",
                VALID_TAGS,
                List.of(new JsonAdaptedLink("mentor", "Missing")), VALID_INFO);

        Map<String, Person> byName = Map.of("A", a);

        assertTrue(japA.resolveLinks(a, byName::get).isEmpty(), "Unresolvable linkee should be skipped");
    }

    @Test
    public void resolveLinks_ignoresSelfLink() throws Exception {
        Person a = mkPerson("A", "11111111", "a@x.com", "addr");

        JsonAdaptedPerson japA = new JsonAdaptedPerson(
                "A", "11111111", "a@x.com", "addr",
                VALID_TAGS,
                List.of(new JsonAdaptedLink("buddy", "A")), VALID_INFO); // self

        Map<String, Person> byName = Map.of("A", a);

        assertTrue(japA.resolveLinks(a, byName::get).isEmpty(), "Self-link should be ignored");
    }

    // ===== constructor(Person) link filtering =====
    @Test
    public void constructor_fromModel_serializesOnlyOutgoingLinks() throws Exception {
        Person alice = mkPerson("Alice", "11111111", "alice@example.com", "1 Road");
        Person bob = mkPerson("Bob", "22222222", "bob@example.com", "2 Road");

        Link ab = new Link(alice, bob, "colleague");

        // give both Persons the same Link instance (one is "incoming" for Bob)
        Set<Link> aliceLinks = new HashSet<>();
        aliceLinks.add(ab);
        Person aliceWith = new Person(
                alice.getName(), alice.getPhone(), alice.getEmail(), alice.getAddress(),
                alice.getTags(), alice.getCustomFields(), aliceLinks, new Info(""));

        Set<Link> bobLinks = new HashSet<>();
        bobLinks.add(ab);
        Person bobWith = new Person(
                bob.getName(), bob.getPhone(), bob.getEmail(), bob.getAddress(),
                bob.getTags(), bob.getCustomFields(), bobLinks, new Info(""));

        JsonAdaptedPerson japAlice = new JsonAdaptedPerson(aliceWith);
        JsonAdaptedPerson japBob = new JsonAdaptedPerson(bobWith);

        Map<String, Person> byName = new HashMap<>();
        byName.put("Alice", alice);
        byName.put("Bob", bob);

        // Alice should resolve one outgoing link to Bob
        Set<Link> aliceResolved = japAlice.resolveLinks(alice, byName::get);
        assertEquals(1, aliceResolved.size());
        Link aliceOut = aliceResolved.iterator().next();
        assertEquals("colleague", aliceOut.getLinkName());
        assertEquals("Bob", aliceOut.getLinkee().getName().fullName);

        // Bob should resolve none (his set contains only the incoming counterpart)
        Set<Link> bobResolved = japBob.resolveLinks(bob, byName::get);
        assertTrue(((java.util.Set<?>) bobResolved).isEmpty(),
                "Only outgoing links should be serialized and thus resolvable");
    }
}


