import java.util.ArrayList;

public class GameEngine {
    private char[][][] map;
    private ArrayList<Entity> entities;
    private MapRenderer mapRenderer;

    private void renderEntities() {
        for (Entity entity: entities) {
            mapRenderer.renderEntityAt(entity, entity.getLocation());
        }
    }

    public void render() {
       renderEntities();
    }

    public GameEngine(MapRenderer mapRenderer) {
        assert(mapRenderer != null);
        this.mapRenderer = mapRenderer;
        mapRenderer.setMap(map);
    }

}
