
package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationContext;

public class LevelDeserializerTest {
    @Test
    public void deserialize_validLevelString_returnsLevel() throws IOException {
        JsonUtil.LevelDeserializer deserializer = new JsonUtil.LevelDeserializer(Level.class);
        DeserializationContext ctxt = null; // not used in _deserialize
        assertEquals(Level.INFO, deserializer._deserialize("INFO", ctxt));
        assertEquals(Level.WARNING, deserializer._deserialize("WARNING", ctxt));
        assertEquals(Level.FINE, deserializer._deserialize("FINE", ctxt));
    }

    @Test
    public void deserialize_invalidLevelString_throwsException() {
        JsonUtil.LevelDeserializer deserializer = new JsonUtil.LevelDeserializer(Level.class);
        DeserializationContext ctxt = null;
        assertThrows(IllegalArgumentException.class, () -> deserializer._deserialize("NOTALEVEL", ctxt));
    }

    @Test
    public void handledType_returnsLevelClass() {
        JsonUtil.LevelDeserializer deserializer = new JsonUtil.LevelDeserializer(Level.class);
        assertEquals(Level.class, deserializer.handledType());
    }
}
