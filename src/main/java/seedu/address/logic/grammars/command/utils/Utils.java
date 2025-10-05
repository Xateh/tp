package seedu.address.logic.grammars.command.utils;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class holding utility methods.
 */
public class Utils {
    private Utils() {
    }

    /**
     * Returns a String that acts as a visual range delimiter.
     *
     * @param start Start index of range (inclusive).
     * @param end   End index of range (exclusive).
     * @return String visually delimiting the range described.
     */
    public static String makeVisualDelimiter(int start, int end) {
        StringBuilder sb = new StringBuilder();
        int current = 0;

        while (current < start) {
            sb.append(" ");
            current++;
        }

        sb.append("^");
        current++;

        while (current < end - 1) {
            sb.append("-");
            current++;
        }

        if (current == end - 1) {
            sb.append("^");
        }

        return sb.toString();
    }

    /**
     * Returns a reversed ArrayList.
     *
     * @param arrayList Input ArrayList to be reversed.
     * @param <T>       Type of elements in the given ArrayList.
     * @return Reversed ArrayList, shallow copy of all items.
     */
    public static <T> ArrayList<T> reverseArrayList(ArrayList<T> arrayList) {
        int count = arrayList.size();
        return IntStream.range(0, count).map(i -> (count - 1) - i).boxed()
                .map(arrayList::get).collect(Collectors.toCollection(ArrayList::new));
    }
}
