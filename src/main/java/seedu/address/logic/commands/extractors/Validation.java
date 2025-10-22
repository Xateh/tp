package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;

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

    private Validation() {
    }

    /**
     * Validate a parameter's kind and position. Note that further validation may still be required.
     *
     * @param bareCommand    BareCommand to extract parameter from.
     * @param parameterKinds Kinds of parameter to expect.
     * @param position       Position from which to retrieve parameter.
     * @return String in validated parameter.
     * @throws ValidationException When the parameter is either missing or of invalid kind.
     */
    public static String validateParameter(BareCommand bareCommand, int position, ParameterKind... parameterKinds)
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

        return parameter.getValue();
    }

    /**
     * Validate variable parameter kinds and positions. Note that further validation may still be required. May return
     * an empty array if there are no specified variable parameters. Note that this does not validate the number of
     * parameters.
     *
     * @param bareCommand    BareCommand to extract parameter from.
     * @param parameterKinds Kinds of parameter to expect.
     * @param startPosition  Position from which to retrieve rest of parameters.
     * @return Strings in validated parameters.
     * @throws ValidationException When the parameters, if any, are of invalid kind.
     */
    public static List<String> validateVariableParameters(BareCommand bareCommand, int startPosition,
                                                          ParameterKind... parameterKinds)
            throws ValidationException {
        requireNonNull(bareCommand);
        assert startPosition >= 0;

        int parameterCount = bareCommand.parameterCount();

        ArrayList<String> parameters = new ArrayList<>();

        for (int i = startPosition; i < parameterCount; i++) {
            Parameter parameter = bareCommand.getParameter(i);

            if (!Set.of(parameterKinds).contains(parameter.getKind())) {
                throw new ValidationException(String.format(MESSAGE_INCORRECT_PARAMETER_KIND,
                        i, Arrays.toString(parameterKinds), parameter.getKind()));
            }

            parameters.add(parameter.getValue());
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
     * @return Strings in validated parameters.
     * @throws ValidationException When there are insufficient parameters or parameters are of invalid kind.
     */
    public static List<String> validateVariableParametersWithMinimumMultiplicity(
            BareCommand bareCommand, int startPosition, int minimumMultiplicity, ParameterKind... parameterKinds)
            throws ValidationException {
        requireNonNull(bareCommand);
        assert startPosition >= 0;

        List<String> parameters = validateVariableParameters(bareCommand, startPosition, parameterKinds);

        if (parameters.size() < minimumMultiplicity) {
            throw new ValidationException(String.format(MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE,
                    minimumMultiplicity, startPosition, parameters.size()));
        }

        return parameters;
    }

}