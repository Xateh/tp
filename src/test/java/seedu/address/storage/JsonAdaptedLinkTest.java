package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Info;
import seedu.address.model.person.Link;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Tests for {@link JsonAdaptedLink}.
 */
public class JsonAdaptedLinkTest {

    // --- Minimal person builders (avoid testutil deps here) ---
    private static Person person(String fullName) {
        return new Person(
                new Name(fullName),
                new Phone("99999999"),
                new Email(fullName.toLowerCase().replace(" ", "") + "@example.com"),
                new Address("Somewhere"),
                new HashSet<>(),
                new LinkedHashMap<>(),
                new HashSet<>(),
                new Info("")
        );
    }

    @Test
    public void toModelType_valid_success() throws Exception {
        Person alice = person("Alice");
        Person bob = person("Bob");

        JsonAdaptedLink adapted = new JsonAdaptedLink("lawyer", "Bob");
        Link model = adapted.toModelType(alice, bob);

        assertEquals("lawyer", model.getLinkName());
        assertEquals(alice.getName(), model.getLinker().getName());
        assertEquals(bob.getName(), model.getLinkee().getName());
    }

    @Test
    public void toModelType_invalidLinkName_throwsIllegalValueException() {
        Person a = person("A");
        Person b = person("B");

        // "!!!" violates Link.VALIDATION_REGEX
        JsonAdaptedLink adapted = new JsonAdaptedLink("!!!", "B");
        assertThrows(IllegalValueException.class, () -> adapted.toModelType(a, b));
    }

    @Test
    public void toModelType_nullLinkName_throwsNullPointerException() {
        Person a = person("A");
        Person b = person("B");

        JsonAdaptedLink adapted = new JsonAdaptedLink(null, "B");
        // Link.isValidLinkName(null) -> requireNonNull -> NPE
        assertThrows(NullPointerException.class, () -> adapted.toModelType(a, b));
    }

    @Test
    public void constructor_fromModel_correctFields() {
        Person a = person("Carol");
        Person b = person("Dan");
        Link link = new Link(a, b, "advisor");

        JsonAdaptedLink adapted = new JsonAdaptedLink(link);
        assertEquals("advisor", adapted.getLinkName());
        assertEquals("Dan", adapted.getLinkeeName());
    }
}
