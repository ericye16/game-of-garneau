import javax.swing.*;
import java.util.HashSet;

public class DebugPanel extends JPanel {
    private final HashSet<Integer> integers = new HashSet<Integer>();
    private JLabel mouseLocation = new JLabel();
    private JLabel tileLocation = new JLabel();
    private JLabel keysPressed = new JLabel();

    public void updateMouseLocation(int x, int y) {
        mouseLocation.setText("Mouse (" + x + ", " + y + ")");
    }

    public void updateKeysPressed(keyLabelAction action, int keyCode) {
        if (action == keyLabelAction.ADD) {
            integers.add(keyCode);
        } else if (action == keyLabelAction.REMOVE) {
            integers.remove(keyCode);
        } else {
            throw new InternalError(); //should never happen
        }

        //render
        String toDisplay = "Keys (";
        for (Integer keyCode_s: integers) {
            toDisplay += keyCode_s + ", ";
        }
        toDisplay += ")";
        keysPressed.setText(toDisplay);
    }

    public void updateTileLocation(int x, int y) {
        tileLocation.setText("Tile (" + x + ", " + y + ")");
    }

    public enum keyLabelAction {
        ADD, REMOVE
    }
}
