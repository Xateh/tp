package seedu.address.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        // -------- Pass 1: Build persons without links --------
        List<Person> builtPersons = new ArrayList<>(persons.size());
        for (JsonAdaptedPerson jap : persons) {
            Person person = jap.toModelType(); // Person with EMPTY links
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
            builtPersons.add(person);
        }

        // -------- Build name → Person lookup map (stable order) --------
        Map<String, Person> byName = builtPersons.stream()
                .collect(Collectors.toMap(
                        p -> p.getName().fullName,
                        p -> p,
                        (a, b) -> a, // keep the first on collision
                        LinkedHashMap::new
                ));

        // -------- Pass 2: Resolve links and replace Persons --------
        for (int i = 0; i < persons.size(); i++) {
            JsonAdaptedPerson jap = persons.get(i);
            Person base = builtPersons.get(i);

            // resolve outgoing links: base -> someone
            Set<Link> resolvedLinks = jap.resolveLinks(base, byName::get);

            if (!resolvedLinks.isEmpty()) {
                // 1) Update the linker with outgoing edges
                Set<Link> newLinksForBase = new java.util.HashSet<>(base.getLinks());
                newLinksForBase.addAll(resolvedLinks);
                Person withLinks = new Person(
                        base.getName(), base.getPhone(), base.getEmail(), base.getAddress(),
                        base.getTags(), base.getCustomFields(), newLinksForBase
                );
                addressBook.setPerson(base, withLinks);
                builtPersons.set(i, withLinks);
                byName.put(withLinks.getName().fullName, withLinks);

                // 2) Mirror for each linkee (in-memory only)
                for (Link out : resolvedLinks) {
                    Person linkee = out.getLinkee();
                    if (withLinks.isSamePerson(linkee)) continue;

                    Person currentLinkee = byName.get(linkee.getName().fullName);
                    if (currentLinkee == null) continue;

                    Set<Link> linkeeLinks = new java.util.HashSet<>(currentLinkee.getLinks());

                    // ✅ keep original direction
                    Link incoming = new Link(out.getLinker(), out.getLinkee(), out.getLinkName());

                    if (linkeeLinks.add(incoming)) {
                        Person updatedLinkee = new Person(
                                currentLinkee.getName(), currentLinkee.getPhone(), currentLinkee.getEmail(),
                                currentLinkee.getAddress(), currentLinkee.getTags(), currentLinkee.getCustomFields(),
                                linkeeLinks
                        );
                        addressBook.setPerson(currentLinkee, updatedLinkee);
                        byName.put(updatedLinkee.getName().fullName, updatedLinkee);

                        for (int k = 0; k < builtPersons.size(); k++) {
                            if (builtPersons.get(k).isSamePerson(currentLinkee)) {
                                builtPersons.set(k, updatedLinkee);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return addressBook;
    }
}