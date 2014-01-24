import javax.swing.*;
import java.util.HashSet;
import java.util.logging.Logger;

public class DebugPanel extends JPanel {
    private final HashSet<Integer> integers = new HashSet<Integer>();
    private JLabel keysPressed = new JLabel();
    private JLabel playerLocation = new JLabel();
    private static Logger logger = Logger.getLogger("DebugPanel");

    /**
     * Update the keys pressed label.
     * @param action Whether to add or remove the key.
     * @param keyCode Which key to add.
     */
    public void updateKeysPressed(keyLabelAction action, int keyCode) {
        if (action == keyLabelAction.ADD) {
            integers.add(keyCode);
        } else if (action == keyLabelAction.REMOVE) {
            integers.remove(keyCode);
        } else {
            throw new InternalError(); //should never happen
        }
        drawKeysPressed();
    }

    /**
     * Draw the keys pressed in the panel.
     */
    private void drawKeysPressed() {
        //render
        String toDisplay = "Keys (";
        for (Integer keyCode_s: integers) {
            toDisplay += keyCode_s + ", ";
        }
        toDisplay += ")";
        keysPressed.setText(toDisplay);
        //logger.info(toDisplay);
    }

    /**
     * Show where the player is.
     * @param x The x-location.
     * @param y The y-location.
     */
    public void updatePlayerPosition(float x, float y) {
        String text = "Pos: (" + x + ", " + y + ")";
        playerLocation.setText(text);
    }

    /**
     * Construct the DebugPanel with default values.
     */
    public DebugPanel() {
        add(keysPressed);
        add(playerLocation);
        updatePlayerPosition(0, 0);
        drawKeysPressed();
    }

    /**
     * The actions to perform with keycodes. Either add or remove.
     */
    public enum keyLabelAction {
        ADD, REMOVE
    }
}
