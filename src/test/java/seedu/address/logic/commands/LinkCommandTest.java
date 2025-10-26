package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Link;
import seedu.address.model.person.Person;
import seedu.address.model.person.builder.PersonBuilder;

public class LinkCommandTest {

    private static final String LINK_NAME = "lawyer";

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    // ---------------- Constructor guard rails ----------------

    @Test
    public void constructor_nullLinkerIndex_throwsNpe() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, ()
                -> new LinkCommand(null, LINK_NAME, INDEX_SECOND_PERSON));
    }

    @Test
    public void constructor_nullLinkName_throwsNpe() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, ()
                -> new LinkCommand(INDEX_FIRST_PERSON, null, INDEX_SECOND_PERSON));
    }

    @Test
    public void constructor_nullLinkeeIndex_throwsNpe() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, ()
                -> new LinkCommand(INDEX_FIRST_PERSON, LINK_NAME, null));
    }

    // ---------------- equals / hashCode / toString ----------------

    @Test
    public void equals_and_hashCode() {
        LinkCommand a = new LinkCommand(INDEX_FIRST_PERSON, "mentor", INDEX_SECOND_PERSON);
        LinkCommand b = new LinkCommand(INDEX_FIRST_PERSON, "mentor", INDEX_SECOND_PERSON);
        LinkCommand c = new LinkCommand(INDEX_FIRST_PERSON, "advisor", INDEX_SECOND_PERSON);
        LinkCommand d = new LinkCommand(INDEX_SECOND_PERSON, "mentor", INDEX_FIRST_PERSON);

        org.junit.jupiter.api.Assertions.assertEquals(a, b);
        org.junit.jupiter.api.Assertions.assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(a, 5);
        assertNotEquals(a, null);
    }

    @Test
    public void toString_containsFields() {
        Index linker = Index.fromOneBased(7);
        Index linkee = Index.fromOneBased(9);
        LinkCommand cmd = new LinkCommand(linker, "best friend", linkee);
        String s = cmd.toString();
        assertTrue(s.contains("linkerIndex"));
        assertTrue(s.contains("linkName"));
        assertTrue(s.contains("linkeeIndex"));
        assertTrue(s.contains(linker.toString()));
        assertTrue(s.contains("best friend"));
        assertTrue(s.contains(linkee.toString()));
    }

    // ---------------- Execution happy path ----------------

    @Test
    public void execute_validIndices_success() {
        Index linkerIdx = INDEX_FIRST_PERSON;
        Index linkeeIdx = INDEX_SECOND_PERSON;

        Person linker = model.getFilteredPersonList().get(linkerIdx.getZeroBased());
        Person linkee = model.getFilteredPersonList().get(linkeeIdx.getZeroBased());

        // Expected updated persons (both show the same Link instance)
        Link link = new Link(linker, linkee, LINK_NAME);

        Person expectedLinker = new PersonBuilder(linker)
                .withLinks(new HashSet<>(linker.getLinks()) {{ add(link); }})
                .build();

        Person expectedLinkee = new PersonBuilder(linkee)
                .withLinks(new HashSet<>(linkee.getLinks()) {{ add(link); }})
                .build();

        expectedModel.setPerson(linker, expectedLinker);
        expectedModel.setPerson(linkee, expectedLinkee);

        String expectedMessage = String.format(
                LinkCommand.MESSAGE_SUCCESS,
                Messages.format(expectedLinker),
                LINK_NAME,
                Messages.format(expectedLinkee)
        );

        assertCommandSuccess(new LinkCommand(linkerIdx, LINK_NAME, linkeeIdx),
                model, expectedMessage, expectedModel);

        // sanity: both persons indeed contain the link object
        Person actualLinker = model.getFilteredPersonList().get(linkerIdx.getZeroBased());
        Person actualLinkee = model.getFilteredPersonList().get(linkeeIdx.getZeroBased());
        assertTrue(actualLinker.getLinks().contains(link));
        assertTrue(actualLinkee.getLinks().contains(link));
    }

    @Test
    public void execute_duplicateLink_showsDuplicateMessage() throws Exception {
        Index linkerIdx = INDEX_FIRST_PERSON;
        Index linkeeIdx = INDEX_SECOND_PERSON;

        // First execution to add the link
        new LinkCommand(linkerIdx, LINK_NAME, linkeeIdx).execute(model);

        // expected model mirrors current state (no further change expected)
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        // Second execution is a duplicate → expect duplicate message + no change
        String expectedMessage = LinkCommand.MESSAGE_DUPLICATE_LINK;

        assertCommandSuccess(new LinkCommand(linkerIdx, LINK_NAME, linkeeIdx),
                model, expectedMessage, expectedModel);
    }

    // ---------------- Invalid indices ----------------

    @Test
    public void execute_invalidLinkerIndex_throwsCommandException() {
        int outOfRange = model.getFilteredPersonList().size() + 1;
        Index badLinker = Index.fromOneBased(outOfRange);
        Index goodLinkee = INDEX_FIRST_PERSON;

        assertCommandFailure(new LinkCommand(badLinker, LINK_NAME, goodLinkee),
                model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidLinkeeIndex_throwsCommandException() {
        int outOfRange = model.getFilteredPersonList().size() + 1;
        Index goodLinker = INDEX_FIRST_PERSON;
        Index badLinkee = Index.fromOneBased(outOfRange);

        assertCommandFailure(new LinkCommand(goodLinker, LINK_NAME, badLinkee),
                model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    // ---------------- Both persons updated check (redundant but explicit) ----------------

    @Test
    public void execute_updatesBothPersons_linkPresentInEachAfter() throws Exception {
        Index i1 = INDEX_FIRST_PERSON;
        Index i2 = INDEX_SECOND_PERSON;

        Person p1Before = model.getFilteredPersonList().get(i1.getZeroBased());
        Person p2Before = model.getFilteredPersonList().get(i2.getZeroBased());

        new LinkCommand(i1, LINK_NAME, i2).execute(model);

        Person p1After = model.getFilteredPersonList().get(i1.getZeroBased());
        Person p2After = model.getFilteredPersonList().get(i2.getZeroBased());

        Link expected = new Link(p1Before, p2Before, LINK_NAME);

        assertTrue(p1After.getLinks().contains(expected));
        assertTrue(p2After.getLinks().contains(expected));
    }
}


