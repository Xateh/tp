package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;

/**
 * Creates a directed link between two persons in the address book.
 * Syntax: link INDEX1 LINK_NAME INDEX2
 * Meaning: person at INDEX1 is the LINK_NAME of the person at INDEX2.
 */
public class LinkCommand extends Command {

    public static final String COMMAND_WORD = "link";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Links two persons.\n"
            + "Parameters: INDEX1 LINK_NAME INDEX2\n"
            + "Example: " + COMMAND_WORD + " 1 lawyer 2";

    public static final String MESSAGE_SUCCESS = "%1$s is now %2$s of %3$s";
    public static final String MESSAGE_DUPLICATE_LINK = "No change: that link already exists.";
    public static final String MESSAGE_SAME_PERSON = "Cannot link a person to themselves.";

    private final Index linkerIndex;   // INDEX1
    private final String linkName;     // LINK_NAME
    private final Index linkeeIndex;   // INDEX2

    public LinkCommand(Index linkerIndex, String linkName, Index linkeeIndex) {
        requireNonNull(linkerIndex);
        requireNonNull(linkName);
        requireNonNull(linkeeIndex);
        this.linkerIndex = linkerIndex;
        this.linkName = linkName;
        this.linkeeIndex = linkeeIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (linkerIndex.getZeroBased() >= lastShownList.size()
                || linkeeIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        if (linkerIndex.equals(linkeeIndex)) {
            throw new CommandException(MESSAGE_SAME_PERSON);
        }

        Person linker = lastShownList.get(linkerIndex.getZeroBased());
        Person linkee = lastShownList.get(linkeeIndex.getZeroBased());

        Link newLink = new Link(linker, linkee, linkName);

        Set<Link> linkerLinks = new HashSet<>(linker.getLinks());
        Set<Link> linkeeLinks = new HashSet<>(linkee.getLinks());

        // If the exact link already exists on linker, treat as no-op
        if (linkerLinks.contains(newLink)) {
            return new CommandResult(MESSAGE_DUPLICATE_LINK);
        }


        // Add the corresponding Link instance to both persons
        linkerLinks.add(newLink);
        linkeeLinks.add(newLink);

        Person updatedLinker = createPersonWithAddedLink(linker, newLink);
        Person updatedLinkee = createPersonWithAddedLink(linkee, newLink);

        model.setPerson(linker, updatedLinker);
        model.setPerson(linkee, updatedLinkee);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                Messages.format(updatedLinker), linkName, Messages.format(updatedLinkee)));
    }

    /**
     * Creates and returns a new {@code Person} with the given link added.
     */
    private static Person createPersonWithAddedLink(Person original, Link linkToAdd) {
        requireNonNull(original);
        requireNonNull(linkToAdd);

        Set<Link> updatedLinks = new HashSet<>(original.getLinks());
        updatedLinks.add(linkToAdd);

        return new PersonBuilder(original)
                .withLinks(updatedLinks)
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LinkCommand)) {
            return false;
        }
        LinkCommand o = (LinkCommand) other;
        return linkerIndex.equals(o.linkerIndex)
                && linkName.equals(o.linkName)
                && linkeeIndex.equals(o.linkeeIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkerIndex, linkName, linkeeIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("linkerIndex", linkerIndex)
                .add("linkName", linkName)
                .add("linkeeIndex", linkeeIndex)
                .toString();
    }
}
