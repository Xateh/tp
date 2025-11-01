package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.FieldContainsKeywordsPredicate;
import seedu.address.model.person.Link;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.builder.PersonBuilder;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().withName(new Name("test")).withAddress(new Address("test1"))
                .withEmail(new Email("test@example.com"))
                .withPhone(new Phone("99999999")).withTags(Set.of()).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(new Name(VALID_NAME_BOB)).withPhone(new Phone(VALID_PHONE_BOB))
                .withTags(Set.of(new Tag(VALID_TAG_HUSBAND))).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(new Name(VALID_NAME_BOB)).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);
        // reapply the same filter as was applied to the original model
        final String[] splitName = personInFilteredList.getName().fullName.split("\\s+");
        expectedModel.updateFilteredPersonList(new FieldContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_editPersonNoLongerShown() {
        // prepare model with a filter that shows only the first person
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(new Name(VALID_NAME_BOB)).build();

        // edit so that the person's name no longer matches the active filter predicate
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        // apply the edit to the expected model
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);
        // reapply the same filter: the edited person should no longer be visible
        final String[] splitName = personInFilteredList.getName().fullName.split("\\s+");
        expectedModel.updateFilteredPersonList(new FieldContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        // confirm that the filtered list in the actual model is now empty (edited person filtered out)
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_removeAllTags_success() {
        // remove all tags from the first person
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withTags(Set.of()).build();

        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withTags().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void editPersonDescriptor_getTags_unmodifiable() {
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags("friend").build();
        // getTags returns unmodifiable set when present
        assertTrue(descriptor.getTags().isPresent());
        Set<Tag> tags = descriptor.getTags().get();
        try {
            tags.add(new Tag("newtag"));
        } catch (UnsupportedOperationException e) {
            // expected
            return;
        }
        // If no exception thrown, fail the test
        throw new AssertionError("Expected UnsupportedOperationException when modifying tags set");
    }

    @Test
    public void editPersonDescriptorCopyConstructorAndIsAnyFieldEdited() {
        // descriptor with no fields
        EditCommand.EditPersonDescriptor emptyDesc = new EditCommand.EditPersonDescriptor();
        assertFalse(emptyDesc.isAnyFieldEdited());

        // set a field and test copy constructor and isAnyFieldEdited
        emptyDesc.setName(new Name(VALID_NAME_BOB));
        assertTrue(emptyDesc.isAnyFieldEdited());
        EditCommand.EditPersonDescriptor copy = new EditCommand.EditPersonDescriptor(emptyDesc);
        assertEquals(emptyDesc.getName(), copy.getName());
    }

    @Test
    public void execute_preservesExistingCustomFields_success() {
        Model modelWithCustomFields = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToEdit = modelWithCustomFields.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Map<String, String> existingCustomFields = Map.of("company", "Tiktok");
        Person personWithCustomFields = new PersonBuilder(personToEdit)
                .withCustomFields(existingCustomFields)
                .build();
        modelWithCustomFields.setPerson(personToEdit, personWithCustomFields);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Person expectedEditedPerson = new PersonBuilder(personWithCustomFields)
                .withPhone(new Phone(VALID_PHONE_BOB))
                .build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(expectedEditedPerson));

        Model expectedModel = new ModelManager(new AddressBook(modelWithCustomFields.getAddressBook()),
                new UserPrefs());
        expectedModel.setPerson(personWithCustomFields, expectedEditedPerson);

        assertCommandSuccess(editCommand, modelWithCustomFields, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void editPersonDescriptor_equals_variousCases() {
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setName(new Name(VALID_NAME_BOB));
        descriptor.setPhone(new seedu.address.model.person.Phone(VALID_PHONE_BOB));
        descriptor.setTags(new java.util.HashSet<>());

        // same object -> true
        assertTrue(descriptor.equals(descriptor));

        // different type -> false
        assertFalse(descriptor.equals("not a descriptor"));

        // different name -> false
        EditCommand.EditPersonDescriptor other = new EditCommand.EditPersonDescriptor(descriptor);
        other.setName(new Name("Different Name"));
        assertFalse(descriptor.equals(other));

        // different phone -> false
        other = new EditCommand.EditPersonDescriptor(descriptor);
        other.setPhone(new seedu.address.model.person.Phone("99999999"));
        assertFalse(descriptor.equals(other));

        // different email -> false
        other = new EditCommand.EditPersonDescriptor(descriptor);
        other.setEmail(new seedu.address.model.person.Email("diff@example.com"));
        assertFalse(descriptor.equals(other));

        // different address -> false
        other = new EditCommand.EditPersonDescriptor(descriptor);
        other.setAddress(new seedu.address.model.person.Address("Different Address"));
        assertFalse(descriptor.equals(other));

        // different tags -> false
        other = new EditCommand.EditPersonDescriptor(descriptor);
        java.util.Set<Tag> tagSet = new java.util.HashSet<>();
        tagSet.add(new Tag("newtag"));
        other.setTags(tagSet);
        assertFalse(descriptor.equals(other));
    }

    @Test
    public void execute_editPersonWithNoLinks_keepsOthersUnchanged() {
        // Person 1 has no links by default in TypicalPersons
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Edit only the name
        String newName = "test";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(personToEdit).withName(newName).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Person expectedEdited = new PersonBuilder(personToEdit).withName(new Name(newName)).build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(expectedEdited));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, expectedEdited);

        // No extra updateFilteredPersonList() is called; setPerson() should be sufficient
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);

        // Sanity: edited person in actual model matches expected, and links remain empty
        Person actualEdited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(expectedEdited.getLinks(), actualEdited.getLinks());
    }

    @Test
    public void execute_editPerson_cascadesLinkUpdates() {
        // Get two existing persons from the model
        Person p1 = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person p2 = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Create a link: p1 (holder) -> friend -> p2 (target)
        Link link = new Link(p1, p2, "friend");
        Person p1WithLink = new PersonBuilder(p1).withLinks(Set.of(link)).build();
        model.setPerson(p1, p1WithLink);

        // Edit the target's name (p2)
        String newName = "Updated Target";
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON,
                new EditPersonDescriptorBuilder().withName(newName).build());

        Person editedP2 = new PersonBuilder(p2).withName(new Name(newName)).build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedP2));

        // Build expected model FROM current address book (already has p1WithLink),
        // then find the corresponding instances inside expectedModel.
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        // Get holder & target instances that exist inside expectedModel
        Person holderInExpected = expectedModel.getAddressBook().getPersonList().stream()
                .filter(px -> px.isSamePerson(p1)) // will resolve to p1WithLink instance in expectedModel
                .findFirst().orElseThrow();

        Person targetInExpected = expectedModel.getAddressBook().getPersonList().stream()
                .filter(px -> px.isSamePerson(p2))
                .findFirst().orElseThrow();

        // Prepare edited target for expectedModel (same identity as targetInExpected, new name)
        Person editedTargetInExpected = new PersonBuilder(targetInExpected)
                .withName(new Name(newName))
                .build();

        // Update the link on the holder to point at the edited target
        Person holderAfterCascadeInExpected = new PersonBuilder(holderInExpected)
                .withLinks(Set.of(new Link(holderInExpected, editedTargetInExpected, "friend")))
                .build();

        // Apply changes to expectedModel using the instances that actually exist inside it
        expectedModel.setPerson(targetInExpected, editedTargetInExpected);
        expectedModel.setPerson(holderInExpected, holderAfterCascadeInExpected);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Target (p2) is linked from TWO different holders (p1, p3).
     * After editing p2's name, both holders' links should point to the edited p2.
     */
    @Test
    public void execute_editTarget_updatesMultipleHolders() {
        Person p1 = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person p2 = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        // create a third person (clone the last with a new name to ensure uniqueness)
        Person p3 = new PersonBuilder().withName(new Name("test")).withAddress(new Address("test1"))
                .withEmail(new Email("test@example.com"))
                .withPhone(new Phone("99999999")).withTags(Set.of()).build();
        model.addPerson(p3);

        // Setup: p1 --mentor--> p2,  p3 --coach--> p2
        Person p1WithLink = new PersonBuilder(p1).withLinks(Set.of(new Link(p1, p2, "mentor"))).build();
        model.setPerson(p1, p1WithLink);
        Person p3WithLink = new PersonBuilder(p3).withLinks(Set.of(new Link(p3, p2, "coach"))).build();
        model.setPerson(p3, p3WithLink);

        // Edit target p2's name
        String newName = "Edited Target";
        EditCommand cmd = new EditCommand(Index.fromOneBased(2),
                new EditPersonDescriptorBuilder().withName(newName).build());
        Person editedP2 = new PersonBuilder(p2).withName(new Name(newName)).build();
        String expectedMsg = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedP2));

        // expectedModel from current state (already has links set up)
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        // Resolve instances inside expectedModel
        Person p1Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p1)).findFirst().orElseThrow();
        Person p2Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p2)).findFirst().orElseThrow();
        Person p3Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p3)).findFirst().orElseThrow();

        Person editedP2Exp = new PersonBuilder(p2Exp).withName(new Name(newName)).build();

        Person p1After = new PersonBuilder(p1Exp)
                .withLinks(Set.of(new Link(p1Exp, editedP2Exp, "mentor")))
                .build();
        Person p3After = new PersonBuilder(p3Exp)
                .withLinks(Set.of(new Link(p3Exp, editedP2Exp, "coach")))
                .build();

        // Apply in expectedModel
        expectedModel.setPerson(p2Exp, editedP2Exp);
        expectedModel.setPerson(p1Exp, p1After);
        expectedModel.setPerson(p3Exp, p3After);

        assertCommandSuccess(cmd, model, expectedMsg, expectedModel);
    }

    /**
     * Holder (p1) has links to TWO different targets (p2, p3).
     * After editing p1's name, both links held by others should point to edited p1.
     */
    @Test
    public void execute_editHolder_updatesMultipleTargets() {
        Person p1 = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person p2 = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        // create a third target
        Person p3 = new PersonBuilder().withName(new Name("test")).withAddress(new Address("test1"))
                .withEmail(new Email("test@example.com"))
                .withPhone(new Phone("99999999")).withTags(Set.of()).build();
        model.addPerson(p3);

        // Store links on targets (so cascade must update them):
        // p1 --advisor--> p2, and p1 --lawyer--> p3 (links stored on p2 and p3)
        Person p2WithLink = new PersonBuilder(p2).withLinks(Set.of(new Link(p1, p2, "advisor"))).build();
        model.setPerson(p2, p2WithLink);
        Person p3WithLink = new PersonBuilder(p3).withLinks(Set.of(new Link(p1, p3, "lawyer"))).build();
        model.setPerson(p3, p3WithLink);

        // Edit holder p1's name
        String newName = "Edited Holder";
        EditCommand cmd = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(newName).build());
        Person editedP1 = new PersonBuilder(p1).withName(new Name(newName)).build();
        String expectedMsg = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedP1));

        // expectedModel from current state (already has links)
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        // Resolve instances
        Person p1Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p1)).findFirst().orElseThrow();
        Person p2Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p2)).findFirst().orElseThrow();
        Person p3Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p3)).findFirst().orElseThrow();

        Person editedP1Exp = new PersonBuilder(p1Exp).withName(new Name(newName)).build();

        Person p2After = new PersonBuilder(p2Exp)
                .withLinks(Set.of(new Link(editedP1Exp, p2Exp, "advisor")))
                .build();
        Person p3After = new PersonBuilder(p3Exp)
                .withLinks(Set.of(new Link(editedP1Exp, p3Exp, "lawyer")))
                .build();

        // Apply in expectedModel
        expectedModel.setPerson(p1Exp, editedP1Exp);
        expectedModel.setPerson(p2Exp, p2After);
        expectedModel.setPerson(p3Exp, p3After);

        assertCommandSuccess(cmd, model, expectedMsg, expectedModel);
    }

    /**
     * Editing an unrelated person (p3) must not touch existing links between p1 and p2.
     */
    @Test
    public void execute_editUnrelatedPerson_doesNotAffectExistingLinks() {
        Person p1 = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person p2 = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person p3 = new PersonBuilder().withName(new Name("test")).withAddress(new Address("test1"))
                .withEmail(new Email("test@example.com"))
                .withPhone(new Phone("99999999")).withTags(Set.of()).build();
        model.addPerson(p3);

        // p1 --friend--> p2 (stored on p1)
        Person p1WithLink = new PersonBuilder(p1).withLinks(Set.of(new Link(p1, p2, "friend"))).build();
        model.setPerson(p1, p1WithLink);

        // Edit unrelated p3 (shouldn't change p1<->p2 link)
        String newName = "Edited Unrelated";
        EditCommand cmd = new EditCommand(Index.fromOneBased(model.getFilteredPersonList().size()),
                new EditPersonDescriptorBuilder().withName(newName).build());
        Person editedP3 = new PersonBuilder(p3).withName(new Name(newName)).build();
        String expectedMsg = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedP3));

        // expectedModel mirrors: only p3 changes; link stays the same
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Person p3Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p3)).findFirst().orElseThrow();
        Person editedP3Exp = new PersonBuilder(p3Exp).withName(new Name(newName)).build();
        expectedModel.setPerson(p3Exp, editedP3Exp);

        // p1 in expected should still have the same link to p2
        Person p1Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p1)).findFirst().orElseThrow();
        assertTrue(p1Exp.getLinks().stream().anyMatch(l ->
                l.getLinker().isSamePerson(p1Exp)
                        && l.getLinkee().isSamePerson(expectedModel.getAddressBook().getPersonList().stream()
                        .filter(px -> px.isSamePerson(p2)).findFirst().orElseThrow())
                        && l.getLinkName().equals("friend")));

        assertCommandSuccess(cmd, model, expectedMsg, expectedModel);
    }

    /**
     * Editing a non-link field (e.g., phone) should not alter links referencing the person.
     */
    @Test
    public void execute_editNonLinkField_preservesLinks() {
        Person p1 = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person p2 = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Set a link stored on p2: p1 --colleague--> p2
        Person p2WithLink = new PersonBuilder(p2).withLinks(Set.of(new Link(p1, p2, "colleague"))).build();
        model.setPerson(p2, p2WithLink);

        // Edit p2's phone only
        String newPhone = "99999999";
        EditCommand cmd = new EditCommand(INDEX_SECOND_PERSON,
                new EditPersonDescriptorBuilder().withPhone(newPhone).build());
        Person editedP2 = new PersonBuilder(p2).withPhone(new Phone(newPhone)).build();
        String expectedMsg = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedP2));

        // expectedModel from current (has link)
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Person p1Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p1)).findFirst().orElseThrow();
        Person p2Exp = expectedModel.getAddressBook().getPersonList()
                .stream().filter(px -> px.isSamePerson(p2)).findFirst().orElseThrow();

        Person editedP2Exp = new PersonBuilder(p2Exp).withPhone(new Phone(newPhone)).build();
        // Link should still be p1 -> p2 (edited)
        Person p2After = new PersonBuilder(editedP2Exp)
                .withLinks(Set.of(new Link(p1Exp, editedP2Exp, "colleague")))
                .build();

        expectedModel.setPerson(p2Exp, p2After);

        assertCommandSuccess(cmd, model, expectedMsg, expectedModel);
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }
}
