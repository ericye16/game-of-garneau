import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.TMXMapReader;
import tiled.view.OrthogonalRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Class to render maps based on Tiled map file (TMX) files.
 * More information on the Tiled Map Editor can be found here: http://www.mapeditor.org/
 */
public class MapRenderer extends JPanel {
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

        //what's our background colour?
        bgcolour = new Color(100, 100, 100);
    }
}
