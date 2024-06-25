package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

import java.util.Collection;

public class AutocompleteTextArea extends TextArea {
    private static final int WORLD_MAX_LOOKUP_CHARS = 64;

    private final ListView<String> autocompleteListView = new ListView<>();
    private final Popup autocompletePopup = new Popup();

    private final FilteredList<String> suggestions = new FilteredList<>(FXCollections.observableArrayList());

    public AutocompleteTextArea() {
        // Prevent listView from capturing key events like Esc, Array Up & Down
        autocompleteListView.setFocusTraversable(false);
        autocompleteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        autocompleteListView.setItems(suggestions);

        autocompletePopup.getContent().add(autocompleteListView);
        autocompletePopup.setHideOnEscape(true);

        // Show popup when a Control+Space is pressed
        addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
                event.consume();

                String world = getWordBeforeCaret();
                filterSuggestions(world);

                showAutocompletePopupAtCaret();
                // requestFocus();
            }
        });

        // Hide popup when focus is lost.
        focusedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue) {
                autocompletePopup.hide();
            }
        });

        // Relocate popup during writing; update applicable suggestions.
        caretPositionProperty().addListener((o, oldValue, newValue) -> {
            if (autocompletePopup.isShowing()) {
                String world = getWordBeforeCaret();

                if (world.isEmpty()) {
                    autocompletePopup.hide();
                    return;
                }

                filterSuggestions(world);
                showAutocompletePopupAtCaret();

                // requestFocus ?
            }
        });

        // Handle Arrow Up, Down and Tab when popup is open.
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!autocompletePopup.isShowing()) return;
            /*
            if (event.getCode() == KeyCode.ESCAPE) {
                autocompletePopup.hide();
                event.consume();
            }
            */
            var selectionModel = autocompleteListView.getSelectionModel();
            int selectedIndex = selectionModel.getSelectedIndex();

            if (event.getCode() == KeyCode.UP) {
                event.consume();
                selectionModel.select(Math.max(0, selectedIndex - 1));
            } else if (event.getCode() == KeyCode.DOWN) {
                event.consume();
                selectionModel.select(Math.min(suggestions.size() - 1, selectedIndex + 1));
            } else if (event.getCode() == KeyCode.TAB) {
                event.consume();

                autocompletePopup.hide();
                if (selectedIndex != -1) {
                    String suggestion = selectionModel.getSelectedItem();
                    // TODO: Handle world before suggestion
                    insertText(getCaretPosition(), suggestion);
                }
            }
        });
    }

    private void showAutocompletePopupAtCaret() {
        if (getSkin() instanceof TextAreaSkin skin) {
            var screenCaretPosition = localToScreen(skin.getCaretBounds());
            autocompletePopup.show(this, screenCaretPosition.getCenterX(), screenCaretPosition.getCenterY() + screenCaretPosition.getHeight());
        }
    }

    private void filterSuggestions(String byWorld) {
        suggestions.setPredicate(s -> (s != null) && s.contains(byWorld));
    }

    public void setSuggestions(Collection<String> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
    }

    private String getWordBeforeCaret() {
        int caretPos = getCaretPosition();

        // For efficiency reasons we only look up to WORLD_MAX_LOOKUP_CHARS characters
        String textBeforeCaret = getText(Math.max(0, caretPos - WORLD_MAX_LOOKUP_CHARS), caretPos);

        int length = 0;
        for (int i = textBeforeCaret.length() - 1; i >= 0; i--) {
            if (!isWordCharacter(textBeforeCaret.charAt(i))) break;
            length++;
        }

        return textBeforeCaret.substring(textBeforeCaret.length() - length);
    }

    private String getWordAfterCaret() {
        int caretPos = getCaretPosition();

        // For efficiency reasons we only look up to WORLD_MAX_LOOKUP_CHARS characters
        String textBeforeCaret = getText(caretPos, Math.min(caretPos + WORLD_MAX_LOOKUP_CHARS, getLength()));

        int length = 0;
        for (int i = 0; i < textBeforeCaret.length(); i++) {
            if (!isWordCharacter(textBeforeCaret.charAt(i))) break;
            length++;
        }

        return textBeforeCaret.substring(0, length);
    }

    public String getEditedWord() {
        return getWordBeforeCaret().concat(getWordAfterCaret());
    }

    private static boolean isWordCharacter(char c) {
        return (c == '-') || Character.isLetterOrDigit(c);
    }
}
