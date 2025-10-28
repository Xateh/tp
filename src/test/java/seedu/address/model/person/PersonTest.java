package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void getLinks_modify_throwsUnsupportedOperationException() {
        Person a = new Person(new Name("A"), new Phone("11111111"), new Email("a@ex.com"),
                new Address("addr"), new java.util.HashSet<>(),
                new java.util.LinkedHashMap<>(), new java.util.HashSet<>());

        Person b = new Person(new Name("B"), new Phone("22222222"), new Email("b@ex.com"),
                new Address("addr"), new java.util.HashSet<>(),
                new java.util.LinkedHashMap<>(), new java.util.HashSet<>());

        Link ab = new Link(a, b, "lawyer");

        // Start with person having a link
        java.util.HashSet<Link> links = new java.util.HashSet<>();
        links.add(ab);
        Person withLink = new Person(a.getName(), a.getPhone(), a.getEmail(), a.getAddress(),
                a.getTags(), a.getCustomFields(), links);

        assertThrows(UnsupportedOperationException.class, () -> withLink.getLinks().add(ab));
        assertThrows(UnsupportedOperationException.class, () -> withLink.getLinks().remove(ab));
    }

    @Test
    public void equals_differentLinks_returnsFalse() {
        // Base person (no links)
        Person a0 = new Person(new Name("A"), new Phone("11111111"), new Email("a@ex.com"),
                new Address("addr"), new java.util.HashSet<>(),
                new java.util.LinkedHashMap<>(), new java.util.HashSet<>());

        // Clone of A by value (still no links)
        Person a1 = new Person(a0.getName(), a0.getPhone(), a0.getEmail(), a0.getAddress(),
                a0.getTags(), a0.getCustomFields(), new java.util.HashSet<>());

        // A with one link
        Person b = new Person(new Name("B"), new Phone("22222222"), new Email("b@ex.com"),
                new Address("addr"), new java.util.HashSet<>(),
                new java.util.LinkedHashMap<>(), new java.util.HashSet<>());
        Link ab = new Link(a0, b, "lawyer");
        java.util.HashSet<Link> links = new java.util.HashSet<>();
        links.add(ab);
        Person aWithLink = new Person(a0.getName(), a0.getPhone(), a0.getEmail(), a0.getAddress(),
                a0.getTags(), a0.getCustomFields(), links);

        assertTrue(a0.equals(a1)); // identical except links
        assertFalse(a0.equals(aWithLink)); // links differ
        assertFalse(aWithLink.equals(a0)); // symmetric
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", info=}";
        assertEquals(expected, ALICE.toString());
    }
}

