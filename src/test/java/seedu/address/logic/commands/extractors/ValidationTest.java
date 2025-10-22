package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.BareCommand.BareCommandBuilder;

/**
 * Contains tests for the {@code Validation} utility class.
 */
public class ValidationTest {

    // --- ParameterKind constants for readability ---
    private static final ParameterKind NORMAL = ParameterKind.NORMAL;
    private static final ParameterKind ADDITIVE = ParameterKind.ADDITIVE;
    private static final ParameterKind SUBTRACTIVE = ParameterKind.SUBTRACTIVE;

    @Nested
    class ValidateParameterTests {

        @Test
        public void validateParameter_validParamSingleKind_returnsValue() throws ValidationException {
            // Case: "command value1" (expecting NORMAL at index 0)
            BareCommand cmd = new BareCommandBuilder().addParameter("value1").build();
            assertEquals("value1", Validation.validateParameter(cmd, 0, NORMAL));
        }

        @Test
        public void validateParameter_validParamMultipleKinds_returnsValue() throws ValidationException {
            // Case: "command +value1" (expecting NORMAL or ADDITIVE at index 0)
            BareCommand cmdAdditive = new BareCommandBuilder().addParameter(ADDITIVE, "value1").build();
            assertEquals("value1", Validation.validateParameter(cmdAdditive, 0, NORMAL, ADDITIVE));

            // Case: "command value2" (expecting NORMAL or ADDITIVE at index 0)
            BareCommand cmdNormal = new BareCommandBuilder().addParameter(NORMAL, "value2").build();
            assertEquals("value2", Validation.validateParameter(cmdNormal, 0, NORMAL, ADDITIVE));
        }

        @Test
        public void validateParameter_validParamSecondPosition_returnsValue() throws ValidationException {
            // Case: "command value1 -value2" (expecting SUBTRACTIVE at index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("value1")
                    .addParameter(SUBTRACTIVE, "value2")
                    .build();
            assertEquals("value2", Validation.validateParameter(cmd, 1, SUBTRACTIVE));
        }

        @Test
        public void validateParameter_missingParameter_throwsValidationException() {
            // Case: "command value1" (but expecting param at index 1)
            BareCommand cmd = new BareCommandBuilder().addParameter("value1").build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateParameter(cmd, 1, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_SINGLE, 1), e.getMessage());
        }

