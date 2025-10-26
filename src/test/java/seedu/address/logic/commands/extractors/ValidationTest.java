package seedu.address.logic.commands.extractors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.grammars.command.BareCommand.Parameter;
import static seedu.address.logic.grammars.command.BareCommand.Parameter.ParameterKind;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.ValidationException;
import seedu.address.logic.grammars.command.BareCommand;
import seedu.address.logic.grammars.command.BareCommand.BareCommandBuilder;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.Assert;

/**
 * Contains tests for the {@code Validation} utility class.
 */
public class ValidationTest {
    private static final String WHITESPACE = " \t\r\n";

    // --- ParameterKind constants for readability ---
    private static final ParameterKind NORMAL = ParameterKind.NORMAL;
    private static final ParameterKind ADDITIVE = ParameterKind.ADDITIVE;
    private static final ParameterKind SUBTRACTIVE = ParameterKind.SUBTRACTIVE;

    @Nested
    class ValidateParameterTests {

        @Test
        public void validateParameter_validParamSingleKind_returnsValue() throws ValidationException {
            // Case: "command value1" (expecting NORMAL at index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("value1")
                    .build();
            assertEquals(new Parameter(NORMAL, "value1"), Validation.validateParameter(cmd, 0, NORMAL));
        }

        @Test
        public void validateParameter_validParamMultipleKinds_returnsValue() throws ValidationException {
            // Case: "command +value1" (expecting NORMAL or ADDITIVE at index 0)
            BareCommand cmdAdditive = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter(ADDITIVE, "value1")
                    .build();
            assertEquals(new Parameter(ADDITIVE, "value1"),
                    Validation.validateParameter(cmdAdditive, 0, NORMAL, ADDITIVE));

            // Case: "command value2" (expecting NORMAL or ADDITIVE at index 0)
            BareCommand cmdNormal = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter(NORMAL, "value2")
                    .build();
            assertEquals(new Parameter(NORMAL, "value2"),
                    Validation.validateParameter(cmdNormal, 0, NORMAL, ADDITIVE));
        }

        @Test
        public void validateParameter_validParamSecondPosition_returnsValue() throws ValidationException {
            // Case: "command value1 -value2" (expecting SUBTRACTIVE at index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("value1")
                    .addParameter(SUBTRACTIVE, "value2")
                    .build();
            assertEquals(new Parameter(SUBTRACTIVE, "value2"),
                    Validation.validateParameter(cmd, 1, SUBTRACTIVE));
        }

        @Test
        public void validateParameter_missingParameter_throwsValidationException() {
            // Case: "command value1" (but expecting param at index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("value1")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateParameter(cmd, 1, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_SINGLE, 1), e.getMessage());
        }

        @Test
        public void validateParameter_incorrectParameterKind_throwsValidationException() {
            // Case: "command +value1" (but expecting NORMAL)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter(ADDITIVE, "value1")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateParameter(cmd, 0, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    0, Arrays.toString(new ParameterKind[]{NORMAL}), ADDITIVE), e.getMessage());
        }

        @Test
        public void validateParameter_incorrectParameterKindMultiple_throwsValidationException() {
            // Case: "command value1" (but expecting ADDITIVE or SUBTRACTIVE)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter(NORMAL, "value1")
                    .build();
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
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_zeroParamsFromStartIndex_returnsEmptyList() throws ValidationException {
            // Case: "command value1" (validating from index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("value1")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 1, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_oneValidParam_returnsListOfOne() throws ValidationException {
            // Case: "command value1" (validating from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("value1")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "value1")), result);
        }

        @Test
        public void validateVariableParameters_multipleValidParams_returnsList() throws ValidationException {
            // Case: "command v1 v2 v3" (validating from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 0, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v1"), new Parameter(NORMAL, "v2"),
                    new Parameter(NORMAL, "v3")), result);
        }

        @Test
        public void validateVariableParameters_validParamsFromStartIndex_returnsList() throws ValidationException {
            // Case: "command v1 v2 v3" (validating from index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 1, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v2"), new Parameter(NORMAL, "v3")), result);
        }

        @Test
        public void validateVariableParameters_validParamsMultipleKinds_returnsList() throws ValidationException {
            // Case: "command value1 +opt -sub" (validating all kinds from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter(NORMAL, "v1")
                    .addParameter(ADDITIVE, "opt")
                    .addParameter(SUBTRACTIVE, "sub")
                    .build();
            List<Parameter> result = Validation.validateVariableParameters(cmd, 0,
                    NORMAL, ADDITIVE, SUBTRACTIVE);
            assertEquals(List.of(new Parameter(NORMAL, "v1"), new Parameter(ADDITIVE, "opt"),
                    new Parameter(SUBTRACTIVE, "sub")), result);
        }

