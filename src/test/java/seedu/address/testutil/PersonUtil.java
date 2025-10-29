package seedu.address.testutil;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append("/name:" + person.getName().fullName + " ");
        sb.append("/phone:" + person.getPhone().value + " ");
        sb.append("/email:" + person.getEmail().value + " ");
        sb.append("/address:" + person.getAddress().value + " ");
        person.getTags().stream().forEach(
                s -> sb.append("/tag:" + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append("/name:").append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append("/phone:").append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append("/email:").append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append("/address:").append(address.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append("/tag");
            } else {
                tags.forEach(s -> sb.append("/tag:").append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
