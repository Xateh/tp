package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;

    /**
    * Container that hosts zero or more {@code key : value} rows for user-defined (custom) fields.
    * <p>Hidden when empty so the card layout remains identical to vanilla AB3.</p>
    */
    @FXML
    private VBox customFieldsBox;

    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        // ----- Custom fields (schema-less key:value) -----
        // We render arbitrary user-defined attributes as "key : value" rows.
        // If there are no rows, the entire section is hidden to keep the card compact.
        // Once Person#getCustomFields() is added on the model, the reflection-based
        // lookup below will start returning data without additional UI changes.

        customFieldsBox.getChildren().clear();

        var fields = tryGetCustomFields(person); // empty for now; real values later

        // Stable alphabetical order (case-insensitive) so cards are predictable to scan.
        var keys = new java.util.ArrayList<>(fields.keySet());
        java.util.Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (String key : keys) {
            Object valObj = fields.get(key);
            String val = (valObj == null) ? "" : String.valueOf(valObj);
            if (!val.isBlank()) {
                customFieldsBox.getChildren().add(kvRow(key, val));
            }
        }

        // DEV PREVIEW:
        // Launch with: ./gradlew run -Ddev.customfields.preview=true
        // to visualize two sample rows before the model feature lands.
        if (customFieldsBox.getChildren().isEmpty()
               && (Boolean.getBoolean("dev.customfields.preview") || System.getenv("CF_PREVIEW") != null)) {
            customFieldsBox.getChildren().addAll(
                kvRow("asset-class", "gold"),
                kvRow("company", "Goldman Sachs")
            );
        }

        if (customFieldsBox.getChildren().isEmpty()) {
            hide(customFieldsBox); // hide LAST
        }
    }

    /**
    * Hides a node from both view and layout.
    * <p>Using {@code visible=false} stops rendering; {@code managed=false} removes it from
    * the parent's layout pass so no empty space is reserved.</p>
    * @param n node to hide
    */
    private void hide(Node n) {
        n.setManaged(false);
        n.setVisible(false);
    }

    /**
    * Builds a single {@code key : value} row for the custom fields section.
    * <p>Reuses the small-label style so the row visually matches the rest of the card.</p>
    * @param key   field name (displayed as {@code key: })
    * @param value field value text
    * @return a horizontal row containing the labels
    */
    private HBox kvRow(String key, String value) {
        Label keyLabel = new Label(key + ":");
        keyLabel.getStyleClass().add("Label");

        Label valueLabel = new Label(" " + value);
        valueLabel.getStyleClass().add("Label");

        HBox pill = new HBox(4, keyLabel, valueLabel);
        pill.getStyleClass().add("custom-field-pill");

        return new HBox(pill);
    }

    /**
    * Attempts to obtain a map of custom fields from {@code person} without creating a compile-time
    * dependency on model changes.
    * <p>This uses reflection to call {@code getCustomFields()} if/when it exists. If absent or
    * incompatible, returns an empty map. This lets the UI ship now and "light up" automatically
    * later when the model adds the method.</p>
    * @param person the model object for this card
    * @return a non-null map of {@code key -> value} pairs (empty if none/unsupported)
    */
    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> tryGetCustomFields(Object person) {
        try {
            var m = person.getClass().getMethod("getCustomFields");
            Object result = m.invoke(person);
            if (result instanceof java.util.Map<?, ?> map) {
                var out = new java.util.LinkedHashMap<String, Object>();
                map.forEach((k, v) -> {
                    if (k != null) {
                        out.put(String.valueOf(k), v);
                    }
                });
                return out;
            }
        } catch (Exception ignored) {
            // Method not present yet, or not accessible; fall through to empty map.
        }
        return java.util.Map.of();
    }
}
