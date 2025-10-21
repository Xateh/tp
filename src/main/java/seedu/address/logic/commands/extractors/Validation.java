package seedu.address.logic.commands.extractors;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.ValidationException;

/**
 * Utility class for common extractions and validations used by multiple commands.
 */
public class Validation {
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Invalid index: expected positive integer, got %1$s";

    private Validation() {}

    /**
     * Validate the {@code Index} input field type for commands.
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
}
