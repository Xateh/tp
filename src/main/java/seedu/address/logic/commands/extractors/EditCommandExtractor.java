package seedu.address.logic.commands.extractors;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Extractor that builds {@code EditCommand}s.
 */
public final class EditCommandExtractor {
    // Messages for extraction
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified.";

    // Options
    public static final String OPTION_KEY_NAME = "name";
    public static final String OPTION_KEY_PHONE = "phone";
    public static final String OPTION_KEY_EMAIL = "email";
    public static final String OPTION_KEY_ADDRESS = "address";
    public static final String OPTION_KEY_TAG = "tag";

    private EditCommandExtractor() {
    }

    /**
     * Extracts command parameters and options from the given Command object. Performs input validation as well.
     *
     * @param bareCommand Command to extract parameters and options from.
     * @return EditCommand that can be executed.
     * @throws ValidationException When the command parameters fail to validate.
     */
    public static EditCommand extract(BareCommand bareCommand) throws ValidationException {
        // extract index
        Index index = Validation.validateIndex(bareCommand, 0);

        // extract edit details
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        try {
            Optional<String> name = bareCommand.getOptionValue(OPTION_KEY_NAME);
            if (name.isPresent()) {
                editPersonDescriptor.setName(ParserUtil.parseName(name.get()));
            }
            Optional<String> phone = bareCommand.getOptionValue(OPTION_KEY_PHONE);
            if (phone.isPresent()) {
                editPersonDescriptor.setPhone(ParserUtil.parsePhone(phone.get()));
            }
            Optional<String> email = bareCommand.getOptionValue(OPTION_KEY_EMAIL);
            if (email.isPresent()) {
                editPersonDescriptor.setEmail(ParserUtil.parseEmail(email.get()));
            }
            Optional<String> address = bareCommand.getOptionValue(OPTION_KEY_ADDRESS);
            if (address.isPresent()) {
                editPersonDescriptor.setAddress(ParserUtil.parseAddress(address.get()));
            }
            if (bareCommand.hasOption(OPTION_KEY_TAG)) {
                editPersonDescriptor.setTags(
                        ParserUtil.parseTags(bareCommand.getOptionAllValues(OPTION_KEY_TAG).get()));
            }
        } catch (ParseException e) {
            throw new ValidationException(e.getMessage());
        }

        return new EditCommand(index, editPersonDescriptor);
    }
}
