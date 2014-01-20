import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import sun.util.logging.resources.logging;

public class GameEngine {
    private ArrayList<Entity> entities;
    private MapRenderer mapRenderer;
    private World world;
    private BodyDef bodyDef = new BodyDef();
    private FixtureDef fixtureDef = new FixtureDef();
    private PolygonShape polygonShape = new PolygonShape();
    private PlayerStudent playerStudent;
    private Body playerStudentBody;
    private HashSet<Integer> keysPressed = new HashSet<Integer>();
    private Logger logger = Logger.getLogger("GameEngine");
    private Timer gameTimer = new Timer();
    private DebugPanel debugPanel;

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
        world.setAllowSleep(true);
        world.setContinuousPhysics(false);
        playerStudent = new PlayerStudent();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.position = new Vec2((float) playerStudent.getLocation()[0], (float) playerStudent.getLocation()[1]);
        bodyDef.userData = playerStudent;
        bodyDef.linearVelocity = new Vec2(0, 0);
        polygonShape.setAsBox(16, 16);
        fixtureDef.shape = polygonShape;
        playerStudentBody = world.createBody(bodyDef);
        playerStudentBody.createFixture(fixtureDef);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateState();
                world.step(0.033f, 8, 3);
                if (debugPanel != null) {
                    Vec2 playerPosition = playerStudentBody.getPosition();
                    debugPanel.updatePlayerPosition(playerPosition.x, playerPosition.y);
                }
            }
        }, 0, 33);
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

    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
        //updateState();
    }

    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
        //updateState();
    }

    private void updateState() {
        float up = 0.f;
        float right = 0.f;
        if (keysPressed.contains(KeyEvent.VK_W)) {
            up+= 1.f;
        }
        if (keysPressed.contains(KeyEvent.VK_S)) {
            up-= 1.f;
        }
        if (keysPressed.contains(KeyEvent.VK_D)) {
            right+=1.f;
        }
        if (keysPressed.contains(KeyEvent.VK_A)) {
            right-=1.f;
        }
        if (right != 0 && up != 0) {
            right /= Math.sqrt(2);
            up /= Math.sqrt(2);
        }
        Vec2 linearVelocity = new Vec2(right, up);

        playerStudentBody.setLinearVelocity(linearVelocity);
        mapRenderer.repaint();
        logger.info("Moving with linVel: " + linearVelocity);
    }

    public float[] getPlayerLocation() {
        Vec2 position = playerStudentBody.getPosition();
        return new float[] {position.x, position.y};
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
    }

}
