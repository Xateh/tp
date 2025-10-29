package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class StatusBarFooterTest {

    @Test
    public void getDisplayedSessionDir_withRelativeParent_returnsRelativeSessions() {
        Path addressBook = Path.of("data", "addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        assertEquals(Path.of("data", "sessions"), Path.of(displayed).normalize());
    }

    @Test
    public void getDisplayedSessionDir_withNoParent_returnsSessionsAtCwd() {
        Path addressBook = Path.of("addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        assertEquals(Path.of("sessions"), Path.of(displayed).normalize());
    }

    @Test
    public void getDisplayedSessionDir_withAbsoluteParent_returnsAbsoluteSessions() {
        Path addressBook = Path.of("/tmp/mydata/addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        assertEquals(Path.of("/tmp/mydata/sessions"), Path.of(displayed).normalize());
    }

    @Test
    public void getDisplayedSessionDir_withNull_returnsSessions() {
        String displayed = StatusBarFooter.getDisplayedSessionDir(null);
        assertEquals(Path.of("sessions"), Path.of(displayed).normalize());
    }

    @Test
    public void getDisplayedSessionDir_withDotParent_returnsSessions() {
        Path addressBook = Path.of(".", "addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        // normalized path should end with 'sessions'
        assertTrue(Path.of(displayed).normalize().endsWith(Path.of("sessions")));
    }

    @Test
    public void getDisplayedSessionDir_withMultiSegmentParent_returnsCorrectSessions() {
        Path addressBook = Path.of("a", "b", "c", "addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        assertEquals(Path.of("a", "b", "c", "sessions"), Path.of(displayed).normalize());
    }

    @Test
    public void getDisplayedSessionDir_withParentContainingUpwardsSegments_resolvesToSessions() {
        Path addressBook = Path.of("a", "b", "..", "c", "addressbook.json");
        String displayed = StatusBarFooter.getDisplayedSessionDir(addressBook);
        // After normalization it should end with sessions
        assertTrue(Path.of(displayed).normalize().endsWith(Path.of("sessions")));
    }
}
