package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.AtomicMoveUnsupportedFileSystem;
import seedu.address.testutil.AtomicMoveUnsupportedFileSystemProvider;
import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_nullFile_throwsException() {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        try {
            JsonUtil.serializeObjectToJsonFile(null, obj);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException | IOException e) {
            // expected
        }
    }

    @Test
    public void serializeObjectToJsonFile_nullObject_throwsException() {
        try {
            JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, null);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        } catch (Exception e) {
            throw new AssertionError("Expected NullPointerException, got " + e);
        }
    }

    @Test
    public void deserializeObjectFromJsonFile_nullFile_throwsException() {
        try {
            JsonUtil.deserializeObjectFromJsonFile(null, SerializableTestClass.class);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException | IOException e) {
            // expected
        }
    }

    @Test
    public void deserializeObjectFromJsonFile_invalidFile_throwsException() throws IOException {
        Path invalidFile = TestUtil.getFilePathInSandboxFolder("invalid.json");
        FileUtil.writeToFile(invalidFile, "not a json");
        try {
            JsonUtil.deserializeObjectFromJsonFile(invalidFile, SerializableTestClass.class);
            throw new AssertionError("Expected IOException");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void readJsonFile_fileNotExist_returnsEmpty() throws Exception {
        Path nonExistent = TestUtil.getFilePathInSandboxFolder("doesnotexist.json");
        assertEquals(JsonUtil.readJsonFile(nonExistent, SerializableTestClass.class), java.util.Optional.empty());
    }

    @Test
    public void readJsonFile_validJson_returnsParsedObject() throws Exception {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        Path file = TestUtil.getFilePathInSandboxFolder("validRead.json");
        JsonUtil.saveJsonFile(obj, file);

        Optional<SerializableTestClass> result = JsonUtil.readJsonFile(file, SerializableTestClass.class);

        assertTrue(result.isPresent());
        assertEquals(obj.getName(), result.get().getName());
        assertEquals(obj.getListOfLocalDateTimes(), result.get().getListOfLocalDateTimes());
        assertEquals(obj.getMapOfIntegerToString(), result.get().getMapOfIntegerToString());
    }

    @Test
    public void readJsonFile_invalidJson_throwsDataLoadingException() throws Exception {
        Path invalidFile = TestUtil.getFilePathInSandboxFolder("invalid2.json");
        FileUtil.writeToFile(invalidFile, "not a json");
        try {
            JsonUtil.readJsonFile(invalidFile, SerializableTestClass.class);
            throw new AssertionError("Expected DataLoadingException");
        } catch (seedu.address.commons.exceptions.DataLoadingException e) {
            // expected
        }
    }

    @Test
    public void saveJsonFile_nulls_throwsException() {
        try {
            JsonUtil.saveJsonFile(null, SERIALIZATION_FILE);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException | IOException e) {
            // expected
        }
        try {
            JsonUtil.saveJsonFile(new SerializableTestClass(), null);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException | IOException e) {
            // expected
        }
    }

    @Test
    public void fromJsonString_invalidJson_throwsException() {
        try {
            JsonUtil.fromJsonString("not a json", SerializableTestClass.class);
            throw new AssertionError("Expected IOException");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void toJsonString_null_throwsException() {
        try {
            JsonUtil.toJsonString(null);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        } catch (Exception e) {
            throw new AssertionError("Expected NullPointerException, got " + e);
        }
    }

    @Test
    public void toJsonStringAndFromJsonStringRoundTrip() throws Exception {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        String json = JsonUtil.toJsonString(obj);
        SerializableTestClass deserialized = JsonUtil.fromJsonString(json, SerializableTestClass.class);
        assertEquals(obj.getName(), deserialized.getName());
        assertEquals(obj.getListOfLocalDateTimes(), deserialized.getListOfLocalDateTimes());
        assertEquals(obj.getMapOfIntegerToString(), deserialized.getMapOfIntegerToString());
    }

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    // Defensive and round-trip tests above

    @Test
    public void saveJsonFile_fileWithNoParent_succeeds() throws IOException {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        Path file = Path.of("serialize_noparent.json");
        try {
            JsonUtil.saveJsonFile(obj, file);
            assertEquals(FileUtil.readFromFile(file), JsonUtil.toJsonString(obj));
        } finally {
            java.nio.file.Files.deleteIfExists(file);
        }
    }

    @Test
    public void saveJsonFile_fileInCurrentDirectory_succeeds() throws IOException {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        Path file = Path.of("./serialize_currentdir.json");
        try {
            JsonUtil.saveJsonFile(obj, file);
            assertEquals(FileUtil.readFromFile(file), JsonUtil.toJsonString(obj));
        } finally {
            java.nio.file.Files.deleteIfExists(file);
        }
    }

    @Test
    public void saveJsonFile_atomicMoveNotSupported_fallback() throws Exception {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        AtomicMoveUnsupportedFileSystemProvider provider =
            new AtomicMoveUnsupportedFileSystemProvider(FileSystems.getDefault().provider());
        @SuppressWarnings("resource")
        AtomicMoveUnsupportedFileSystem fileSystem =
            new AtomicMoveUnsupportedFileSystem(FileSystems.getDefault(), provider);

        Path wrappedTarget = fileSystem.wrap(TestUtil.getFilePathInSandboxFolder("simulate/atomic/fallback.json"));
        Path actualTarget = fileSystem.unwrap(wrappedTarget);

        try {
            Files.deleteIfExists(actualTarget);

            JsonUtil.saveJsonFile(obj, wrappedTarget);

            assertTrue(provider.wasAtomicMoveAttempted());
            assertTrue(provider.wasFallbackMoveUsed());
            assertEquals(JsonUtil.toJsonString(obj), FileUtil.readFromFile(actualTarget));
        } finally {
            Files.deleteIfExists(actualTarget);
            Path dir = actualTarget.getParent();
            Path sandboxRoot = TestUtil.getFilePathInSandboxFolder("").normalize();
            while (dir != null && sandboxRoot != null && dir.startsWith(sandboxRoot) && !dir.equals(sandboxRoot)) {
                if (!Files.deleteIfExists(dir)) {
                    break;
                }
                dir = dir.getParent();
            }
        }
    }

    @Test
    public void saveJsonFile_missingParentDirectories_createsDirectories() throws Exception {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();
        Path nestedFile = TestUtil.getFilePathInSandboxFolder("nested/dir/save.json");

        Path sandboxRoot = TestUtil.getFilePathInSandboxFolder("").normalize();

        try {
            JsonUtil.saveJsonFile(obj, nestedFile);
            assertTrue(Files.exists(nestedFile));
            assertEquals(JsonUtil.toJsonString(obj), FileUtil.readFromFile(nestedFile));
        } finally {
            Files.deleteIfExists(nestedFile);
            Path dir = nestedFile.getParent();
            while (dir != null && sandboxRoot != null && dir.startsWith(sandboxRoot) && !dir.equals(sandboxRoot)) {
                if (!Files.deleteIfExists(dir)) {
                    break;
                }
                dir = dir.getParent();
            }
        }
    }

    @Test
    public void readJsonFile_nullSearchKeywords_returnsEmptyList() throws Exception {
        // Simulate a session JSON with null searchKeywords
        String json = "{"
            + "\"formatVersion\":\"2.0\","
            + "\"savedAt\":\"2025-10-14T00:00:00Z\","
            + "\"addressBook\":{\"persons\":[]},"
            + "\"searchKeywords\":null,"
            + "\"guiSettings\":{\"windowWidth\":800,\"windowHeight\":600,\"windowX\":10,\"windowY\":10}"
            + "}";
        Class<?> sessionClass = Class.forName("seedu.address.storage.JsonSerializableSession");
        Object session = JsonUtil.fromJsonString(json, sessionClass);
        java.lang.reflect.Method toModelType = sessionClass.getDeclaredMethod("toModelType");
        toModelType.setAccessible(true);
        Object result = toModelType.invoke(session);
        java.lang.reflect.Method getSearchKeywords = result.getClass().getMethod("getSearchKeywords");
        Object keywords = getSearchKeywords.invoke(result);
        assertTrue(keywords instanceof java.util.List);
        assertTrue(((java.util.List<?>) keywords).isEmpty());
    }

    @Test
    public void levelDeserializer_invalidLevel_throwsIllegalArgumentException() throws Exception {
        // instantiate the inner LevelDeserializer via reflection
        Class<?> cls = JsonUtil.LevelDeserializer.class;
        java.lang.reflect.Constructor<?> ctor = cls.getDeclaredConstructor(Class.class);
        ctor.setAccessible(true);
        Object deserializer = ctor.newInstance(java.util.logging.Level.class);

        // call the protected _deserialize method via reflection
        java.lang.reflect.Method m = cls.getDeclaredMethod(
            "_deserialize",
            String.class,
            com.fasterxml.jackson.databind.DeserializationContext.class);
        m.setAccessible(true);

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                m.invoke(deserializer, "NOT_A_LEVEL", null);
            } catch (java.lang.reflect.InvocationTargetException ite) {
                throw ite.getCause();
            }
        });
    }

    @Test
    public void saveJsonFile_directoryFallback_throws() throws Exception {
        SerializableTestClass obj = new SerializableTestClass();
        obj.setTestValues();

        // Use root path which has no parent and whose toAbsolutePath().getParent() is also null on Unix.
        // This forces the code path that sets directory to Paths.get(".") and fileName to "session".
        Path rootPath = Path.of("/");

        try {
            // We expect an IOException (permission or invalid target) when trying to move temp file to root.
            assertThrows(IOException.class, () -> JsonUtil.saveJsonFile(obj, rootPath));
        } finally {
            // nothing to cleanup — JsonUtil.deleteIfExists should have cleaned temp
        }
    }

    @Test
    public void instantiateJsonUtil_constructorCovered() {
        // Instantiating the utility class to cover the implicit constructor/static init for 100% coverage
        JsonUtil util = new JsonUtil();
        assertNotNull(util);
    }

}
