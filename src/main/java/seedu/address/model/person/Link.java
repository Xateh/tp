package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents a named relationship between two {@link Person} objects in the address book.
 * <p>
 * A {@code Link} models a directed relationship of the form:
 * <pre>
 *     linker --(linkName)--> linkee
 * </pre>
 * For example, if the command {@code link 1 lawyer 2} is executed,
 * this class represents "Person 1 is the <em>lawyer</em> of Person 2".
 * </p>
 *
 * <p>
 * Links are:
 * <ul>
 *   <li>Immutable — all fields are {@code final}.</li>
 *   <li>Validated — {@code linkName} must satisfy {@link #isValidLinkName(String)}.</li>
 *   <li>Directed — the link is one-way; the reverse relationship is not automatically added.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Guarantees:
 * <ul>
 *   <li>{@code linker != linkee}</li>
 *   <li>{@code linkName} follows {@link #VALIDATION_REGEX}</li>
 *   <li>Fields are non-null</li>
 * </ul>
 * </p>
 */
public class Link {

    /** Constraint message displayed when a link name fails validation. */
    public static final String MESSAGE_CONSTRAINTS =
            "Link name should be 1–30 characters long and "
                    + "contain only letters, digits, spaces, and these symbols: - _ & / ( ) .";

    /**
     * Regular expression defining a valid link name:
     * <ul>
     *   <li>Must start with an alphanumeric character (no leading space/symbol)</li>
     *   <li>May contain letters, digits, spaces, and limited punctuation (- _ # & / ( ) .)</li>
     *   <li>Maximum length: 30 characters</li>
     * </ul>
     */
    private static final String VALIDATION_REGEX = "[A-Za-z0-9][A-Za-z0-9 _#&/().-]{0,29}";

    private final Person linker; // The person that initiates the relationship
    private final Person linkee; // The person that is the target of the relationship
    private final String linkName; // The label of the relationship (e.g. "lawyer", "friend")

    /**
     * Constructs a {@code Link}.
     *
     * @param linker   The person initiating the link (source).
     * @param linkee   The person receiving the link (target).
     * @param linkName The descriptive label of the relationship.
     * @throws IllegalArgumentException If any argument is null,
     *                                  if {@code linker.equals(linkee)}, or
     *                                  if {@code linkName} is invalid according to {@link #isValidLinkName(String)}.
     */
    public Link(Person linker, Person linkee, String linkName) {
        requireAllNonNull(linker, linkee, linkName);
        checkArgument(isValidLinkName(linkName), MESSAGE_CONSTRAINTS);
        checkArgument(!linker.equals(linkee), "Linker and linkee must be different persons.");
        this.linker = linker;
        this.linkee = linkee;
        this.linkName = linkName;
    }

    /**
     * Returns the person who initiated the link.
     *
     * @return The linker (source person).
     */
    public Person getLinker() {
        return this.linker;
    }

    /**
     * Returns the person who is the target of the link.
     *
     * @return The linkee (target person).
     */
    public Person getLinkee() {
        return this.linkee;
    }

    /**
     * Returns the name (label) of this link.
     *
     * @return The relationship name.
     */
    public String getLinkName() {
        return this.linkName;
    }

    /**
     * Returns true if the given string is a valid link name.
     *
     * @param name The name to validate.
     * @return {@code true} if the name matches the allowed format, {@code false} otherwise.
     */
    public static boolean isValidLinkName(String name) {
        requireNonNull(name);
        return name.matches(VALIDATION_REGEX);
    }

    /**
     * Returns a string representation of this link in the format:
     * <pre>
     *     Link[lawyer: Alice -> Bob]
     * </pre>
     */
    @Override
    public String toString() {
        return String.format("Link[%s: %s -> %s]", linkName, linker.getName(), linkee.getName());
    }

    /**
     * Returns true if both {@code Link} objects have the same {@code linker}, {@code linkee}, and {@code linkName}.
     * <p>
     * Only names of the persons are compared to avoid recursive equality checks in {@link Person#equals(Object)}.
     * </p>
     *
     * @param other The other object to compare.
     * @return {@code true} if both represent the same directed relationship.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Link)) {
            return false;
        }
        Link o = (Link) other;
        return linker.getName().equals(o.linker.getName())
                && linkee.getName().equals(o.linkee.getName())
                && linkName.equals(o.linkName);
    }

    /**
     * Returns a hash code value for this {@code Link}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(linkName, linker.getName().fullName, linkee.getName().fullName);
    }
}

