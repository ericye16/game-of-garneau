import java.util.ArrayList;

public class GameEngine {
    private char[][][] map;
    private ArrayList<Entity> entities;

    private void renderMap() {
        //TODO: implement
    }
    private void renderEntityAt(Entity entity, double[] location) {
        assert(location.length == 3);

    }
    private void renderEntities() {
        for (Entity entity: entities) {
            renderEntityAt(entity, entity.getLocation());
        }
    }

    public void render() {
        renderMap();
        renderEntities();
    }

}
