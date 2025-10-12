package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class JsonAdaptedPersonCustomFieldsTest {

    @TempDir Path temp;

    @Test
    void roundTrip_preservesCustomFieldsAndOrder() throws Exception {
        AddressBook book = new AddressBook();
        Map<String, String> cf = new LinkedHashMap<>();
        cf.put("asset-class", "gold");
        cf.put("company", "GS");

        Person p = new PersonBuilder().withName("Alex").build().withCustomFields(cf);
        book.addPerson(p);

        Path file = temp.resolve("ab.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(book);
        ReadOnlyAddressBook loaded = storage.readAddressBook(file).get();

        Person loadedP = loaded.getPersonList().get(0);
        assertEquals(cf, loadedP.getCustomFields());
        assertArrayEquals(cf.keySet().toArray(), loadedP.getCustomFields().keySet().toArray());
    }
}

