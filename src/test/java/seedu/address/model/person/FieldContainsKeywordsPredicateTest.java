package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class FieldContainsKeywordsPredicateTest {

    @Test
    public void equalsMethod() {
        List<String> firstList = Collections.singletonList("first");
        List<String> secondList = Arrays.asList("first", "second");

        FieldContainsKeywordsPredicate firstPred = new FieldContainsKeywordsPredicate(firstList);
        FieldContainsKeywordsPredicate secondPred = new FieldContainsKeywordsPredicate(secondList);

        assertTrue(firstPred.equals(firstPred)); // same object
        assertTrue(firstPred.equals(new FieldContainsKeywordsPredicate(firstList))); // same values
        assertFalse(firstPred.equals(1)); // different type
        assertFalse(firstPred.equals(null)); // null
        assertFalse(firstPred.equals(secondPred)); // different keywords
    }

    @Test
    public void nameFlag_matchesName() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("Alice"),
                        true, false, false, false, false, Set.of());

        // matches name
        assertTrue(pred.test(new PersonBuilder().withName("Alice Bob")
                .withPhone("99999999")
                .withEmail("alice@gmail.com")
                .withAddress("123 Main Street")
                .withTags("friends").build()));

        // keyword in other fields ignored
        assertFalse(pred.test(new PersonBuilder().withName("Bob")
                .withPhone("99999999")
                .withEmail("user@domain.com")
                .withAddress("Alice Avenue")
                .withTags("Alice").build()));
    }

    @Test
    public void phoneFlag_matchesPhone() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("99999999"),
                        false, true, false, false, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withName("Dummy")
                .withPhone("99999999").build()));

        assertFalse(pred.test(new PersonBuilder().withName("99999999")
                .withPhone("1234")
                .withEmail("99999999@mail.com")
                .withAddress("Blk 99999999 Road")
                .withTags("nine").build()));
    }

    @Test
    public void emailFlag_matchesEmail() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("alice@gmail.com"),
                        false, false, true, false, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withEmail("alice@gmail.com")
                .withName("Alice").build()));

        assertFalse(pred.test(new PersonBuilder().withEmail("notalice@gmail.com")
                .withName("Alice").build()));
    }

    @Test
    public void addressFlag_matchesAddress() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("Street"),
                        false, false, false, true, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withAddress("123 Main Street").build()));
        assertFalse(pred.test(new PersonBuilder().withAddress("123 Mainland")
                .withName("Street").build()));
    }

    @Test
    public void tagFlag_matchesTags() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("friends"),
                        false, false, false, false, true, Set.of());

        assertTrue(pred.test(new PersonBuilder().withTags("friends", "colleagues").build()));
        assertFalse(pred.test(new PersonBuilder().withName("friends")
                .withTags("buddies").build()));
    }

    @Test
    public void multiFlags_nameAndEmail() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("Alice", "Alice@gmail.com"),
                        true, false, true, false, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withName("Alice").withEmail("user@x.com").build()));
        assertTrue(pred.test(new PersonBuilder().withName("Bob").withEmail("Alice@gmail.com").build()));
        assertFalse(pred.test(new PersonBuilder().withName("Bob")
                .withEmail("user@x.com")
                .withAddress("Alice Road")
                .withTags("Alice").build()));
    }

    @Test
    public void mixedCaseKeyword_caseInsensitive() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("aLIce"),
                        true, false, false, false, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withName("ALICE Yeoh").build()));
        assertTrue(pred.test(new PersonBuilder().withName("alice").build()));
    }

    @Test
    public void wordBoundary_fullWordMatch() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("alex"),
                        true, false, false, false, false, Set.of());

        assertTrue(pred.test(new PersonBuilder().withName("alex yeoh").build()));
        assertFalse(pred.test(new PersonBuilder().withName("alexander yeoh").build()));
    }

    @Test
    public void customFieldsOnly_match() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("gold"),
                        false, false, false, false, false, Set.of("assetclass"));
        Map<String, String> customs = new LinkedHashMap<>();
        customs.put("assetclass", "gold");
        Person tester = new PersonBuilder().build().withCustomFields(customs);
        assertTrue(pred.test(tester));
    }

    @Test
    public void customFieldValue_doesNotMatch() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("silver"),
                        false, false, false, false, false, Set.of("assetclass"));

        Map<String, String> customs = new LinkedHashMap<>();
        customs.put("assetclass", "gold"); // keyword "silver" not found
        Person tester = new PersonBuilder().build().withCustomFields(customs);

        assertFalse(pred.test(tester));
    }

    @Test
    public void customKeyNotSelected_returnsFalse() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("gold"),
                        false, false, false, false, false, Set.of("region"));

        Map<String, String> customs = new LinkedHashMap<>();
        customs.put("assetclass", "gold");
        Person tester = new PersonBuilder().build().withCustomFields(customs);

        assertFalse(pred.test(tester));
    }

    @Test
    public void multipleCustomKeys_anyOneMatches_returnsTrue() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("asia"),
                        false, false, false, false, false, Set.of("region", "assetclass"));

        Map<String, String> customs = new LinkedHashMap<>();
        customs.put("assetclass", "gold");
        customs.put("region", "Asia");
        Person tester = new PersonBuilder().build().withCustomFields(customs);

        assertTrue(pred.test(tester));
    }

    @Test
    public void noCustomFields_returnsFalse() {
        FieldContainsKeywordsPredicate pred =
                new FieldContainsKeywordsPredicate(List.of("gold"),
                        false, false, false, false, false, Set.of("assetclass"));

        Person tester = new PersonBuilder().build(); // no custom fields
        assertFalse(pred.test(tester));
    }

    @Test
    public void equals_diffFlags_notEqual() {
        List<String> kws = List.of("Alice");

        FieldContainsKeywordsPredicate allFields =
                new FieldContainsKeywordsPredicate(kws);

        FieldContainsKeywordsPredicate nameOnly =
                new FieldContainsKeywordsPredicate(kws,
                        true, false, false, false, false, Set.of());

        FieldContainsKeywordsPredicate emailOnly =
                new FieldContainsKeywordsPredicate(kws,
                        false, false, true, false, false, Set.of());

        assertFalse(allFields.equals(nameOnly));
        assertFalse(nameOnly.equals(emailOnly));

        FieldContainsKeywordsPredicate nameOnlyCopy =
                new FieldContainsKeywordsPredicate(kws,
                        true, false, false, false, false, Set.of());
        assertTrue(nameOnly.equals(nameOnlyCopy));
    }


    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(keywords);

        String expected = FieldContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

}

