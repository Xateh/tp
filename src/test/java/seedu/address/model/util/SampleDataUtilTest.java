package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;

/**
 * Unit tests for {@link SampleDataUtil} ensuring the sample persons and seeded links
 * match the expected demo dataset introduced in the PR.
 */
public class SampleDataUtilTest {

    private static Person findByName(ReadOnlyAddressBook ab, String fullName) {
        return ab.getPersonList().stream()
                .filter(p -> p.getName().fullName.equals(fullName))
                .findFirst()
                .orElse(null);
    }

    @Test
    public void getSamplePersons_containsCoreDemoContactsAndFields() {
        Person[] persons = SampleDataUtil.getSamplePersons();
        // basic presence
        assertTrue(Arrays.stream(persons).anyMatch(p -> p.getName().fullName.equals("Alice Tan")));
        assertTrue(Arrays.stream(persons).anyMatch(p -> p.getName().fullName.equals("Bob Ong")));
        assertTrue(Arrays.stream(persons).anyMatch(p -> p.getName().fullName.equals("Cassandra Law")));

        // verify custom fields and info for Alice
        Person alice = Arrays.stream(persons)
                .filter(p -> p.getName().fullName.equals("Alice Tan"))
                .findFirst()
                .orElse(null);
        assertNotNull(alice);
        Map<String, String> aliceFields = alice.getCustomFields();
        assertEquals("Gold", aliceFields.get("assetClass"));
        assertEquals("Met at FinTech conf 2025", alice.getInfo().toString());

        // verify Farah's custom fields
        Person farah = Arrays.stream(persons)
                .filter(p -> p.getName().fullName.equals("Farah Khan"))
                .findFirst()
                .orElse(null);
        assertNotNull(farah);
        Map<String, String> farahFields = farah.getCustomFields();
        assertEquals("KhanCo", farahFields.get("firm"));
        assertEquals("CPA", farahFields.get("cert"));
    }

    @Test
    public void getSampleAddressBook_seedsDirectedLinks() {
        ReadOnlyAddressBook ab = SampleDataUtil.getSampleAddressBook();

        // Note: debug printing removed to keep tests quiet. Use a debugger or add
        // targeted assertions/logging if further inspection of the address book is needed.

        Person cassandra = findByName(ab, "Cassandra Law");
        Person alice = findByName(ab, "Alice Tan");
        Person farah = findByName(ab, "Farah Khan");
        Person george = findByName(ab, "George Smith");
        Person bob = findByName(ab, "Bob Ong");
        Person hannah = findByName(ab, "Hannah Lee");
        Person jasmine = findByName(ab, "Jasmine Tan");
        Person alex = findByName(ab, "Alex Yeoh");
        Person bernice = findByName(ab, "Bernice Yu");

        // basic sanity
        assertNotNull(cassandra);
        assertNotNull(alice);
        assertNotNull(farah);

        // Verify that the linkers have their outgoing links
        assertTrue(cassandra.getLinks().contains(new Link(cassandra, alice, "lawyer")),
                "Cassandra outgoing links: " + cassandra.getLinks());
        assertTrue(farah.getLinks().contains(new Link(farah, alice, "accountant")),
                "Farah outgoing links: " + farah.getLinks());
        assertTrue(george.getLinks().contains(new Link(george, bob, "banker")),
                "George outgoing links: " + george.getLinks());
        assertTrue(hannah.getLinks().contains(new Link(hannah, jasmine, "broker")),
                "Hannah outgoing links: " + hannah.getLinks());
        assertTrue(alex.getLinks().contains(new Link(alex, bernice, "colleague")),
                "Alex outgoing links: " + alex.getLinks());

        // Verify that the linkees contain the expected links.
        // Current seeding replaces linkee's links per seeding order.
        // According to the sample seeding order, Alice should contain the accountant link (Farah -> Alice)
        assertTrue(alice.getLinks().contains(new Link(farah, alice, "accountant")),
                "Alice links: " + alice.getLinks());
        // Bob, Jasmine and Bernice should contain their incoming links
        assertTrue(bob.getLinks().contains(new Link(george, bob, "banker")),
                "Bob links: " + bob.getLinks());
        assertTrue(jasmine.getLinks().contains(new Link(hannah, jasmine, "broker")),
                "Jasmine links: " + jasmine.getLinks());
        assertTrue(bernice.getLinks().contains(new Link(alex, bernice, "colleague")),
                "Bernice links: " + bernice.getLinks());
    }

}
