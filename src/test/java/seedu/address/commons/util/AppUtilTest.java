package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class AppUtilTest {

    @Test
    public void getImage_exitingImage() {
        try {
            assertNotNull(AppUtil.getImage("/images/address_book_32.png"));
        } catch (RuntimeException ex) {
            boolean toolkitUnavailable = ex.getMessage() != null && ex.getMessage().contains("No toolkit found");
            if (toolkitUnavailable) {
                Assumptions.assumeTrue(false, "JavaFX toolkit unavailable in test environment");
            }
            throw ex;
        }
    }

    @Test
    public void getImage_nullGiven_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AppUtil.getImage(null));
    }

    @Test
    public void checkArgument_true_nothingHappens() {
        AppUtil.checkArgument(true);
        AppUtil.checkArgument(true, "");
    }

    @Test
    public void checkArgument_falseWithoutErrorMessage_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> AppUtil.checkArgument(false));
    }

    @Test
    public void checkArgument_falseWithErrorMessage_throwsIllegalArgumentException() {
        String errorMessage = "error message";
        assertThrows(IllegalArgumentException.class, errorMessage, () -> AppUtil.checkArgument(false, errorMessage));
    }
}