        @Test
        public void validateVariableParameters_firstParamInvalidKind_throwsValidationException() {
            // Case: "command +opt value1" (validating from index 0, allowing only NORMAL)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
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
                    .setImperative("command")
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
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .addParameter("v2")
                    .build();
            List<Parameter> result = Validation.validateVariableParametersWithMinimumMultiplicity(
                    cmd, 0, 2, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v1"), new Parameter(NORMAL, "v2")), result);
        }

        @Test
        public void validateVariableParameters_countGreaterThanMin_returnsList() throws ValidationException {
            // Case: "command value1 value2 value3" (min 2, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .addParameter("v2")
                    .addParameter("v3")
                    .build();
            List<Parameter> result = Validation.validateVariableParametersWithMinimumMultiplicity(
                    cmd, 0, 2, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v1"), new Parameter(NORMAL, "v2"),
                    new Parameter(NORMAL, "v3")), result);
        }

        @Test
        public void validateVariableParameters_countEqualsMinFromStartIndex_returnsList() throws ValidationException {
            // Case: "command prefix v1 v2" (min 2, from index 1)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("prefix")
                    .addParameter("v1")
                    .addParameter("v2")
                    .build();
            List<Parameter> result = Validation.validateVariableParametersWithMinimumMultiplicity(
                    cmd, 1, 2, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v1"), new Parameter(NORMAL, "v2")), result);
        }

        @Test
        public void validateVariableParameters_minZeroCountZero_returnsEmptyList() throws ValidationException {
            // Case: "command" (min 0, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .build();
            List<Parameter> result = Validation.validateVariableParametersWithMinimumMultiplicity(
                    cmd, 0, 0, NORMAL);
            assertEquals(List.of(), result);
        }

        @Test
        public void validateVariableParameters_minZeroCountOne_returnsList() throws ValidationException {
            // Case: "command value1" (min 0, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .build();
            List<Parameter> result = Validation.validateVariableParametersWithMinimumMultiplicity(
                    cmd, 0, 0, NORMAL);
            assertEquals(List.of(new Parameter(NORMAL, "v1")), result);
        }

        @Test
        public void validateVariableParameters_countLessThanMin_throwsValidationException() {
            // Case: "command value1" (min 2, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .build();
            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(
                        cmd, 0, 2, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE, 2, 0, 1),
                    e.getMessage());
        }

