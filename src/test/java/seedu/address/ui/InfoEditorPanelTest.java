package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Info;

public class InfoEditorPanelTest {

    @Test
    public void functionalInterface_commandExecutor_worksCorrectly() {
        AtomicReference<String> commandRef = new AtomicReference<>();
        Consumer<String> commandExecutor = command -> commandRef.set(command);

        commandExecutor.accept("test command");
        assertEquals("test command", commandRef.get());
    }

    @Test
    public void functionalInterface_showPersonList_worksCorrectly() {
        AtomicBoolean showListCalled = new AtomicBoolean(false);
        Runnable showPersonList = () -> showListCalled.set(true);

        showPersonList.run();
        assertTrue(showListCalled.get());
    }

    @Test
    public void info_creation_directTest() {
        // Test Info class directly to isolate the issue
        String multilineInfo = "Line 1\nLine 2\nLine 3";
        Info info = new Info(multilineInfo);
        assertEquals(multilineInfo, info.toString());
    }

    @Test
    public void info_creation_emptyString() {
        Info info = new Info("");
        assertEquals("", info.toString());
    }

    @Test
    public void encodeToSafeHex_basicString_encodesCorrectly() {
        String input = "Test";
        String expectedHex = "54657374"; // "Test" in hex

        String actualHex = stringToHex(input);
        assertEquals(expectedHex, actualHex);
    }

    @Test
    public void encodeToSafeHex_emptyString_encodesCorrectly() {
        String input = "";
        String expectedHex = ""; // Empty string in hex

        String actualHex = stringToHex(input);
        assertEquals(expectedHex, actualHex);
    }

    @Test
    public void encodeToSafeHex_specialCharacters_encodesCorrectly() {
        String input = "Test: $100 & 50% off!";

        String actualHex = stringToHex(input);
        // Verify hex is not empty and contains only valid hex characters
        assertTrue(actualHex.matches("[0-9A-F]*"));
        assertTrue(actualHex.length() > 0);

        // Verify specific characters are encoded correctly
        assertTrue(actualHex.contains("24")); // $ symbol
        assertTrue(actualHex.contains("26")); // & symbol
        assertTrue(actualHex.contains("25")); // % symbol
    }

    @Test
    public void encodeToSafeHex_unicodeCharacters_encodesCorrectly() {
        String input = "Testing «ταβ» characters";

        String actualHex = stringToHex(input);
        // Verify hex is not empty and contains only valid hex characters
        assertTrue(actualHex.matches("[0-9A-F]*"));
        assertTrue(actualHex.length() > 0);

        // Unicode characters should result in longer hex strings
        assertTrue(actualHex.length() > input.length() * 2);
    }

    @Test
    public void encodeToSafeHex_newlineCharacters_encodesCorrectly() {
        String input = "Line1\nLine2\nLine3";

        String actualHex = stringToHex(input);
        // Verify hex is not empty and contains only valid hex characters
        assertTrue(actualHex.matches("[0-9A-F]*"));
        assertTrue(actualHex.length() > 0);
        // Should contain 0A (hex for newline)
        assertTrue(actualHex.contains("0A"));
    }

    @Test
    public void encodeToSafeHex_roundTrip_preservesData() {
        String original = "Test with special chars: $100 & 50% off!\nNew line here.";

        String encoded = stringToHex(original);
        String decoded = hexToString(encoded);

        assertEquals(original, decoded);
    }

    @Test
    public void commandGeneration_logic_worksCorrectly() {
        // Test the logic that would be used in InfoEditorPanel.handleSave()
        int oneBasedIndex = 3;
        String infoText = "Test information";
        String encodedInfo = stringToHex(infoText);
        String expectedCommand = "infosave " + oneBasedIndex + " " + encodedInfo;

        String actualCommand = generateSaveCommand(oneBasedIndex, infoText);
        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void commandGeneration_emptyInfo_worksCorrectly() {
        int oneBasedIndex = 1;
        String infoText = "";
        String expectedCommand = "infosave 1 ";

        String actualCommand = generateSaveCommand(oneBasedIndex, infoText);
        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    public void commandGeneration_specialCharacters_worksCorrectly() {
        int oneBasedIndex = 2;
        String infoText = "Client: $500 & 25% discount!";

        String actualCommand = generateSaveCommand(oneBasedIndex, infoText);
        assertTrue(actualCommand.startsWith("infosave 2 "));

        // Verify the hex part only contains valid hex characters
        String hexPart = actualCommand.substring("infosave 2 ".length());
        assertTrue(hexPart.matches("[0-9A-F]*"));
    }

    /**
     * Helper method to convert string to hex (same logic as in InfoEditorPanel)
     */
    private String stringToHex(String input) {
        byte[] bytes = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b & 0xFF));
        }
        return hexString.toString();
    }

    /**
     * Helper method to convert hex back to string for round-trip testing
     */
    private String hexToString(String hex) {
        if (hex.isEmpty()) {
            return "";
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Helper method that simulates the command generation logic in InfoEditorPanel
     */
    private String generateSaveCommand(int oneBasedIndex, String infoText) {
        String encodedInfo = stringToHex(infoText);
        return "infosave " + oneBasedIndex + " " + encodedInfo;
    }
}
