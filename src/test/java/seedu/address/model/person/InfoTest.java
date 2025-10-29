package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InfoTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Info(null));
    }

    @Test
    public void constructor_validInfo_success() {
        Info info = new Info("Valid information");
        assertEquals("Valid information", info.value);
    }

    @Test
    public void constructor_emptyString_success() {
        Info info = new Info("");
        assertEquals("", info.value);
    }

    @Test
    public void constructor_multilineString_success() {
        String multilineInfo = "Line 1\nLine 2\nLine 3";
        Info info = new Info(multilineInfo);
        assertEquals(multilineInfo, info.value);
    }

    @Test
    public void constructor_specialCharacters_success() {
        String specialInfo = "Testing «ταБЬℓσ»: 1<2 & 4+1>3, now 20% off!";
        Info info = new Info(specialInfo);
        assertEquals(specialInfo, info.value);
    }

    @Test
    public void toString_validInfo_returnsValue() {
        Info info = new Info("Test information");
        assertEquals("Test information", info.toString());
    }

    @Test
    public void toString_emptyInfo_returnsEmptyString() {
        Info info = new Info("");
        assertEquals("", info.toString());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Info info = new Info("Test info");
        assertTrue(info.equals(info));
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        Info info1 = new Info("Test info");
        Info info2 = new Info("Test info");
        assertTrue(info1.equals(info2));
    }

    @Test
    public void equals_differentValue_returnsFalse() {
        Info info1 = new Info("Test info 1");
        Info info2 = new Info("Test info 2");
        assertFalse(info1.equals(info2));
    }

    @Test
    public void equals_null_returnsFalse() {
        Info info = new Info("Test info");
        assertFalse(info.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Info info = new Info("Test info");
        assertFalse(info.equals("Test info"));
    }

    @Test
    public void hashCode_sameValue_returnsSameHashCode() {
        Info info1 = new Info("Test info");
        Info info2 = new Info("Test info");
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    public void hashCode_differentValue_returnsDifferentHashCode() {
        Info info1 = new Info("Test info 1");
        Info info2 = new Info("Test info 2");
        assertNotEquals(info1.hashCode(), info2.hashCode());
    }
}