        @Test
        public void validateVariableParameters_countZeroMinNonZero_throwsValidationException() {
            // Case: "command" (min 1, from index 0)
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .build();
            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(
                        cmd, 0, 1, NORMAL);
            });
            assertEquals(String.format(Validation.MESSAGE_INSUFFICIENT_PARAMETERS_VARIABLE, 1, 0, 0),
                    e.getMessage());
        }

        @Test
        public void validateVariableParameters_invalidKindBeforeMinMet_throwsValidationException() {
            // Case: "command value1 +opt" (min 2, from index 0, expecting NORMAL)
            // This should fail validation *before* the multiplicity check
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("v1")
                    .addParameter(ADDITIVE, "opt")
                    .build();

            ValidationException e = assertThrows(ValidationException.class, () -> {
                Validation.validateVariableParametersWithMinimumMultiplicity(
                        cmd, 0, 2, NORMAL);
            });
            // Exception comes from the inner validateVariableParameters call
            assertEquals(String.format(Validation.MESSAGE_INCORRECT_PARAMETER_KIND,
                    1, Arrays.toString(new ParameterKind[]{NORMAL}), ADDITIVE), e.getMessage());
        }
    }

    @Nested
    class ValidateIndexTests {
        @Test
        public void validateIndex_validIndex_success() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("1")
                    .build();
            Index expected = Index.fromOneBased(1);

            Index index = assertDoesNotThrow(() -> Validation.validateIndex(cmd, 0));
            assertEquals(expected, index);
        }

        @Test
        public void validateIndex_largeValidIndex_success() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("100")
                    .build();
            Index expected = Index.fromOneBased(100);

            Index index = assertDoesNotThrow(() -> Validation.validateIndex(cmd, 0));
            assertEquals(expected, index);
        }


        @Test
        public void validateIndex_invalidIndexOutOfRange_throwsException() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("0")
                    .build();

            assertThrows(ValidationException.class, () -> Validation.validateIndex(cmd, 0));
        }

        @Test
        public void validateIndex_invalidIndexNotAnInteger_throwsException() {
            BareCommand cmd = new BareCommandBuilder()
                    .setImperative("command")
                    .addParameter("as")
                    .build();

            assertThrows(ValidationException.class, () -> Validation.validateIndex(cmd, 0));
        }
    }

    @Nested
    class ValidateNameTests {
        private static final String INVALID_NAME = "R@chel";
        private static final String VALID_NAME = "Rachel Walker";

        @Test
        public void validateName_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validateName((String) null));
        }

        @Test
        public void validateName_invalidValue_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validateName(INVALID_NAME));
        }

        @Test
        public void validateName_validValueWithoutWhitespace_returnsName() throws Exception {
            Name expectedName = new Name(VALID_NAME);
            assertEquals(expectedName, Validation.validateName(VALID_NAME));
        }

        @Test
        public void validateName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
            String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
            Name expectedName = new Name(VALID_NAME);
            assertEquals(expectedName, Validation.validateName(nameWithWhitespace));
        }
    }

    @Nested
    class ValidatePhoneTests {
        private static final String INVALID_PHONE = "+651234";
        private static final String VALID_PHONE = "123456";

        @Test
        public void validatePhone_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validatePhone((String) null));
        }

        @Test
        public void validatePhone_invalidValue_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validatePhone(INVALID_PHONE));
        }

        @Test
        public void validatePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
            Phone expectedPhone = new Phone(VALID_PHONE);
            assertEquals(expectedPhone, Validation.validatePhone(VALID_PHONE));
        }

        @Test
        public void validatePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
            String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
            Phone expectedPhone = new Phone(VALID_PHONE);
            assertEquals(expectedPhone, Validation.validatePhone(phoneWithWhitespace));
        }
    }

    @Nested
    class ValidateAddressTests {
        private static final String INVALID_ADDRESS = " ";
        private static final String VALID_ADDRESS = "123 Main Street #0505";

        @Test
        public void validateAddress_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validateAddress((String) null));
        }

        @Test
        public void validateAddress_invalidValue_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validateAddress(INVALID_ADDRESS));
        }

        @Test
        public void validateAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
            Address expectedAddress = new Address(VALID_ADDRESS);
            assertEquals(expectedAddress, Validation.validateAddress(VALID_ADDRESS));
        }

        @Test
        public void validateAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
            String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
            Address expectedAddress = new Address(VALID_ADDRESS);
            assertEquals(expectedAddress, Validation.validateAddress(addressWithWhitespace));
        }
    }

    @Nested
    class ValidateEmailTests {
        private static final String INVALID_EMAIL = "example.com";
        private static final String VALID_EMAIL = "rachel@example.com";

        @Test
        public void validateEmail_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validateEmail((String) null));
        }

        @Test
        public void validateEmail_invalidValue_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validateEmail(INVALID_EMAIL));
        }

        @Test
        public void validateEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
            Email expectedEmail = new Email(VALID_EMAIL);
            assertEquals(expectedEmail, Validation.validateEmail(VALID_EMAIL));
        }

        @Test
        public void validateEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
            String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
            Email expectedEmail = new Email(VALID_EMAIL);
            assertEquals(expectedEmail, Validation.validateEmail(emailWithWhitespace));
        }
    }

    @Nested
    class ValidateTagTests {
        private static final String INVALID_TAG = "#friend";
        private static final String VALID_TAG_1 = "friend";
        private static final String VALID_TAG_2 = "neighbour";

        @Test
        public void validateTag_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validateTag(null));
        }

        @Test
        public void parseTag_invalidValue_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validateTag(INVALID_TAG));
        }

        @Test
        public void validateTag_validValueWithoutWhitespace_returnsTag() throws Exception {
            Tag expectedTag = new Tag(VALID_TAG_1);
            assertEquals(expectedTag, Validation.validateTag(VALID_TAG_1));
        }

        @Test
        public void validateTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
            String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
            Tag expectedTag = new Tag(VALID_TAG_1);
            assertEquals(expectedTag, Validation.validateTag(tagWithWhitespace));
        }

        @Test
        public void validateTags_null_throwsNullPointerException() {
            Assert.assertThrows(NullPointerException.class, () -> Validation.validateTags(null));
        }

        @Test
        public void validateTags_collectionWithInvalidTags_throwsValidationException() {
            Assert.assertThrows(ValidationException.class, () -> Validation.validateTags(
                    Arrays.asList(VALID_TAG_1, INVALID_TAG)));
        }

        @Test
        public void validateTags_emptyCollection_returnsEmptySet() throws Exception {
            assertTrue(Validation.validateTags(Collections.emptyList()).isEmpty());
        }

        @Test
        public void validateTags_collectionWithValidTags_returnsTagSet() throws Exception {
            Set<Tag> actualTagSet = Validation.validateTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
            Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

            assertEquals(expectedTagSet, actualTagSet);
        }
    }
}
