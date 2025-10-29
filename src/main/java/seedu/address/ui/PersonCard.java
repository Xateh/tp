package seedu.address.ui;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * Feature flag for showing preview custom fields in the UI only (no logic/persistence).
     */
    private static final String FLAG_CUSTOM_FIELDS_UI = "feature.customFields.ui";
    private static final String ENV_CUSTOM_FIELDS_UI = "CF_UI";

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
    @FXML private FlowPane links;

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

        links.getChildren().clear();
        person.getLinks().stream()
                .sorted((a, b) -> {
                    int byName = a.getLinkName().compareToIgnoreCase(b.getLinkName());
                    if (byName != 0) {
                        return byName;
                    }
                    return a.getLinkee().getName().fullName
                            .compareToIgnoreCase(b.getLinkee().getName().fullName);
                })
                .forEach(link -> {
                    boolean isLinker = link.getLinker().isSamePerson(person);
                    String otherName = isLinker
                            ? link.getLinkee().getName().fullName
                            : link.getLinker().getName().fullName;
                    String arrow = isLinker ? "→" : "←";
                    Label pill = new Label(link.getLinkName() + " " + arrow + " " + otherName);
                    pill.getStyleClass().add("link-label");
                    links.getChildren().add(pill);
                });


        if (links.getChildren().isEmpty()) {
            hide(links);
        }

        // ----- Custom fields (schema-less key:value) -----
        // We render arbitrary user-defined attributes as "key : value" rows.
        // If there are no rows, the entire section is hidden to keep the card compact.
        // This PR is UI-only: preview data is supplied behind a feature flag so that master
        // is not coupled to logic/persistence that does not exist yet.

        customFieldsBox.getChildren().clear();

        // Real Data
        Map<String, String> fields = new java.util.LinkedHashMap<>(person.getCustomFields());

        // Stable alphabetical order (case-insensitive) sp cards are predictable to scan.
        var keys = new java.util.ArrayList<>(fields.keySet());
        java.util.Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (String key : keys) {
            String val = fields.get(key);
            if (!val.isBlank()) {
                customFieldsBox.getChildren().add(kvRow(key, val));
            }
        }

        // Back-compat: if no fields were provided by the flag-specific provider above, nothing is shown.

        if (customFieldsBox.getChildren().isEmpty()) {
            hide(customFieldsBox); // hide LAST
        }
    }

    /**
     * Returns preview custom fields only when the feature flag is enabled.
     * <p>This keeps the UI change non-invasive: by default the section remains hidden and
     * there is no dependency on model/persistence. When enabled, the returned map is used
     * to render the same UI that future real data will drive.</p>
     *
     * @return a deterministic, insertion-ordered map of key-value pairs when enabled; otherwise an empty map.
     */
    private Map<String, String> getPreviewCustomFieldsIfEnabled() {

        // Preferred: environment variable
        String env = System.getenv(ENV_CUSTOM_FIELDS_UI);
        boolean viaEnv = env != null && !env.isBlank();

        // Optional: JVM property (works if your run task forwards it; otherwise ignored)
        boolean viaProperty = Boolean.getBoolean("feature.customFields.ui");

        if (!viaProperty && !viaEnv) {
            return Map.of();
        }

        // Keep order stable for predictable UI and tests.
        Map<String, String> preview = new LinkedHashMap<>();
        preview.put("asset-class", "gold");
        preview.put("company", "Goldman Sachs");
        return preview;
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
        keyLabel.getStyleClass().add("Label"); // keep casing to preserve current visuals

        Label valueLabel = new Label(" " + value);
        valueLabel.getStyleClass().add("Label"); // keep casing to preserve current visuals

        HBox pill = new HBox(4, keyLabel, valueLabel);
        pill.getStyleClass().add("custom-field-pill");

        return new HBox(pill);
    }
}
