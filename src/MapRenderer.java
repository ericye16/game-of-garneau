import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import tiled.core.*;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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
    private static Logger logger = Logger.getLogger("MapRenderer");
    private int currentFloor = 0;

    /**
     * Paint the panel according to the state of the game. Paints the background and required entities.
     * @param g
     */
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
        //debugging
        if (debugPanel != null) {
            //draw a blue square in the middle of these collision areas
            g.setColor(Color.BLUE);
            for (Body collisionBody: gameEngine.getCollisionBodies()) {
                Vec2 position = collisionBody.getPosition();
                g.drawRect((int) tiles2pixels(position.x),(int) tiles2pixels(position.y), 4, 4);
            }
        }
        //draw the doors
        g.setColor(Color.RED);
        for (MapObject object : ((ObjectGroup) tiledmap[currentFloor].getLayer(2))) {
            g.drawRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
        }
        //draw the special areas, i.e. staircases.
        g.setColor(Color.YELLOW);
        for (MapObject object: ((ObjectGroup) tiledmap[currentFloor].getLayer(3))) {
            g.drawRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
        }

        //draw the enemies
        BufferedImage enemySprite = Enemy.getSpriteStatic();
        for (Body enemyBody: gameEngine.getEnemyBodies()) {
            Enemy enemy = (Enemy) enemyBody.getUserData();
            double[] location = enemy.location;
            int x = (int) tiles2pixels((float) location[0]) - 8;
            int y = (int) tiles2pixels((float) location[1]) - 8;

            AffineTransform transform = AffineTransform.getRotateInstance(enemy.getAngle(), 8, 8);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            g.drawImage(op.filter(enemySprite, null), x, y, null);
        }

        //draw the player
        float[] location = gameEngine.getPlayerLocation();
        BufferedImage sprite = gameEngine.getPlayerStudent().getSprite();
        int x = (int) tiles2pixels(location[0]) - 8;
        int y = (int) tiles2pixels(location[1]) - 8;

        AffineTransform transform = AffineTransform.getRotateInstance(gameEngine.getPlayerStudent().getAngle(), 8, 8);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(sprite, null), x, y, null);
    }

    /**
     * Constructor for the MapRenderer class. Reads in all the tiled map editor files as input.
     */
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

    private static float pixels2tiles(float px) {
        return px / 32;
    }

    private static float tiles2pixels(float tiles) {
        return tiles * 32;
    }

    /**
     * Set the GameEngine associated with this object.
     * @param gameEngine The GameEngine associated.
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Set the DebugPanel associated with this object.
     * @param debugPanel The DebugPanel Associated.
     */
    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
        initializeWithGameEngine();
    }

    /**
     * Initialize the renderer and start the GameEngine
     */
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

        //add all of the classroom as collision objects
        ObjectGroup collisionGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(1);
        Iterator<MapObject> objects= collisionGroup.getObjects();
        while(objects.hasNext()) {
            MapObject obj = objects.next();
            if (obj != null) {
                gameEngine.addCollisionArea(pixels2tiles(obj.getX()), pixels2tiles(obj.getY()), pixels2tiles(obj.getWidth()),
                    pixels2tiles(obj.getHeight()), null);
            }
        }

        //add all of the doors as collision objects
        ObjectGroup doorGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(2);
        if (doorGroup != null) {
            Iterator<MapObject> doors = doorGroup.getObjects();
            while (doors.hasNext()) {
                MapObject door = doors.next();
                if (door == null || !(door.getShape() instanceof Rectangle)) {
                    logger.warning("Door problem! " + door);
                }
                if (door != null) {
                    gameEngine.addCollisionArea(pixels2tiles(door.getX()), pixels2tiles(door.getY()), pixels2tiles(door.getWidth()),
                            pixels2tiles(door.getHeight()), Entities.DOOR);
                }
            }
        } else {
            logger.severe("null second (door) layer on floor: " + currentFloor);
        }

        //add all of the specials (staircases) as collision objects
        ObjectGroup specialsGroup = (ObjectGroup) tiledmap[currentFloor].getLayer(3);
        if (specialsGroup != null) {
            Iterator<MapObject> specials = specialsGroup.getObjects();
            while (specials.hasNext()) {
                MapObject special = specials.next();
                if (special == null || !(special.getShape() instanceof Rectangle)) {
                    logger.warning("Special problem! " + special);
                }
                if (special != null) {
                    Entities specialEntity = null;
                    String specialProperty = special.getProperties().getProperty("special");
                    if (specialProperty.equals("upstaircase")) specialEntity = Entities.STAIRS_UP;
                    else if (specialProperty.equals("downstaircase")) specialEntity = Entities.STAIRS_DOWN;
                    else {
                        logger.warning("special property did not match anything known: " + specialProperty);
                    }
                    gameEngine.addCollisionArea(pixels2tiles(special.getX()), pixels2tiles(special.getY()), pixels2tiles(special.getWidth()),
                            pixels2tiles(special.getHeight()), specialEntity);
                }
            }
        } else {
            logger.severe("null third (special) layer on floor: " + currentFloor);
        }

        //start the GameEngine
        gameEngine.go();
    }

    /**
     * Climb up the stairs. Changes the level to the next one, if applicable.
     */
    public void goUpTheStairs() {
        if (currentFloor >= 2) {
            throw new IndexOutOfBoundsException();
        }
        currentFloor++;
        switchFloors(currentFloor, true);
    }

    /**
     * Go down the stairs. changes the level to the lower one, if applicable.
     */
    public void goDownTheStairs() {
        if (currentFloor <= 0) {
            throw new IndexOutOfBoundsException();
        }
        currentFloor--;
        switchFloors(currentFloor, false);
    }

    /**
     * Called every time floors are changed.
     * Initializes the player to the correct location on the floor.
     * @param floor The new floor (zero-indexed).
     * @param goingUp Are we going up? true if up, else false.
     */
    private void switchFloors(int floor, boolean goingUp) {
        gameEngine.stopAll();
        double[] newLocation;
        if (goingUp) {
            switch (floor) {
                case 1:
                    newLocation = new double[] {6, 19, 1};
                    break;
                case 2:
                    newLocation = new double[] {6, 19, 2};
                    break;
                default:
                    throw new IndexOutOfBoundsException();
            }
        } else {
            switch (floor) {
                case 0:
                    newLocation = new double[] {19, 9, 0};
                    break;
                case 1:
                    newLocation = new double[] {19, 9, 1};
                    break;
                default:
                    throw new IndexOutOfBoundsException();
            }
        }
        gameEngine.setPlayerLocation(newLocation);
        gameEngine.resetWorld();
        initializeWithGameEngine();
    }

    /**
     * Get the current floor. Zero-indexed.
     * @return The current floor.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    /**
     * Handle keypresses.
     * @param e The KeyEvent.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (debugPanel != null) {
            debugPanel.updateKeysPressed(DebugPanel.keyLabelAction.ADD, e.getKeyCode());
            gameEngine.keyPressed(e);
        }
    }

    /**
     * Handle keyreleases.
     * @param e The KeyEvent.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (debugPanel != null) {
            debugPanel.updateKeysPressed(DebugPanel.keyLabelAction.REMOVE, e.getKeyCode());
        }
        gameEngine.keyReleased(e);
    }
}
