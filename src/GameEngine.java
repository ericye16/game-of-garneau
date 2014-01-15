import java.awt.*;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class GameEngine {
    private ArrayList<Entity> entities;
    private MapRenderer mapRenderer;
    private World world;
    private BodyDef bodyDef = new BodyDef();
    private FixtureDef fixtureDef = new FixtureDef();
    private PolygonShape polygonShape = new PolygonShape();

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

    public void addCollisionArea(float x, float y, float xsize, float ysize, Entity entity) {
        bodyDef.type = BodyType.STATIC;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.position = new Vec2(x, y);
        bodyDef.userData = entity;
        polygonShape.setAsBox(xsize / 2, ysize / 2);
        fixtureDef.shape = polygonShape;
        Body body1 = world.createBody(bodyDef);
        body1.createFixture(fixtureDef);
    }

    public World getWorld() {
        return world;
    }

}
