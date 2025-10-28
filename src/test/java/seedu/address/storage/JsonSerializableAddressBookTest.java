package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path WITH_LINKS_FILE =
            TEST_DATA_FOLDER.resolve("addressBookWithLinks.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalStateException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_withLinks_success() throws Exception {
        JsonSerializableAddressBook dataFromFile =
                JsonUtil.readJsonFile(WITH_LINKS_FILE, JsonSerializableAddressBook.class).get();

        AddressBook ab = dataFromFile.toModelType();

        // At least one person should have reconstructed links
        boolean someoneHasLinks = ab.getPersonList().stream().anyMatch(p -> !p.getLinks().isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(someoneHasLinks, "Expected at least one person to have links");

        // (Optional) Assert specific relationship exists if your fixture matches this:
        // Alice --lawyer--> Bob
        Map<String, Person> byName = ab.getPersonList().stream()
                .collect(java.util.stream.Collectors.toMap(p -> p.getName().toString(), p -> p));
        Person alice = byName.get("Alice");
        Person bob = byName.get("Bob");
        if (alice != null && bob != null) {
            boolean hasLawyerLink = alice.getLinks().stream()
                    .anyMatch(l -> l.getLinkName().equals("lawyer")
                            && l.getLinkee().getName().equals(bob.getName()));
            org.junit.jupiter.api.Assertions.assertTrue(hasLawyerLink, "Alice should be 'lawyer' of Bob");
        }
    }
}

