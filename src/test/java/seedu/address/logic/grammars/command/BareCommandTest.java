package seedu.address.logic.grammars.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.grammars.command.BareCommand.BareCommandBuilder;
import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BareCommandTest {
    @Test
    public void parameters_kindAndPredicateMatch_success() {
        Parameter normalParameter = new BareCommand.Parameter(ParameterKind.NORMAL, "value");
        assertEquals(ParameterKind.NORMAL, normalParameter.getKind());
        assertTrue(normalParameter.isNormal());

        Parameter additiveParameter = new BareCommand.Parameter(ParameterKind.ADDITIVE, "value");
        assertEquals(ParameterKind.ADDITIVE, additiveParameter.getKind());
        assertTrue(additiveParameter.isAdditive());

        Parameter subtractiveParameter = new BareCommand.Parameter(ParameterKind.SUBTRACTIVE, "value");
        assertEquals(ParameterKind.SUBTRACTIVE, subtractiveParameter.getKind());
        assertTrue(subtractiveParameter.isSubtractive());
    }

    @Test
    public void parse_simpleCommand_success() {
        String cmdString = "test";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
    }

    @Test
    public void parse_simpleCommandWithParameters_success() {
        String cmdString = "test param0 +param1 -param2";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(3, cmd.getAllParameters().size());
        assertTrue(cmd.getParameter(0).isNormal());
        assertEquals("param0", cmd.getParameter(0).getValue());
        assertTrue(cmd.getParameter(1).isAdditive());
        assertEquals("param1", cmd.getParameter(1).getValue());
        assertTrue(cmd.getParameter(2).isSubtractive());
        assertEquals("param2", cmd.getParameter(2).getValue());
    }

    @Test
    public void parseCommandWithBooleanOptions_success() {
        String cmdString = "test /opt1 /opt2";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
        assertTrue(cmd.hasOption("opt1"));
        assertEquals(0, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.hasOption("opt2"));
        assertEquals(0, cmd.getOptionMultiplicity("opt2"));
        assertEquals(-1, cmd.getOptionMultiplicity("opt3"));
    }

    @Test
    public void parse_commandWithKeyValueOptions_success() {
        String cmdString = "test /opt1:\"long value\" /opt2:\"single\"";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
        assertTrue(cmd.getOptionValue("opt1").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt1").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.getOptionValue("opt2").isPresent());
        assertEquals("single", cmd.getOptionValue("opt2").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt2"));
    }

    @Test
    public void parse_commandWithRepeatedKeyValueOptions_success() {
        String cmdString = "test /opt:\"long value\" /opt:\"single\"";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("test", cmd.getImperative());
        assertEquals(0, cmd.getAllParameters().size());
        assertTrue(cmd.getOptionValue("opt").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt").get());
        assertTrue(cmd.getOptionAllValues("opt").isPresent());
        List<String> expectedOptionValues = List.of("long value", "single");
        assertEquals(expectedOptionValues, cmd.getOptionAllValues("opt").get());
        assertEquals(2, cmd.getOptionMultiplicity("opt"));
    }

    @Test
    public void parse_commandWithMissingOptions_success() {
        String cmdString = "test";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertFalse(cmd.hasOption("opt"));
        assertFalse(cmd.getOptionValue("opt").isPresent());
        assertFalse(cmd.getOptionAllValues("opt").isPresent());
        assertEquals(-1, cmd.getOptionMultiplicity("opt"));
    }

    @Test
    public void parse_commandWithRepeatedBooleanOptions_success() {
        String cmdString = "complex /opt /opt";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertTrue(cmd.hasOption("opt"));
        assertTrue(cmd.getOptionValue("opt").isEmpty());
        assertEquals(0, cmd.getOptionMultiplicity("opt"));
        assertFalse(cmd.hasOption("nonopt"));
        assertTrue(cmd.getOptionValue("nonopt").isEmpty());
        assertEquals(-1, cmd.getOptionMultiplicity("nonopt"));
    }

    @Test
    public void parse_complexCommand_success() {
        String cmdString = "complex param0 +param1 -param2 /opt1:\"long value\" /opt2:\"single\"/ opt3 /opt3 "
                + "/opt2:another /opt4 /opt4:value";

        BareCommand cmd = assertDoesNotThrow(() -> BareCommand.parse(cmdString));

        assertEquals("complex", cmd.getImperative());
        assertEquals(3, cmd.getAllParameters().size());
        assertTrue(cmd.getParameter(0).isNormal());
        assertEquals("param0", cmd.getParameter(0).getValue());
        assertTrue(cmd.getParameter(1).isAdditive());
        assertEquals("param1", cmd.getParameter(1).getValue());
        assertTrue(cmd.getParameter(2).isSubtractive());
        assertEquals("param2", cmd.getParameter(2).getValue());
        assertTrue(cmd.getOptionValue("opt1").isPresent());
        assertEquals("long value", cmd.getOptionValue("opt1").get());
        assertEquals(1, cmd.getOptionMultiplicity("opt1"));
        assertTrue(cmd.getOptionValue("opt2").isPresent());
        assertEquals("single", cmd.getOptionValue("opt2").get());
        assertEquals(2, cmd.getOptionMultiplicity("opt2"));
        assertTrue(cmd.hasOption("opt3"));
        assertTrue(cmd.getOptionValue("opt3").isEmpty());
        assertEquals(0, cmd.getOptionMultiplicity("opt3"));
        assertTrue(cmd.hasOption("opt4"));
        assertTrue(cmd.getOptionValue("opt4").isPresent());
        assertEquals(1, cmd.getOptionMultiplicity("opt4"));
        assertFalse(cmd.hasOption("opt5"));
        assertTrue(cmd.getOptionValue("opt5").isEmpty());
        assertEquals(-1, cmd.getOptionMultiplicity("opt5"));
    }

    @Nested
    class ParameterTest {

        private final Parameter paramNormal = new Parameter(ParameterKind.NORMAL, "val1");
        private final Parameter paramAdditive = new Parameter(ParameterKind.ADDITIVE, "val2");
        private final Parameter paramSubtractive = new Parameter(ParameterKind.SUBTRACTIVE, "val3");

        @Test
        public void getters_validData_success() {
            assertEquals(ParameterKind.NORMAL, paramNormal.getKind());
            assertEquals("val1", paramNormal.getValue());

            assertEquals(ParameterKind.ADDITIVE, paramAdditive.getKind());
            assertEquals("val2", paramAdditive.getValue());
        }

        @Test
        public void isX_allKinds_returnsCorrectBoolean() {
            assertTrue(paramNormal.isNormal());
            assertFalse(paramNormal.isAdditive());
            assertFalse(paramNormal.isSubtractive());

            assertFalse(paramAdditive.isNormal());
            assertTrue(paramAdditive.isAdditive());
            assertFalse(paramAdditive.isSubtractive());

            assertFalse(paramSubtractive.isNormal());
            assertFalse(paramSubtractive.isAdditive());
            assertTrue(paramSubtractive.isSubtractive());
        }

        @Test
        public void equals_variousScenarios_correctResult() {
            Parameter paramNormalCopy = new Parameter(ParameterKind.NORMAL, "val1");
            Parameter paramDiffValue = new Parameter(ParameterKind.NORMAL, "diff");
            Parameter paramDiffKind = new Parameter(ParameterKind.ADDITIVE, "val1");

            // --- Same object ---
            assertEquals(paramNormal, paramNormal);

            // --- Same values ---
            assertEquals(paramNormal, paramNormalCopy);

            // --- Different values ---
            assertNotEquals(paramNormal, paramDiffValue);

            // --- Different kinds ---
            assertNotEquals(paramNormal, paramDiffKind);

            // --- Different type ---
            assertNotEquals(paramNormal, "val1");

            // --- Null ---
            assertNotEquals(paramNormal, null);
        }
    }

    @Nested
    class BareCommandBuilderTest {

        @Test
        public void build_noImperative_throwsIllegalStateException() {
            BareCommandBuilder builder = new BareCommandBuilder().addParameter("p1");
            Exception e = assertThrows(IllegalStateException.class, builder::build);
            assertTrue(e.getMessage().contains("Imperative not declared"));
        }

        @Test
        public void build_minimalCommand_success() {
            BareCommand cmd = new BareCommandBuilder().setImperative("list").build();
            assertEquals("list", cmd.getImperative());
            assertEquals(0, cmd.parameterCount());
            assertTrue(cmd.getAllOptions().isEmpty());
        }

        @Test
        public void build_complexCommand_success() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("find")
                    .addParameter("p1")
                    .addParameter(ParameterKind.ADDITIVE, "p2")
                    .setOption("flag")
                    .setOption("key", "v1")
                    .setOption("key", "v2")
                    .build();

            assertEquals("find", cmd.getImperative());
            assertEquals(2, cmd.parameterCount());
            assertEquals(new Parameter(ParameterKind.NORMAL, "p1"), cmd.getParameter(0));
            assertEquals(new Parameter(ParameterKind.ADDITIVE, "p2"), cmd.getParameter(1));

            assertTrue(cmd.hasOption("flag"));
            assertTrue(cmd.hasOption("key"));
            assertEquals(List.of("v1", "v2"), cmd.getOptionAllValues("key").orElseThrow());
        }

        @Test
        public void build_optionTypes_correctStorage() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("test")
                    .setOption("flag") // Boolean flag
                    .setOption("single", "val") // Single value
                    .setOption("multi", "v1") // Multi-value
                    .setOption("multi", "v2")
                    .build();

            // Flag should exist with 0 values
            assertTrue(cmd.hasOption("flag"));
            assertEquals(Optional.of(List.of()), cmd.getOptionAllValues("flag"));
            assertEquals(Optional.empty(), cmd.getOptionValue("flag"));
            assertEquals(0, cmd.getOptionMultiplicity("flag"));

            // Single value
            assertTrue(cmd.hasOption("single"));
            assertEquals(Optional.of(List.of("val")), cmd.getOptionAllValues("single"));
            assertEquals(Optional.of("val"), cmd.getOptionValue("single"));
            assertEquals(1, cmd.getOptionMultiplicity("single"));

            // Multi-value
            assertTrue(cmd.hasOption("multi"));
            assertEquals(Optional.of(List.of("v1", "v2")), cmd.getOptionAllValues("multi"));
            assertEquals(Optional.of("v1"), cmd.getOptionValue("multi")); // getOptionValue gets first
            assertEquals(2, cmd.getOptionMultiplicity("multi"));
        }

        @Test
        public void build_immutabilityCheck_success() {
            // Checks that the builder can be reused and that built commands are immutable
            BareCommandBuilder builder = new BareCommandBuilder()
                    .setImperative("cmd")
                    .addParameter("p1")
                    .setOption("key", "v1");

            // Build first command
            BareCommand cmd1 = builder.build();
            assertEquals(1, cmd1.parameterCount());
            assertEquals(List.of("v1"), cmd1.getOptionAllValues("key").get());

            // Modify builder
            builder.addParameter("p2")
                    .setOption("key", "v2")
                    .setOption("newKey", "v3");

            // Build second command
            BareCommand cmd2 = builder.build();

            // Verify cmd1 is unchanged
            assertEquals(1, cmd1.parameterCount());
            assertEquals(List.of("v1"), cmd1.getOptionAllValues("key").get());
            assertFalse(cmd1.hasOption("newKey"));

            // Verify cmd2 has new data
            assertEquals(2, cmd2.parameterCount());
            assertEquals(List.of("v1", "v2"), cmd2.getOptionAllValues("key").get());
            assertTrue(cmd2.hasOption("newKey"));
        }
    }

    @Nested
    class BareCommandGettersTest {

        private BareCommand complexCommand;

        @BeforeEach
        public void setUp() {
            // This command is used for all getter tests
            complexCommand = new BareCommandBuilder()
                    .setImperative("edit")
                    .addParameter("1") // index 0
                    .addParameter(ParameterKind.SUBTRACTIVE, "tag1") // index 1
                    .setOption("flag") // flag, 0 values
                    .setOption("name", "New Name") // single value
                    .setOption("tag", "friend") // multi-value
                    .setOption("tag", "work")
                    .build();
        }

        @Test
        public void getImperative_success() {
            assertEquals("edit", complexCommand.getImperative());
        }

        @Test
        public void parameterCount_success() {
            assertEquals(2, complexCommand.parameterCount());
        }

        @Test
        public void getParameter_validIndex_success() {
            assertEquals(new Parameter(ParameterKind.NORMAL, "1"), complexCommand.getParameter(0));
            assertEquals(new Parameter(ParameterKind.SUBTRACTIVE, "tag1"), complexCommand.getParameter(1));
        }

        @Test
        public void getParameter_invalidIndex_throwsIndexOutOfBoundsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> complexCommand.getParameter(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> complexCommand.getParameter(2)); // count is 2
        }

        @Test
        public void getAllParameters_returnsUnmodifiableList() {
            List<Parameter> params = complexCommand.getAllParameters();
            assertEquals(2, params.size());
            // Check that the returned list is unmodifiable
            assertThrows(UnsupportedOperationException.class, () -> params.add(
                    new Parameter(ParameterKind.NORMAL, "p3")));
        }

        @Test
        public void hasOption_variousScenarios_correctResult() {
            assertTrue(complexCommand.hasOption("flag"));
            assertTrue(complexCommand.hasOption("name"));
            assertTrue(complexCommand.hasOption("tag"));
            assertFalse(complexCommand.hasOption("nonexistent"));
        }

        @Test
        public void getOptionValue_variousScenarios_correctOptional() {
            // Returns first value for multi-value option
            assertEquals(Optional.of("friend"), complexCommand.getOptionValue("tag"));
            // Returns value for single-value option
            assertEquals(Optional.of("New Name"), complexCommand.getOptionValue("name"));
            // Returns empty for flag option (no values)
            assertEquals(Optional.empty(), complexCommand.getOptionValue("flag"));
            // Returns empty for non-existent option
            assertEquals(Optional.empty(), complexCommand.getOptionValue("nonexistent"));
        }

        @Test
        public void getOptionAllValues_variousScenarios_correctOptional() {
            // Returns all values for multi-value option
            assertEquals(Optional.of(List.of("friend", "work")), complexCommand.getOptionAllValues("tag"));
            // Returns all values for single-value option
            assertEquals(Optional.of(List.of("New Name")), complexCommand.getOptionAllValues("name"));
            // Returns empty list for flag option
            assertEquals(Optional.of(List.of()), complexCommand.getOptionAllValues("flag"));
            // Returns empty for non-existent option
            assertEquals(Optional.empty(), complexCommand.getOptionAllValues("nonexistent"));
        }

        @Test
        public void getOptionAllValues_returnsUnmodifiableList() {
            Optional<List<String>> values = complexCommand.getOptionAllValues("tag");
            assertTrue(values.isPresent());
            List<String> list = values.get();
            // Check that the returned list is unmodifiable
            assertThrows(UnsupportedOperationException.class, () -> list.add("newTag"));
        }

        @Test
        public void getOptionMultiplicity_variousScenarios_correctCount() {
            assertEquals(2, complexCommand.getOptionMultiplicity("tag"));
            assertEquals(1, complexCommand.getOptionMultiplicity("name"));
            assertEquals(0, complexCommand.getOptionMultiplicity("flag"));
            assertEquals(-1, complexCommand.getOptionMultiplicity("nonexistent"));
        }

        @Test
        public void getVariableOptionKeys_implementationTest_returnsFilteredKeys() {
            List<String> keys = complexCommand.getVariableOptionKeys("flag", "tag", "nonexistent");

            assertFalse(keys.contains("flag"));
            assertFalse(keys.contains("tag"));
            assertTrue(keys.contains("name"));
            assertFalse(keys.contains("nonexistent")); // Not in options
            assertEquals(1, keys.size());

            // Test with no ignores
            List<String> keys2 = complexCommand.getVariableOptionKeys();
            assertEquals(3, keys2.size());
        }

        @Test
        public void getAllOptions_returnsUnmodifiableMap() {
            Map<String, List<String>> options = complexCommand.getAllOptions();

            // Check that the map itself is unmodifiable
            assertThrows(UnsupportedOperationException.class, () -> options.put("newKey", List.of("v")));

            // Check that the lists inside the map are also unmodifiable
            List<String> tagValues = options.get("tag");
            assertThrows(UnsupportedOperationException.class, () -> tagValues.add("newTag"));
        }

        @Test
        public void getters_nullKeyChecks_throwsNullPointerException() {
            assertThrows(NullPointerException.class, () -> complexCommand.getOptionValue(null));
            assertThrows(NullPointerException.class, () -> complexCommand.getOptionAllValues(null));
            assertThrows(NullPointerException.class, () -> complexCommand.hasOption(null));
            assertThrows(NullPointerException.class, () -> complexCommand.getOptionMultiplicity(null));
        }
    }
}
