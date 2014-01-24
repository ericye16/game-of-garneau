import javax.swing.*;
import java.util.HashSet;
import java.util.logging.Logger;

public class DebugPanel extends JPanel {
    private final HashSet<Integer> integers = new HashSet<Integer>();
    private JLabel tileLocation = new JLabel();
    private JLabel keysPressed = new JLabel();
    private JLabel playerLocation = new JLabel();
    private static Logger logger = Logger.getLogger("DebugPanel");


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

    public void updateTileLocation(int x, int y) {
        String text = "Tile (" + x + ", " + y + ")";
        tileLocation.setText(text);
        //logger.info(text);
    }

    public void updatePlayerPosition(float x, float y) {
        String text = "Pos: (" + x + ", " + y + ")";
        playerLocation.setText(text);
    }

    public DebugPanel() {
        add(tileLocation);
        add(keysPressed);
        add(playerLocation);
        updateTileLocation(0, 0);
        updatePlayerPosition(0, 0);
        drawKeysPressed();
    }

    public enum keyLabelAction {
        ADD, REMOVE
    }
}
