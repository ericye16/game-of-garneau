import tiled.core.*;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

/**
 * Class to render maps based on Tiled map file (TMX) files.
 * More information on the Tiled Map Editor can be found here: http://www.mapeditor.org/
 */
public class MapRenderer extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
    private DebugPanel debugPanel;
    protected GameEngine gameEngine = new GameEngine(this);
    protected TileSet tileSet;
    protected Map tiledmap;
    protected Color bgcolour;
    protected tiled.view.MapRenderer tiledMapRenderer; //really unfortunate name collision

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
        for (MapLayer layer: tiledmap) {
            if (layer instanceof TileLayer) {
                tiledMapRenderer.paintTileLayer(g2d, (TileLayer) layer);
            }
        }
    }

    public MapRenderer(String filename) {
        try {
            tiledmap = new TMXMapReader().readMap(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setOpaque(true);

        //we draw the entire map
        int width = tiledmap.getWidth() * tiledmap.getTileWidth();
        int height = tiledmap.getHeight() * tiledmap.getTileHeight();
        setPreferredSize(new Dimension(width, height));

        //we only support orthogonal maps
        if (tiledmap.getOrientation() != Map.ORIENTATION_ORTHOGONAL) {
            throw new IllegalArgumentException("Can only use orthogonal tiled maps.");
        }
        tiledMapRenderer = new OrthogonalRenderer(tiledmap);
        ObjectGroup collisionGroup = (ObjectGroup) tiledmap.getLayer(1);
        Iterator<MapObject> objects= collisionGroup.getObjects();
        while(objects.hasNext()) {
            MapObject obj = objects.next();
            gameEngine.addCollisionArea(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), null);
        }

        //what's our background colour?
        bgcolour = new Color(100, 100, 100);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
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
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
