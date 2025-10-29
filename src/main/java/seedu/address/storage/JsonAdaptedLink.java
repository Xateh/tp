package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;

/**
 * Jackson-friendly version of {@link Link}.
 * <p>
 * This class provides the intermediate representation of a {@code Link} object for JSON
 * serialization and deserialization. It stores only lightweight data fields
 * (e.g., {@code linkName} and {@code linkeeName}) rather than full {@link Person} references,
 * since Person objects may not yet be constructed during deserialization.
 * </p>
 *
 * <p>
 * The actual {@link Link} objects can be reconstructed later during a second-pass
 * resolution phase (e.g., in {@link JsonAdaptedPerson#resolveLinks(Person, java.util.function.Function)})
 * once all {@link Person} instances are available.
 * </p>
 */
public class JsonAdaptedLink {

    private final String linkName;
    private final String linkeeName; // Simplest stable identifier for now

    /**
     * Constructs a {@code JsonAdaptedLink} with the given {@code linkName} and {@code linkeeName}.
     * This constructor is used by Jackson during deserialization.
     *
     * @param linkName   The label of the relationship (e.g., "lawyer", "friend").
     * @param linkeeName The name of the person that this link points to.
     */
    @JsonCreator
    public JsonAdaptedLink(@JsonProperty("linkName") String linkName,
                           @JsonProperty("linkeeName") String linkeeName) {
        this.linkName = linkName;
        this.linkeeName = linkeeName;
    }

    /**
     * Converts a given {@code Link} model object into this Jackson-friendly form
     * for JSON serialization.
     *
     * @param source The {@code Link} to convert.
     */
    public JsonAdaptedLink(Link source) {
        this.linkName = source.getLinkName();
        this.linkeeName = source.getLinkee().getName().fullName;
    }

    /**
     * Returns the name of the link (relationship label).
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * Returns the name of the linked person (linkee).
     */
    public String getLinkeeName() {
        return linkeeName;
    }

    /**
     * Converts this Jackson-friendly adapted link object into the model's {@code Link} object.
     * <p>
     * This method assumes that both {@code linker} and {@code linkee} {@link Person}
     * objects are already constructed and available. Typically, this is invoked
     * in a second deserialization pass once the full person list is built.
     * </p>
     *
     * @param linker The person initiating the link (the "source" of the relationship).
     * @param linkee The person receiving the link (the "target" of the relationship).
     * @return A new {@code Link} model object.
     * @throws IllegalValueException If the {@code linkName} is invalid
     *         according to {@link Link#isValidLinkName(String)}.
     */
    public Link toModelType(Person linker, Person linkee) throws IllegalValueException {
        if (!Link.isValidLinkName(linkName)) {
            throw new IllegalValueException(Link.MESSAGE_CONSTRAINTS);
        }
        return new Link(linker, linkee, linkName);
    }
}
