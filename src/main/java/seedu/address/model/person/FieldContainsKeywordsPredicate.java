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

    //new flags to indicate whether the field is specified to search on
    private final boolean searchName;
    private final boolean searchPhone;
    private final boolean searchEmail;
    private final boolean searchAddress;
    private final boolean searchTag;


    //if no option value provided, search all non-custom fields
    public FieldContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, true, true, true, true, true);
    }

    /**
     * Constructs a {@code FieldContainsKeywordsPredicate} with specified search field flags.
     * <p>
     * Each boolean flag determines whether the corresponding {@link Person} field
     * should be included in the search.
     * </p>
     *
     * @param keywords The list of keywords to match against the person's fields.
     * @param searchName {@code true} to include the person's name field in the search.
     * @param searchPhone {@code true} to include the person's phone number field in the search.
     * @param searchEmail {@code true} to include the person's email address field in the search.
     * @param searchAddress {@code true} to include the person's physical address field in the search.
     * @param searchTag {@code true} to include the person's tags in the search.
     */
    public FieldContainsKeywordsPredicate(List<String> keywords,
                                          boolean searchName,
                                          boolean searchPhone,
                                          boolean searchEmail,
                                          boolean searchAddress,
                                          boolean searchTag) {
        this.keywords = keywords;
        this.searchName = searchName;
        this.searchPhone = searchPhone;
        this.searchEmail = searchEmail;
        this.searchAddress = searchAddress;
        this.searchTag = searchTag;
    }

    /**
     * Tests whether any of the specified {@code keywords} matches at least one of the selected
     * {@link Person} fields.
     *
     * <p>A match is <b>case-insensitive</b> and requires an <b>exact word match</b>
     * (i.e. "alex" matches "Alex Yeoh" but not "alexander").</p>
     *
     * <p>The fields checked depend on which search flags were provided:
     * <ul>
     *     <li>{@code /name} — Matches against the person's full name.</li>
     *     <li>{@code /phone} — Matches against the person's phone number.</li>
     *     <li>{@code /email} — Matches against the person's email address.</li>
     *     <li>{@code /address} — Matches against the person's physical address.</li>
     *     <li>{@code /tag} — Matches against any of the person's tags.</li>
     * </ul>
     * If no specific field flags are supplied, all of the above fields are searched by default.</p>
     *
     * <p>The method iterates through all provided keywords and returns {@code true} as soon as
     * one keyword matches any of the selected fields.</p>
     *
     * @param person The {@link Person} whose details are being tested against the keywords.
     * @return {@code true} if at least one keyword matches any of the selected fields
     *      (case-insensitive, full-word match);
     *      {@code false} otherwise.
     */
    @Override
    public boolean test(Person person) {
        for (String keyword : keywords) {
            if (searchName
                    && StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword)) {
                return true;
            }
            if (searchPhone
                    && StringUtil.containsWordIgnoreCase(person.getPhone().value, keyword)) {
                return true;
            }
            if (searchEmail
                    && StringUtil.containsWordIgnoreCase(person.getEmail().value, keyword)) {
                return true;
            }
            if (searchAddress
                    && StringUtil.containsWordIgnoreCase(person.getAddress().value, keyword)) {
                return true;
            }
            if (searchTag
                    && person.getTags().stream()
                            .anyMatch(tag -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword))) {
                return true;
            }
        }
        return false;
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

        return keywords.equals(otherFieldsContainsKeywordsPredicate.keywords)
                && searchName == otherFieldsContainsKeywordsPredicate.searchName
                && searchPhone == otherFieldsContainsKeywordsPredicate.searchPhone
                && searchEmail == otherFieldsContainsKeywordsPredicate.searchEmail
                && searchAddress == otherFieldsContainsKeywordsPredicate.searchAddress
                && searchTag == otherFieldsContainsKeywordsPredicate.searchTag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
