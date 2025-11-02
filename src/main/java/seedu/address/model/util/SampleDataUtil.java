package seedu.address.model.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Info;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        // Demo-focused sample persons: includes the three demo contacts (Alice, Bob, Cassandra)
        LinkedHashMap<String, String> aliceFields = new LinkedHashMap<>();
        aliceFields.put("assetClass", "Gold");
        aliceFields.put("notes", "Met at FinTech conf 2025");

        LinkedHashMap<String, String> bobFields = new LinkedHashMap<>();
        bobFields.put("assetClass", "Banking");

        LinkedHashMap<String, String> cassFields = new LinkedHashMap<>();
        cassFields.put("specialty", "Corporate Law");

        LinkedHashMap<String, String> farahFields = new LinkedHashMap<>();
        farahFields.put("firm", "KhanCo");
        farahFields.put("cert", "CPA");

        LinkedHashMap<String, String> georgeFields = new LinkedHashMap<>();
        georgeFields.put("bank", "FirstCapital");

        LinkedHashMap<String, String> hannahFields = new LinkedHashMap<>();
        hannahFields.put("license", "BrokerAX3");

        LinkedHashMap<String, String> jasmineFields = new LinkedHashMap<>();
        jasmineFields.put("title", "VPInvestments");

        LinkedHashMap<String, String> natalieFields = new LinkedHashMap<>();
        natalieFields.put("specialty", "StrategyConsulting");

        LinkedHashMap<String, String> oliverFields = new LinkedHashMap<>();
        oliverFields.put("company", "LedgerSupplies");

        return new Person[] {
            new Person(
                    new Name("Alice Tan"),
                    new Phone("90123456"),
                    new Email("alice@example.com"),
                    new Address("12 Orchard Road"),
                    getTagSet("client"),
                    aliceFields,
                    new HashSet<>(),
                    new Info("Met at FinTech conf 2025")
            ),
            new Person(
                    new Name("Bob Ong"),
                    new Phone("92233344"),
                    new Email("bob@example.com"),
                    new Address("5 Finance Ave"),
                    getTagSet("bank"),
                    bobFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Cassandra Law"),
                    new Phone("93344455"),
                    new Email("cass@example.com"),
                    new Address("33 Legal St"),
                    getTagSet("lawyer"),
                    cassFields,
                    new HashSet<>(),
                    new Info("")
            ),

            // A few familiar example contacts retained for context
            new Person(
                    new Name("Alex Yeoh"),
                    new Phone("87438807"),
                    new Email("alexyeoh@example.com"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                    getTagSet("friends"),
                    new LinkedHashMap<>(),
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Bernice Yu"),
                    new Phone("99272758"),
                    new Email("berniceyu@example.com"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                    getTagSet("colleagues", "friends"),
                    new LinkedHashMap<>(),
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Betsy Crowe"),
                    new Phone("1234567"),
                    new Email("betsycrowe@example.com"),
                    new Address("Newgate Prison"),
                    getTagSet("friend", "criminal"),
                    new LinkedHashMap<>(),
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Farah Khan"),
                    new Phone("98765432"),
                    new Email("farah@example.com"),
                    new Address("1 Business Rd"),
                    getTagSet("client"),
                    farahFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("George Smith"),
                    new Phone("87654321"),
                    new Email("george@example.com"),
                    new Address("2 Finance St"),
                    getTagSet("bank"),
                    georgeFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Hannah Lee"),
                    new Phone("76543210"),
                    new Email("hannah@example.com"),
                    new Address("3 Investment Ave"),
                    getTagSet("broker"),
                    hannahFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Jasmine Tan"),
                    new Phone("65432109"),
                    new Email("jasmine@example.com"),
                    new Address("4 Wealth St"),
                    getTagSet("investor"),
                    jasmineFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Natalie Wong"),
                    new Phone("54321098"),
                    new Email("natalie@example.com"),
                    new Address("5 Strategy Rd"),
                    getTagSet("consultant"),
                    natalieFields,
                    new HashSet<>(),
                    new Info("")
            ),
            new Person(
                    new Name("Oliver Twist"),
                    new Phone("43210987"),
                    new Email("oliver@example.com"),
                    new Address("6 Ledger St"),
                    getTagSet("business"),
                    oliverFields,
                    new HashSet<>(),
                    new Info("")
            )
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
