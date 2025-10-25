package seedu.address.ui;

/**
 * Adapter abstraction over a text field used by {@link CommandBox} to allow
 * testing without requiring the JavaFX toolkit.
 *
 * <p>Note: {@code TextFieldAdapter} defines a small, test-friendly contract for
 * interacting with a text field. In production the adapter is implemented by a
 * thin wrapper around the JavaFX {@code TextField} (created inside
 * {@link CommandBox}). Tests can provide a lightweight, pure-Java
 * implementation of this interface to exercise {@code CommandBox} behaviour
 * without initializing the JavaFX runtime.</p>
 *
 * @see CommandBox
 */
public interface TextFieldAdapter {
    /** Register a listener to be invoked when the text changes. */
    void addTextChangeListener(Runnable listener);

    /** Returns a mutable list representing the style class list. */
    java.util.List<String> getStyleClass();

    /** Sets the text displayed in the field. */
    void setText(String text);

    /** Positions the caret at the given index. */
    void positionCaret(int pos);

    /** Returns the current text value. */
    String getText();
}
