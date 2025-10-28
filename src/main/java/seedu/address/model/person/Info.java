package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's information in the address book.
 */
public class Info {

    public final String value;

    /**
     * Constructs an {@code Info}.
     *
     * @param information A valid information string.
     */
    public Info(String information) {
        requireNonNull(information);
        this.value = information;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Info
                && value.equals(((Info) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
