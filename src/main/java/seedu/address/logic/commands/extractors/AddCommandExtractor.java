package seedu.address.logic.commands.extractors;

import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind.NORMAL;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.builder.PersonBuilder;

/**
 * Extractor that builds {@code FieldCommand}s from {@link BareCommand}s.
 */
public final class AddCommandExtractor {
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
        Name name = Validation.validateName(Validation.validateParameter(bareCommand, 0, NORMAL).getValue());
        Phone phone = Validation.validatePhone(Validation.validateParameter(bareCommand, 1, NORMAL).getValue());
        Address address = Validation.validateAddress(Validation.validateParameter(bareCommand, 2, NORMAL).getValue());
        Email email = Validation.validateEmail(Validation.validateParameter(bareCommand, 3, NORMAL).getValue());

        Person person = new PersonBuilder()
                .withName(name)
                .withPhone(phone)
                .withAddress(address)
                .withEmail(email)
                .build();

        return new AddCommand(person);
    }
}
