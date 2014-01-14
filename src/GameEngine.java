import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

public class GameEngine {
    private ArrayList<Entity> entities;
    private MapRenderer mapRenderer;
    private World world;
    private BodyDef bodyDef = new BodyDef();

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

        this.world = new World(new Vec2(0, 0));

    }

    private void addCollisionArea(float x, float y, float xsize, float ysize) {
        bodyDef.type = BodyType.STATIC;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.position = new Vec2(x, y);
        world.createBody(bodyDef);
        //todo: joints
    }

    public World getWorld() {
        return world;
    }

}
