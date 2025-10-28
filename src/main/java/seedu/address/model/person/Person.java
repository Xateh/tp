package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Map<String, String> customFields;
    private final Set<Link> links = new HashSet<>();
    private final Info info;

    /**
     * Full constructor including custom fields.
     */
    public Person(Name name, Phone phone, Email email, Address address,
                  Set<Tag> tags, Map<String, String> customFields, Set<Link> links, Info info) {
        requireAllNonNull(name, phone, email, address, tags, customFields, links, info);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        // Preserve order and make defensive copy
        this.customFields = new LinkedHashMap<>(customFields);
        this.links.addAll(links);
        this.info = info;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns the person's info
     */
    public Info getInfo() {
        return info;
    }

    /**
     * Returns an unmodifiable view of custom fields.
     */
    public Map<String, String> getCustomFields() {
        return Collections.unmodifiableMap(customFields);
    }

    /**
     * Returns all links associated to the person;
     */
    public Set<Link> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    /**
     * Returns a new {@code Person} identical to this, but with the provided custom fields.
     * The provided map is copied defensively and iteration order is preserved.
     */
    public Person withCustomFields(Map<String, String> fields) {
        return new Person(name, phone, email, address, tags, new LinkedHashMap<>(fields), links, info);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && customFields.equals(otherPerson.customFields)
                && links.equals(otherPerson.links)
                && info.equals(otherPerson.info);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, customFields, links, info);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("info", info)
                .toString();
    }

}
