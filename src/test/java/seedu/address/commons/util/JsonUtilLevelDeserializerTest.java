package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Level;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JsonUtil.LevelDeserializer} integration via the public JsonUtil API.
 */
class JsonUtilLevelDeserializerTest {

    private static class LevelHolder {
        private Level level;

        LevelHolder() {
        }

        LevelHolder(Level level) {
            this.level = level;
        }

        public Level getLevel() {
            return level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }
    }

    @Test
    void levelRoundTripSerializesAndDeserializesLevel() throws Exception {
        LevelHolder original = new LevelHolder(Level.WARNING);
        String json = JsonUtil.toJsonString(original);
        LevelHolder deserialized = JsonUtil.fromJsonString(json, LevelHolder.class);
        assertEquals(original.getLevel(), deserialized.getLevel());
    }
}
