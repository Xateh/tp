package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class WarningTest {

    @Test
    public void duplicateInputIgnored_validMessage_success() {
        Warning warning = Warning.duplicateInputIgnored("Duplicate tags ignored for Alice");
        assertEquals(Warning.Type.DUPLICATE_INPUT_IGNORED, warning.getType());
        assertEquals("Duplicate tags ignored for Alice", warning.getMessage());
        assertEquals("Duplicate input ignored: Duplicate tags ignored for Alice", warning.formatForDisplay());
    }

    @Test
    public void ignoredBlankKeywords_validMessage_success() {
        Warning warning = Warning.ignoredBlankKeywords("Blank keywords were ignored");
        assertEquals(Warning.Type.IGNORED_BLANK_KEYWORDS, warning.getType());
        assertEquals("Blank keywords were ignored", warning.getMessage());
        assertEquals("Ignored blank keywords: Blank keywords were ignored", warning.formatForDisplay());
    }

    @Test
    public void ignoredMultiwordKeywords_validMessage_success() {
        Warning warning = Warning.ignoredMultiwordKeywords("Multi-word keywords ignored: multi word");
        assertEquals(Warning.Type.IGNORED_MULTIWORD_KEYWORDS, warning.getType());
        assertEquals("Multi-word keywords ignored: multi word", warning.getMessage());
        String expected = "Ignored multi-word keywords: "
                + "Multi-word keywords ignored: multi word";
        assertEquals(expected, warning.formatForDisplay());
    }

    @Test
    public void duplicateInputIgnored_nullMessage_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Warning.duplicateInputIgnored(null));
    }

    @Test
    public void duplicateInputIgnored_blankMessage_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Warning.duplicateInputIgnored("   "));
    }

    @Test
    public void equalsAndHashCode() {
        Warning first = Warning.duplicateInputIgnored("duplicate input");
        Warning second = Warning.duplicateInputIgnored("duplicate input");
        Warning different = Warning.duplicateInputIgnored("different input");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
    }
}