        @Test
        public void validateParameter_incorrectParameterKind_throwsValidationException() {
            // Case: "command +value1" (but expecting NORMAL)
            BareCommand cmd = new BareCommandBuilder().addParameter(ADDITIVE, "value1").build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateParameter(cmd, 0, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    0, Arrays.toString(new ParameterKind[]{NORMAL}), ADDITIVE), e.getMessage());
        }

        @Test
        public void validateParameter_incorrectParameterKindMultiple_throwsValidationException() {
            // Case: "command value1" (but expecting ADDITIVE or SUBTRACTIVE)
            BareCommand cmd = new BareCommandBuilder().addParameter(NORMAL, "value1").build();
            ParameterKind[] expectedKinds = {ADDITIVE, SUBTRACTIVE};

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateParameter(cmd, 0, expectedKinds);
            });
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    0, Arrays.toString(expectedKinds), NORMAL), e.getMessage());
        }

        @Test
        public void validateParameter_nullBareCommand_throwsNullPointerException() {
            // Test: validateParameter(null, 0, NORMAL)
            assertThrows(NullPointerException.class, () -> {
                Validation.validateParameter(null, 0, NORMAL);
            });
        }
    }

    @Nested
    class ValidateVariableParametersTests {

        @Test
        public void validateVariableParameters_zeroParams_returnsEmptyList() throws ValidationException {
            // Case: "command" (validating from index 0)
            BareCommand cmd = new BareCommandBuilder().build();
            List<String> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_zeroParamsFromStartIndex_returnsEmptyList() throws ValidationException {
            // Case: "command value1" (validating from index 1)
            BareCommand cmd = new BareCommandBuilder().addParameter("value1").build();
            List<String> result = Validation.validateVariableParameters(cmd, 1, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_oneValidParam_returnsListOfOne() throws ValidationException {
            // Case: "command value1" (validating from index 0)
            BareCommand cmd = new BareCommandBuilder().addParameter("value1").build();
            List<String> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of("value1"), result);
        }

        @Test
        public void validateVariableParameters_multipleValidParams_returnsList() throws ValidationException {
            // Case: "command v1 v2 v3" (validating from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<String> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of("v1", "v2", "v3"), result);
        }

        @Test
        public void validateVariableParameters_validParamsFromStartIndex_returnsList() throws ValidationException {
            // Case: "command v1 v2 v3" (validating from index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<String> result = Validation.validateVariableParameters(cmd, 1, NORMAL);
            assertEquals(List.of("v2", "v3"), result);
        }

        @Test
        public void validateVariableParameters_validParamsMultipleKinds_returnsList() throws ValidationException {
            // Case: "command value1 +opt -sub" (validating all kinds from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter(NORMAL, "v1")
                    .addParameter(ADDITIVE, "opt")
                    .addParameter(SUBTRACTIVE, "sub")
                    .build();
            List<String> result = Validation.validateVariableParameters(cmd, 0, NORMAL, ADDITIVE, SUBTRACTIVE);
            assertEquals(List.of("v1", "opt", "sub"), result);
        }

        @Test
        public void validateVariableParameters_firstParamInvalidKind_throwsValidationException() {
            // Case: "command +opt value1" (validating from index 0, allowing only NORMAL)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter(ADDITIVE, "opt")
                    .addParameter(NORMAL, "v1")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParameters(cmd, 0, NORMAL);
            });
            // Error should be at index 0
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    0, Arrays.toString(new ParameterKind[]{NORMAL}), ADDITIVE), e.getMessage());
        }

        @Test
        public void validateVariableParameters_laterParamInvalidKind_throwsValidationException() {
            // Case: "command value1 -sub" (validating from index 0, allowing only NORMAL)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter(NORMAL, "v1")
                    .addParameter(SUBTRACTIVE, "sub")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParameters(cmd, 0, NORMAL);
            });
            // Error should be at index 1
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    1, Arrays.toString(new ParameterKind[]{NORMAL}), SUBTRACTIVE), e.getMessage());
        }

        @Test
        public void validateVariableParameters_nullBareCommand_throwsNullPointerException() {
            // Test: validateVariableParameters(null, 0, NORMAL)
            assertThrows(NullPointerException.class, () -> {
                Validation.validateVariableParameters(null, 0, NORMAL);
            });
        }
    }

    @Nested
    class ValidateVariableParametersWithMinimumMultiplicityTests {

        @Test
        public void validateVariableParameters_countEqualsMin_returnsList() throws ValidationException {
            // Case: "command value1 value2" (min 2, from index 0)
            BareCommand cmd = new BareCommandBuilder().addParameter("v1").addParameter("v2").build();
            List<String> result = Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 2, NORMAL);
            assertEquals(List.of("v1", "v2"), result);
        }

        @Test
        public void validateVariableParameters_countGreaterThanMin_returnsList() throws ValidationException {
            // Case: "command value1 value2 value3" (min 2, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<String> result = Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 2, NORMAL);
            assertEquals(List.of("v1", "v2", "v3"), result);
        }

        @Test
        public void validateVariableParameters_countEqualsMinFromStartIndex_returnsList() throws ValidationException {
            // Case: "command prefix v1 v2" (min 2, from index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("prefix")
                    .addParameter("v1")
                    .addParameter("v2")
                    .build();
            List<String> result = Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 1, 2, NORMAL);
            assertEquals(List.of("v1", "v2"), result);
        }

        @Test
        public void validateVariableParameters_minZeroCountZero_returnsEmptyList() throws ValidationException {
            // Case: "command" (min 0, from index 0)
            BareCommand cmd = new BareCommandBuilder().build();
            List<String> result = Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 0, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_minZeroCountOne_returnsList() throws ValidationException {
            // Case: "command value1" (min 0, from index 0)
            BareCommand cmd = new BareCommandBuilder().addParameter("v1").build();
            List<String> result = Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 0, NORMAL);
            assertEquals(List.of("v1"), result);
        }

        @Test
        public void validateVariableParameters_countLessThanMin_throwsValidationException() {
            // Case: "command value1" (min 2, from index 0)
            BareCommand cmd = new BareCommandBuilder().addParameter("v1").build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 2, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE, 2, 0, 1),
                    e.getMessage());
        }

        @Test
        public void validateVariableParameters_countZeroMinNonZero_throwsValidationException() {
            // Case: "command" (min 1, from index 0)
            BareCommand cmd = new BareCommandBuilder().build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 1, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE, 1, 0, 0),
                    e.getMessage());
        }

        @Test
        public void validateVariableParameters_invalidKindBeforeMinMet_throwsValidationException() {
            // Case: "command value1 +opt" (min 2, from index 0, expecting NORMAL)
            // This should fail validation *before* the multiplicity check
            BareCommand cmd = new BareCommandBuilder()
                    .addParameter("v1")
                    .addParameter(ADDITIVE, "opt")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(cmd, 0, 2, NORMAL);
            });
            // Exception comes from the inner validateVariableParameters call
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    1, Arrays.toString(new ParameterKind[]{NORMAL}), ADDITIVE), e.getMessage());
        }
    }
}
