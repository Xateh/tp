package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class LinkTest {

    // ---- Helpers ------------------------------------------------------------

    private static Person mkPerson(String fullName, String phone, String email, String address) {
        return new Person(
                new Name(fullName),
                new Phone(phone),
                new Email(email),
                new Address(address),
                new HashSet<Tag>(),
                new LinkedHashMap<String, String>(),
                new HashSet<Link>()
        );
    }

    // ---- isValidLinkName ----------------------------------------------------

    @Test
    public void isValidLinkName_valid_returnsTrue() {
        assertTrue(Link.isValidLinkName("lawyer"));
        assertTrue(Link.isValidLinkName("L")); // min length 1
        assertTrue(Link.isValidLinkName("co-founder")); // hyphen
        assertTrue(Link.isValidLinkName("team mate")); // space
        assertTrue(Link.isValidLinkName("R&D")); // ampersand
        assertTrue(Link.isValidLinkName("PM/PO")); // slash
        assertTrue(Link.isValidLinkName("Dr. (attending)")); // dot + parentheses
        assertTrue(Link.isValidLinkName("A_1")); // underscore + digit
        assertTrue(Link.isValidLinkName("012345678901234567890123456789")); // 30 chars
    }

    @Test
    public void isValidLinkName_invalid_returnsFalseOrThrows() {
        // null -> NPE by contract
        assertThrows(NullPointerException.class, () -> Link.isValidLinkName(null));

        // empty or blank
        assertFalse(Link.isValidLinkName(""));
        assertFalse(Link.isValidLinkName(" "));

        // leading symbol/space not allowed (must start with alphanumeric)
        assertFalse(Link.isValidLinkName("-lead"));
        assertFalse(Link.isValidLinkName(" lead"));

        // forbidden character (@ not in allowed list)
        assertFalse(Link.isValidLinkName("alpha@beta"));

        // over 30 chars (31)
        assertFalse(Link.isValidLinkName("0123456789012345678901234567890"));

        // newline / control chars
        assertFalse(Link.isValidLinkName("mentor\ncoach"));
    }

    // ---- Constructor validations -------------------------------------------

    @Test
    public void constructor_nulls_throwsNullPointerException() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");

        assertThrows(NullPointerException.class, () -> new Link(null, b, "lawyer"));
        assertThrows(NullPointerException.class, () -> new Link(a, null, "lawyer"));
        assertThrows(NullPointerException.class, () -> new Link(a, b, null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");

        assertThrows(IllegalArgumentException.class, () -> new Link(a, b, ""));
        assertThrows(IllegalArgumentException.class, () -> new Link(a, b, "-role"));
        assertThrows(IllegalArgumentException.class, () -> new Link(a, b, "alpha@beta"));
    }

    @Test
    public void constructor_samePersons_throwsIllegalArgumentException() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        // same person on both ends is disallowed
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new Link(a, a, "lawyer"));
    }

    // ---- equals / hashCode --------------------------------------------------

    @Test
    public void equals_sameFields_true() {
        Person a1 = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b1 = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");

        // Different instances but same names — equals() compares by names + linkName
        Person a2 = mkPerson("Alex Yeoh", "00000000", "ax@ex.com", "other"); // details differ, name same
        Person b2 = mkPerson("Bernice Yu", "11111111", "by@ex.com", "other"); // details differ, name same

        Link l1 = new Link(a1, b1, "lawyer");
        Link l2 = new Link(a2, b2, "lawyer");

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    @Test
    public void equals_directionMatters_false() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");

        Link forward = new Link(a, b, "lawyer");
        Link reversed = new Link(b, a, "lawyer");

        assertNotEquals(forward, reversed);
    }

    @Test
    public void equals_differentName_false() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");
        Person c = mkPerson("Charlotte Oliveiro", "93210283", "charlotte@example.com", "addr");

        Link ab = new Link(a, b, "lawyer");
        Link ac = new Link(a, c, "lawyer");

        assertNotEquals(ab, ac);
    }

    @Test
    public void equals_differentLinkLabel_false() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");

        Link lawyer = new Link(a, b, "lawyer");
        Link mentor = new Link(a, b, "mentor");

        assertNotEquals(lawyer, mentor);
    }

    // ---- toString -----------------------------------------------------------

    @Test
    public void toString_containsPieces() {
        Person a = mkPerson("Alex Yeoh", "87438807", "alex@example.com", "addr");
        Person b = mkPerson("Bernice Yu", "99272758", "bernice@example.com", "addr");
        Link l = new Link(a, b, "lawyer");

        String s = l.toString();
        assertTrue(s.contains("lawyer"));
        assertTrue(s.contains("Alex Yeoh"));
        assertTrue(s.contains("Bernice Yu"));
        assertTrue(s.contains("->")); // the arrow in the format
    }
}
