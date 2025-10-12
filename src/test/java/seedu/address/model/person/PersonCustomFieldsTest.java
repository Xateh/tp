package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

class PersonCustomFieldsTest {

    @Test
    void getCustomFieldsUnmodifiable() {
        Person p = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () ->
                p.getCustomFields().put("k", "v"));
    }

    @Test
    void withCustomFieldsCopiesAndPreservesOrder() {
        Person p = new PersonBuilder().build();
        Map<String, String> in = new LinkedHashMap<>();
        in.put("asset-class", "gold");
        in.put("company", "GS");

        Person q = p.withCustomFields(in);
        // different instance
        assertNotSame(p, q);
        // order preserved
        assertArrayEquals(new String[]{"asset-class", "company"},
                q.getCustomFields().keySet().toArray(new String[0]));
        // defensive copy
        in.put("x", "y");
        assertFalse(q.getCustomFields().containsKey("x"));
    }

    @Test
    void equalsAndHashCodeIncludeCustomFields() {
        Person a = new PersonBuilder().withName("Alex").build();
        Person b = new PersonBuilder().withName("Alex").build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        Person c = a.withCustomFields(Map.of("company", "GS"));
        assertNotEquals(a, c);
    }
}

