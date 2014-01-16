import javax.swing.*;

public class GameFrame extends JFrame {

    private final MapRenderer mapRenderer = new MapRenderer("res/map.tmx");
    private final GameEngine gameEngine = new GameEngine(mapRenderer);

    public GameFrame() {
        setTitle("Game of Garneau, v." + Version.version);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(mapRenderer);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startGUI();
            }
        });
    }

    private static void startGUI() {
        GameFrame gameFrame = new GameFrame();
    }
}
