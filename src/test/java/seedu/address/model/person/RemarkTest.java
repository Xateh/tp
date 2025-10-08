package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

class RemarkTest {

    @Test
    public void constructor_nullRemark_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals() {
        Remark first = new Remark("Notes");
        Remark same = new Remark("Notes");
        Remark different = new Remark("Different");

        assertEquals(first, same);
        assertNotEquals(first, different);
    }

    @Test
    public void toString_returnsUnderlyingValue() {
        Remark remark = new Remark("Specific details");
        assertEquals("Specific details", remark.toString());
    }
}
