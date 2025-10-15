package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Fields} matches any of the keywords given.
 */
public class FieldContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public FieldContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /** Returns the keywords used by this predicate. */
    public List<String> getKeywords() {
        return List.copyOf(keywords);
    }

    /**
     * Returns {@code true} if any of the given {@code keywords} matches at least one of the
     * {@link Person}'s fields exactly. A match is case-insensitive and checks across multiple fields:
     * <ul>
     *     <li>Name</li>
     *     <li>Phone number</li>
     *     <li>Email address</li>
     *     <li>Physical address</li>
     *     <li>Tags</li>
     * </ul>
     * The method iterates through all keywords and returns {@code true} as soon as a match is found.
     *
     * @param person The person whose details are being compared against the keywords.
     * @return {@code true} if any keyword matches any of the person's fields (case-insensitive),
     *         {@code false} otherwise.
     */
    @Override
    public boolean test(Person person) {
        return keywords.stream().anyMatch(keyword ->
                        StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword)
                        || StringUtil.containsWordIgnoreCase(person.getPhone().value, keyword)
                        || StringUtil.containsWordIgnoreCase(person.getEmail().value, keyword)
                        || StringUtil.containsWordIgnoreCase(person.getAddress().value, keyword)
                        || person.getTags().stream().anyMatch(tag ->
                        StringUtil.containsWordIgnoreCase(tag.tagName, keyword))
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FieldContainsKeywordsPredicate otherFieldsContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherFieldsContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
