package seedu.address.logic.commands.extractors;

import java.nio.charset.StandardCharsets;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InfoSaveCommand;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.model.person.Info;

/**
 * Extractor that builds {@code InfoSaveCommand}s.
 */
public class InfoSaveCommandExtractor {
    public static final String MESSAGE_INDEX_UNSPECIFIED = "Index not specified for infosave command.";
    public static final String MESSAGE_INDEX_FAILED_TO_PARSE = "Invalid index: expected positive integer, got %1$s";
    public static final String MESSAGE_INFO_UNSPECIFIED = "Information not specified for infosave command.";

    private InfoSaveCommandExtractor() {}

    /**
     * Extracts command parameters from the given Command object.
     */
    public static InfoSaveCommand extract(BareCommand bareCommand) throws ValidationException {
        String[] params = bareCommand.getAllParameters();

        if (params.length == 0) {
            throw new ValidationException(MESSAGE_INDEX_UNSPECIFIED);
        }

        Index index;
        try {
            index = Index.fromOneBased(Integer.parseInt(params[0]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new ValidationException(String.format(MESSAGE_INDEX_FAILED_TO_PARSE, params[0]));
        }

        if (params.length < 2) {
            throw new ValidationException(MESSAGE_INFO_UNSPECIFIED);
        }

        // The second parameter is the hex encoded info
        String hexEncodedInfo = params[1];

        String infoString;
        try {
            // Handle empty hex string (empty info)
            if (hexEncodedInfo.isEmpty()) {
                infoString = "";
            } else {
                infoString = decodeFromSafeHex(hexEncodedInfo);
            }
        } catch (Exception e) {
            throw new ValidationException("Failed to decode information data: " + e.getMessage());
        }

        Info info = new Info(infoString);
        return new InfoSaveCommand(index, info);
    }

    /**
     * Decodes a hex string back to the original string.
     */
    private static String decodeFromSafeHex(String hexString) throws ValidationException {
        if (hexString.length() % 2 != 0) {
            throw new ValidationException("Invalid hex string length");
        }

        try {
            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                String hexByte = hexString.substring(2 * i, 2 * i + 2);
                bytes[i] = (byte) Integer.parseInt(hexByte, 16);
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid hex characters in encoded string");
        }
    }
}
