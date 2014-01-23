import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
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
public class MapRenderer extends JPanel implements KeyListener {
    private DebugPanel debugPanel;
    private final String[] filenames = new String[] {"floor1", "floor2", "floor3"};
    protected GameEngine gameEngine;
    protected TileSet tileSet;
    protected Map[] tiledmap = new Map[filenames.length];
    protected Color bgcolour;
    protected tiled.view.MapRenderer tiledMapRenderer; //really unfortunate name collision
    final private int tileHeight = 32; //there's a much better way to do this
    final private int tileWidth = 32; //yeah
    private static Logger logger = Logger.getLogger("MapRenderer");
    private int currentFloor = 0;

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
        if (debugPanel != null) {
            g.setColor(Color.RED);
            for (MapObject object : ((ObjectGroup) tiledmap[currentFloor].getLayer(2))) {
                g.drawRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
            }
            g.setColor(Color.YELLOW);
            for (MapObject object: ((ObjectGroup) tiledmap[currentFloor].getLayer(3))) {
                g.drawRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
            }
            g.setColor(Color.BLUE);
            for (Body collisionBody: gameEngine.getCollisionBodies()) {
                Vec2 position = collisionBody.getPosition();
                g.drawRect((int) tiles2pixels(position.x),(int) tiles2pixels(position.y), 4, 4);
            }
        }
        g.setColor(Color.BLACK);
        float[] location = gameEngine.getPlayerLocation();
        g.fillRect((int) tiles2pixels(location[0]) - 8,(int)  tiles2pixels(location[1]) - 8, 16, 16);
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
            if (obj != null) {
                gameEngine.addCollisionArea(pixel2tiles(obj.getX()), pixel2tiles(obj.getY()), pixel2tiles(obj.getWidth()),
                    pixel2tiles(obj.getHeight()), null);
            }
        }

        ObjectGroup doorGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(2);
        if (doorGroup != null) {
            Iterator<MapObject> doors = doorGroup.getObjects();
            while (doors.hasNext()) {
                MapObject door = doors.next();
                if (door != null) {
                    gameEngine.addCollisionArea(pixel2tiles(door.getX()), pixel2tiles(door.getY()), pixel2tiles(door.getWidth()),
                            pixel2tiles(door.getHeight()), Entities.DOOR);
                }
                if (door == null || !(door.getShape() instanceof Rectangle)) {
                    logger.warning("Door problem! " + door);
                }
            }
        } else {
            logger.severe("null second (door) layer on floor: " + currentFloor);
        }

        ObjectGroup specialsGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(3);
        if (specialsGroup != null) {
            Iterator<MapObject> specials = specialsGroup.getObjects();
            while (specials.hasNext()) {
                MapObject special = specials.next();
                if (special != null) {
                    Entities specialEntity = null;
                    String specialProperty = special.getProperties().getProperty("special");
                    if (specialProperty.equals("upstaircase")) specialEntity = Entities.STAIRS_UP;
                    else if (specialProperty.equals("downstaircase")) specialEntity = Entities.STAIRS_DOWN;
                    else {
                        logger.warning("special property did not match anything known: " + specialProperty);
                    }
                    gameEngine.addCollisionArea(pixel2tiles(special.getX()), pixel2tiles(special.getY()), pixel2tiles(special.getWidth()),
                            pixel2tiles(special.getHeight()), specialEntity);
                }
                if (special == null || !(special.getShape() instanceof Rectangle)) {
                    logger.warning("Special problem! " + special);
                }
            }
        } else {
            logger.severe("null third (special) layer on floor: " + currentFloor);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

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
