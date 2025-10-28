package seedu.address.model.person.builder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Info;
import seedu.address.model.person.Link;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * A builder for the {@link Person} class. Follows the builder pattern to facilitate creating or modifying Person
 * objects. This is especially useful for commands that modify a person, as it avoids reconstructing the person from
 * scratch.
 */
public class PersonBuilder {

    // Required fields
    private Name name;
    private Phone phone;
    private Email email;
    private Address address;

    // Optional fields with defaults
    private Set<Tag> tags;
    private Map<String, String> customFields;
    private Set<Link> links;
    private Info info;

    /**
     * Creates a {@code PersonBuilder} with default empty fields. Tags, custom fields and links are initialized to empty
     * collections. Info is initialized with the empty string.
     */
    public PersonBuilder() {
        tags = new HashSet<>();
        customFields = new LinkedHashMap<>();
        links = new HashSet<>();
        info = new Info("");
    }

    /**
     * Initializes the PersonBuilder with the data of an existing {@code personToCopy}. This allows for easy
     * modification of a person.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        // Create mutable copies of the collections
        tags = new HashSet<>(personToCopy.getTags());
        customFields = new LinkedHashMap<>(personToCopy.getCustomFields());
        links = new HashSet<>(personToCopy.getLinks());
        info = personToCopy.getInfo();
    }

    /** Sets the {@code Name} of the {@code Person} we are building. */
    public PersonBuilder withName(Name name) {
        this.name = name;
        return this;
    }

    /** Sets the {@code Phone} of the {@code Person} we are building. */
    public PersonBuilder withPhone(Phone phone) {
        this.phone = phone;
        return this;
    }

    /** Sets the {@code Email} of the {@code Person} we are building. */
    public PersonBuilder withEmail(Email email) {
        this.email = email;
        return this;
    }

    /** Sets the {@code Address} of the {@code Person} we are building. */
    public PersonBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    /**
     * Sets the {@code Set<Tag>} of the {@code Person} we are building. A new {@link HashSet} is created to ensure the
     * builder's state is independent of the provided set.
     */
    public PersonBuilder withTags(Set<Tag> tags) {
        this.tags = new HashSet<>(tags);
        return this;
    }

    /**
     * Sets the custom fields {@code Map<String, String>} of the {@code Person} we are building. A new
     * {@link LinkedHashMap} is created to preserve order and ensure the builder's state is independent of the provided
     * map.
     */
    public PersonBuilder withCustomFields(Map<String, String> customFields) {
        this.customFields = new LinkedHashMap<>(customFields);
        return this;
    }

    /**
     * Sets the {@code Set<Link>} of the {@code Person} we are building. A new {@link HashSet} is created to ensure the
     * builder's state is independent of the provided set.
     */
    public PersonBuilder withLinks(Set<Link> links) {
        this.links = new HashSet<>(links);
        return this;
    }

    /**
     * Sets the info {@code Info} of the {@code Person} we are building.
     * @return This builder instance for fluent chaining.
     */
    public PersonBuilder withInfo(Info info) {
        this.info = info;
        return this;
    }

    /**
     * Creates and returns the new {@link Person} object based on the fields set in this builder.
     *
     * @return The constructed {@link Person}.
     * @throws IllegalStateException if any required fields (name, phone, email, address) have not been set (are null).
     */
    public Person build() {
        if (name == null || phone == null || email == null || address == null) {
            throw new IllegalStateException(
                    "A required field (name, phone, email, or address) has not been set.");
        }

        // Use the full constructor that includes customFields and links.
        return new Person(name, phone, email, address, tags, customFields, links, info);
    }
}
