package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind.NORMAL;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.builder.PersonBuilder;
import seedu.address.model.tag.Tag;

/**
 * Extractor that builds {@code FieldCommand}s from {@link BareCommand}s.
 */
public final class AddCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INVALID_TAGS = "If the tag option is specified, it must not be empty.";

    // Options
    public static final String OPTION_KEY_TAG = "tag";

    private AddCommandExtractor() {
    }

    /**
     * Extracts command parameters and options from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters and options from.
     * @return AddCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static AddCommand extract(BareCommand bareCommand) throws ValidationException {
        PersonBuilder personBuilder = new PersonBuilder();

        personBuilder.withName(Validation.validateName(
                Validation.validateParameter(bareCommand, 0, NORMAL).getValue()));
        personBuilder.withPhone(Validation.validatePhone(
                Validation.validateParameter(bareCommand, 1, NORMAL).getValue()));
        personBuilder.withAddress(Validation.validateAddress(
                Validation.validateParameter(bareCommand, 2, NORMAL).getValue()));
        personBuilder.withEmail(Validation.validateEmail(
                Validation.validateParameter(bareCommand, 3, NORMAL).getValue()));
        if (bareCommand.hasOption(OPTION_KEY_TAG)) {
            Set<Tag> tags = Validation.validateTags(bareCommand.getOptionAllValues(OPTION_KEY_TAG).get());
            if (tags.isEmpty()) {
                throw new ValidationException(MESSAGE_INVALID_TAGS);
            }
            personBuilder.withTags(tags);
        }

        return new AddCommand(personBuilder.build());
    }
}
