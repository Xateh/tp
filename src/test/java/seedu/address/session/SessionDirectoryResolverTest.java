package seedu.address.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class SessionDirectoryResolverTest {

    @Test
    void resolve_withParent_returnsSiblingSessions() {
        Path addressBookPath = Path.of("data", "addressbook.json");
        assertEquals(Path.of("data", "sessions"), SessionDirectoryResolver.resolve(addressBookPath));
    }

    @Test
    void resolve_withoutParent_returnsDefaultSessionsDirectory() {
        Path addressBookPath = Path.of("addressbook.json");
        assertEquals(Path.of("sessions"), SessionDirectoryResolver.resolve(addressBookPath));
    }
}
