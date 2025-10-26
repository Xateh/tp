package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Utility class for common extractions and validations used by multiple commands. Whenever these validators are used,
 * the validator will throw a {@code ValidationException} when validation fails with a generic failure message. Callers
 * should enrich the message where possible, by rethrowing a new exception after appending more details to the exception
 * message.
 */
public class Validation {
    public static final String MESSAGE_INSUFFICIENT_PARAMETERS_SINGLE =
            "Insufficient number of parameters supplied: expected parameter at index %1$s, got nothing.";
    public static final String MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE =
            "Insufficient number of parameters supplied: expected at least %1$s variable parameters starting from "
                    + "index %2$s, got %3$s.";
    public static final String MESSAGE_INCORRECT_PARAMETER_KIND =
            "Unexpected parameter kind at index $1%s: expected one of $2%s, got $3%s.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";

    public static final List<String> DISALLOWED_CUSTOM_FIELD_NAMES =
            List.of("name", "email", "phone", "address", "tag", "field");

    private Validation() {
    }

    /**
     * Validate a parameter's kind and position. Note that further validation may still be required.
     *
     * @param bareCommand    BareCommand to extract parameter from.
     * @param parameterKinds Kinds of parameter to expect.
     * @param position       Position from which to retrieve parameter.
     * @return Validated parameter.
     * @throws ValidationException When the parameter is either missing or of invalid kind.
     */
    public static Parameter validateParameter(BareCommand bareCommand, int position, ParameterKind... parameterKinds)
            throws ValidationException {
        requireNonNull(bareCommand);
        assert position >= 0;

        int parameterCount = bareCommand.parameterCount();

        if (parameterCount <= position) {
            throw new ValidationException(String.format(MESSAGE_INSUFFICIENT_PARAMETERS_SINGLE, position));
        }

        Parameter parameter = bareCommand.getParameter(position);

        if (!Set.of(parameterKinds).contains(parameter.getKind())) {
            throw new ValidationException(String.format(MESSAGE_INCORRECT_PARAMETER_KIND,
                    position, Arrays.toString(parameterKinds), parameter.getKind()));
        }

        return parameter;
    }

    /**
     * Validate variable parameter kinds and positions. Note that further validation may still be required. May return
     * an empty array if there are no specified variable parameters. Note that this does not validate the number of
     * parameters.
     *
     * @param bareCommand    BareCommand to extract parameter from.
     * @param parameterKinds Kinds of parameter to expect.
     * @param startPosition  Position from which to retrieve rest of parameters.
     * @return List of validated parameters.
     * @throws ValidationException When the parameters, if any, are of invalid kind.
     */
    public static List<Parameter> validateVariableParameters(BareCommand bareCommand, int startPosition,
                                                             ParameterKind... parameterKinds)
            throws ValidationException {
        requireNonNull(bareCommand);
        assert startPosition >= 0;

        int parameterCount = bareCommand.parameterCount();

        ArrayList<Parameter> parameters = new ArrayList<>();

        for (int i = startPosition; i < parameterCount; i++) {
            Parameter parameter = bareCommand.getParameter(i);

            if (!Set.of(parameterKinds).contains(parameter.getKind())) {
                throw new ValidationException(String.format(MESSAGE_INCORRECT_PARAMETER_KIND,
                        i, Arrays.toString(parameterKinds), parameter.getKind()));
            }

            parameters.add(parameter);
        }

        return parameters;
    }

    /**
     * Validate variable parameter kinds and positions including a minimum multiplicity. Note that further validation
     * may still be required. May return an empty array if there are no specified variable parameters.
     *
     * @param bareCommand    BareCommand to extract parameter from.
     * @param parameterKinds Kinds of parameter to expect.
     * @param startPosition  Position from which to retrieve rest of parameters.
     * @return List of validated parameters.
     * @throws ValidationException When there are insufficient parameters or parameters are of invalid kind.
     */
    public static List<Parameter> validateVariableParametersWithMinimumMultiplicity(
            BareCommand bareCommand, int startPosition, int minimumMultiplicity, ParameterKind... parameterKinds)
            throws ValidationException {
        requireNonNull(bareCommand);
        assert startPosition >= 0;

        List<Parameter> parameters = validateVariableParameters(bareCommand, startPosition, parameterKinds);

        if (parameters.size() < minimumMultiplicity) {
            throw new ValidationException(String.format(MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE,
                    minimumMultiplicity, startPosition, parameters.size()));
        }

        return parameters;
    }

    /**
     * Validates the {@code Index} input field type for commands.
     *
     * @param input String to validate.
     * @return {@code Index} after validation.
     * @throws ValidationException When the input fails to validate.
     */
    public static Index validateIndex(String input) throws ValidationException {
        requireNonNull(input);
        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            // only thrown by Integer::parseInt
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, input));
        } catch (IndexOutOfBoundsException e) {
            // only thrown by Index::fromOneBased
            throw new ValidationException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, input));
        }
        return index;
    }

    /**
     * Validates the {@code Index} input field type for commands.
     *
     * @param bareCommand BareCommand to extract parameter from.
     * @param position    Position from which to retrieve index.
     * @return {@code Index} after validation.
     * @throws ValidationException When the input fails to validate.
     */
    public static Index validateIndex(BareCommand bareCommand, int position) throws ValidationException {
        requireNonNull(bareCommand);
        return Validation.validateIndex(
                Validation.validateParameter(bareCommand, position, ParameterKind.NORMAL).getValue());
    }


    /**
     * Parses a {@code String name} into a {@code Name}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ValidationException the given {@code name} is invalid.
     */
    public static Name validateName(String name) throws ValidationException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ValidationException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ValidationException if the given {@code phone} is invalid.
     */
    public static Phone validatePhone(String phone) throws ValidationException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ValidationException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ValidationException if the given {@code address} is invalid.
     */
    public static Address validateAddress(String address) throws ValidationException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ValidationException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ValidationException if the given {@code email} is invalid.
     */
    public static Email validateEmail(String email) throws ValidationException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ValidationException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ValidationException if the given {@code tag} is invalid.
     */
    public static Tag validateTag(String tag) throws ValidationException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ValidationException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> validateTags(Collection<String> tags) throws ValidationException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(validateTag(tagName));
        }
        return tagSet;
    }
}
