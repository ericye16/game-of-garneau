import tiled.core.*;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Class to render maps based on Tiled map file (TMX) files.
 * More information on the Tiled Map Editor can be found here: http://www.mapeditor.org/
 */
public class MapRenderer extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
    private DebugPanel debugPanel;
    private final String[] filenames = new String[] {"map", "floor1", "floor2", "floor3"};
    protected GameEngine gameEngine;
    protected TileSet tileSet;
    protected Map[] tiledmap = new Map[filenames.length];
    protected Color bgcolour;
    protected tiled.view.MapRenderer tiledMapRenderer; //really unfortunate name collision
    final private int tileHeight = 32; //there's a much better way to do this
    final private int tileWidth = 32; //yeah
    private static Logger logger = Logger.getLogger("MapRenderer");
    private int currentFloor = 0;

    public void renderEntityAt(Entity entity, double[] location) {
        assert(location.length == 3);
    }

    @Override
    public void paintComponent(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g.create();
        final Rectangle clip = g2d.getClipBounds();

        //background with background colour
        g2d.setPaint(bgcolour);
        g2d.fill(clip);

        //draw each tile map layer
        //unfortunate name collision
        for (MapLayer layer: tiledmap[currentFloor]) {
            if (layer instanceof TileLayer) {
                tiledMapRenderer.paintTileLayer(g2d, (TileLayer) layer);
            }
        }
        g.setColor(Color.RED);
        Iterator<MapObject> objects = ((ObjectGroup) tiledmap[currentFloor].getLayer(1)).iterator();
        while (objects.hasNext()) {
            MapObject object = objects.next();
            g.drawRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
        }
        g.setColor(Color.BLACK);
        float[] location = gameEngine.getPlayerLocation();
        g.fillRect((int) tiles2pixels(location[0]),(int)  tiles2pixels(location[1]), 16, 16);
    }

    public MapRenderer() {
        try {
            for (int i = 0; i < filenames.length; i++) {
                tiledmap[i] = new TMXMapReader().readMap("res/" + filenames[i] + ".tmx");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setOpaque(true);

        //what's our background colour?
        bgcolour = new Color(100, 100, 100);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    private static float pixel2tiles(float px) {
        return px / 32;
    }

    private static float tiles2pixels(float tiles) {
        return tiles * 32;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
        initializeWithGameEngine();
    }

    private void initializeWithGameEngine() {
        //we draw the entire map
        int width = tiledmap[currentFloor].getWidth() * tiledmap[currentFloor].getTileWidth();
        int height = tiledmap[currentFloor].getHeight() * tiledmap[currentFloor].getTileHeight();
        setPreferredSize(new Dimension(width, height));

        //we only support orthogonal maps
        if (tiledmap[currentFloor].getOrientation() != Map.ORIENTATION_ORTHOGONAL) {
            throw new IllegalArgumentException("Can only use orthogonal tiled maps.");
        }
        tiledMapRenderer = new OrthogonalRenderer(tiledmap[currentFloor]);
        ObjectGroup collisionGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(1);
        Iterator<MapObject> objects= collisionGroup.getObjects();
        while(objects.hasNext()) {
            MapObject obj = objects.next();
            gameEngine.addCollisionArea(pixel2tiles(obj.getX()), pixel2tiles(obj.getY()), pixel2tiles(obj.getWidth()),
                    pixel2tiles(obj.getHeight()), null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (debugPanel != null) {
            debugPanel.updateMouseLocation(e.getX(), e.getY());
            debugPanel.updateTileLocation(e.getX() / tileWidth, e.getY() / tileHeight);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (debugPanel != null) {
            debugPanel.updateKeysPressed(DebugPanel.keyLabelAction.ADD, e.getKeyCode());
            gameEngine.keyPressed(e);
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (debugPanel != null) {
            debugPanel.updateKeysPressed(DebugPanel.keyLabelAction.REMOVE, e.getKeyCode());
        }
        gameEngine.keyReleased(e);
    }
}
