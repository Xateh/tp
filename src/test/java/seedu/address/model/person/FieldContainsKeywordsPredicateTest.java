package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class FieldContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        FieldContainsKeywordsPredicate firstPredicate = new FieldContainsKeywordsPredicate(firstPredicateKeywordList);
        FieldContainsKeywordsPredicate secondPredicate = new FieldContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        FieldContainsKeywordsPredicate firstPredCopy = new FieldContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        FieldContainsKeywordsPredicate pred = new FieldContainsKeywordsPredicate(Collections.singletonList("Alice"));
        assertTrue(pred.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        pred = new FieldContainsKeywordsPredicate(Arrays.asList("Alice", "Bob"));
        assertTrue(pred.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        pred = new FieldContainsKeywordsPredicate(Arrays.asList("Bob", "Carol"));
        assertTrue(pred.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        pred = new FieldContainsKeywordsPredicate(Arrays.asList("aLIce", "bOB"));
        assertTrue(pred.test(new PersonBuilder().withName("Alice Bob").build()));

    }

    @Test
    public void test_phoneContainsKeyword_returnsTrue() {
        // Match phone number
        FieldContainsKeywordsPredicate pred = new FieldContainsKeywordsPredicate(Arrays.asList("99999999"));
        assertTrue(pred.test(new PersonBuilder().withName("dummy").withPhone("99999999").build()));
    }


    @Test
    public void test_emailContainsKeyword_returnsTrue() {
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("alice@gmail.com"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("alice@gmail.com").build()));
    }

    @Test
    public void test_addressContainsKeyword_returnsTrue() {
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("Street"));
        assertTrue(predicate.test(new PersonBuilder().withAddress("123 Main Street").build()));
    }

    @Test
    public void test_tagContainsKeyword_returnsTrue() {
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("friends"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends", "colleagues").build()));
    }

    @Test
    public void test_keywordMatchesMultipleFields_returnsTrue() {
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("Alice"));
        Person person = new PersonBuilder()
                .withName("Alice")
                .withEmail("alice@example.com")
                .withAddress("123 Alice Street")
                .build();
        assertTrue(predicate.test(person));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new FieldContainsKeywordsPredicate(Arrays.asList("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        //Failure for sub words
        predicate = new FieldContainsKeywordsPredicate(Arrays.asList("al"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

    }

    @Test
    public void test_keywordNotInOtherFields_returnsFalse() {
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("Nonexistent"));

        Person person = new PersonBuilder()
                .withName("Alice")
                .withPhone("99999999")
                .withEmail("alice@gmail.com")
                .withAddress("123 Main Street")
                .withTags("friend")
                .build();

        assertFalse(predicate.test(person));
    }

    @Test
    public void test_partialMatchInOtherFields_returnsFalse() {
        // phone: keyword is only part of the number
        FieldContainsKeywordsPredicate predicate =
                new FieldContainsKeywordsPredicate(Collections.singletonList("9999"));
        assertFalse(predicate.test(new PersonBuilder().withPhone("99998888").build()));

        // email: keyword is substring
        predicate = new FieldContainsKeywordsPredicate(Collections.singletonList("gmail"));
        assertFalse(predicate.test(new PersonBuilder().withEmail("gmailing@site.com").build()));

        // address: keyword is substring of a longer word
        predicate = new FieldContainsKeywordsPredicate(Collections.singletonList("Main"));
        assertFalse(predicate.test(new PersonBuilder().withAddress("Mainland Avenue").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(keywords);

        String expected = FieldContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void getKeywords_returnsDefensiveCopy() {
        List<String> original = Arrays.asList("Alice", "Bob");
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(original);

        List<String> extracted = predicate.getKeywords();
        assertEquals(original, extracted);
        assertFalse(extracted == original);
    }

    @Test
    public void getKeywords_returnsUnmodifiableList() {
        FieldContainsKeywordsPredicate predicate = new FieldContainsKeywordsPredicate(Arrays.asList("Alice"));

        List<String> extracted = predicate.getKeywords();
        assertThrows(UnsupportedOperationException.class, () -> extracted.add("Bob"));
    }
}

