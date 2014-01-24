import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private final MapRenderer mapRenderer = new MapRenderer();
    private final GameEngine gameEngine = new GameEngine(mapRenderer);

    public GameFrame() {
        setTitle("Game of Garneau, v." + Version.version);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(mapRenderer, BorderLayout.LINE_START);
        DebugPanel debugPanel = new DebugPanel();
        mapRenderer.setGameEngine(gameEngine);
        mapRenderer.setDebugPanel(debugPanel);
        gameEngine.setDebugPanel(debugPanel);
        add(debugPanel, BorderLayout.PAGE_END);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        Version.printAbout();
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
