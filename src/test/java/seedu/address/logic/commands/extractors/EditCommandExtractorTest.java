package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.lexer.LexerException;
import seedu.address.logic.grammars.command.parser.ParserException;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandExtractorTest {
    @Test
    public void extract_validArgs_returnsEditCommand() throws LexerException, ParserException, ValidationException {
        EditPersonDescriptor descriptor1 = new EditPersonDescriptorBuilder().withName("John Doe").build();
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor1),
                EditCommandExtractor.extract(BareCommand.parse("edit 1 /name:\"John Doe\"")));

        EditPersonDescriptor descriptor2 = new EditPersonDescriptorBuilder().withPhone("98765432")
                .withEmail("john@doe.com").build();
        assertEquals(new EditCommand(INDEX_SECOND_PERSON, descriptor2),
                EditCommandExtractor.extract(BareCommand.parse(
                        "edit 2 /phone:98765432 /email:\"john@doe.com\"")));

        EditPersonDescriptor descriptor3 = new EditPersonDescriptorBuilder().withName("John Doe")
                .withPhone("98765432").withEmail("john@doe.com").withAddress("123 Main St")
                .withTags("friend").build();
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor3),
                EditCommandExtractor.extract(BareCommand.parse(
                        "edit 1 /tag:friend /address:\"123 Main St\" /name:\"John Doe\" "
                                + "/phone:98765432 /email:\"john@doe.com\"")));

        EditPersonDescriptor descriptor4 = new EditPersonDescriptorBuilder().withTags("friend", "colleague").build();
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor4),
                EditCommandExtractor.extract(BareCommand.parse("edit 1 /tag:friend /tag:colleague")));

        EditPersonDescriptor descriptor5 = new EditPersonDescriptorBuilder().withTags().build();
        // .withTags() implies empty set
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor5),
                EditCommandExtractor.extract(BareCommand.parse("edit 1 /tag")));
    }

    @Test
    public void extract_invalidArgs_throwsException() {
        assertThrows(ValidationException.class, () -> EditCommandExtractor.extract(BareCommand.parse(
                "edit 1 /name:\"John Doe @\"")));
        assertThrows(ValidationException.class, () -> EditCommandExtractor.extract(BareCommand.parse(
                "edit 1 /phone:abcdef")));
        assertThrows(ValidationException.class, () -> EditCommandExtractor.extract(BareCommand.parse(
                "edit 1 /email:random")));
    }

    @Test
    public void extract_emptyTags_removesAllTags() throws LexerException, ParserException, ValidationException {
        EditPersonDescriptor descriptor6 = new EditPersonDescriptorBuilder().withName("New Name").withTags().build();
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor6),
                EditCommandExtractor.extract(BareCommand.parse("edit 1 /name:\"New Name\" /tag")));
    }

    @Test
    public void extract_invalidArgs_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                EditCommandExtractor.extract(BareCommand.parse("edit /name:John")));

        assertThrows(ValidationException.class, () ->
                EditCommandExtractor.extract(BareCommand.parse("edit abc /name:John")));

        assertThrows(ValidationException.class, () ->
                EditCommandExtractor.extract(BareCommand.parse("edit 0 /name:John")));
    }

    @Test
    public void extract_duplicateTags_handledCorrectly() throws LexerException, ParserException, ValidationException {
        // Duplicate tags should be handled gracefully by the Set in the descriptor
        // Input: "edit 1 /tag:friend /tag:friend"
        // Expected: A descriptor with a single "friend" tag.
        EditCommand expected = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withTags("friend")
                .build());
        assertEquals(expected,
                EditCommandExtractor.extract(BareCommand.parse("edit 1 /tag:friend /tag:friend")));
    }

    @Test
    public void extract_duplicateTagOptions_generatesWarningOnExecute() throws Exception {
        // prepare a model with a person
        seedu.address.model.AddressBook ab = new seedu.address.model.AddressBook();
        seedu.address.model.person.Person p = new seedu.address.testutil.PersonBuilder().withTags().build();
        ab.addPerson(p);
        seedu.address.model.Model model = new seedu.address.model.ModelManager(ab, new seedu.address.model.UserPrefs());

        EditCommand cmd = EditCommandExtractor.extract(BareCommand.parse("edit 1 /tag:friend /tag:friend"));
        seedu.address.logic.commands.CommandResult result = cmd.execute(model);

        assertEquals(1, result.getWarnings().size());
        assertEquals(seedu.address.logic.commands.Warning.Type.DUPLICATE_INPUT_IGNORED,
                result.getWarnings().get(0).getType());
    }
}
