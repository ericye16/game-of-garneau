import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class GameEngine {
    final static float SPEED = 2.f;

    private ArrayList<Entity> entities;
    public MapRenderer mapRenderer;
    private World world;
    private PlayerStudent playerStudent;
    private Body playerStudentBody;
    private HashSet<Integer> keysPressed = new HashSet<Integer>();
    private Logger logger = Logger.getLogger("GameEngine");
    private Timer gameTimer = new Timer();
    private Timer renderTimer = new Timer();
    private DebugPanel debugPanel;
    private ArrayList<Body> collisionBodies = new ArrayList<Body>();

    private void renderEntities() {
        for (Entity entity: entities) {
            mapRenderer.renderEntityAt(entity, entity.getLocation());
        }
    }

    public void render() {
       renderEntities();
    }

    public GameEngine(final MapRenderer mapRenderer) {
        assert(mapRenderer != null);
        this.mapRenderer = mapRenderer;

        this.world = new World(new Vec2(0, 0));
        world.setAllowSleep(true);
        world.setContinuousPhysics(false);
        playerStudent = new PlayerStudent();
        BodyDef playerBodyDef = new BodyDef();
        playerBodyDef.type = BodyType.DYNAMIC;
        playerBodyDef.active = true;
        playerBodyDef.allowSleep = true;
        playerBodyDef.position = new Vec2((float) playerStudent.getLocation()[0], (float) playerStudent.getLocation()[1]);
        playerBodyDef.userData = playerStudent;
        playerBodyDef.linearVelocity = new Vec2(0, 0);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.25f, 0.25f);
        FixtureDef playerFixture = new FixtureDef();
        playerFixture.shape = polygonShape;
        playerFixture.friction = 0;
        playerFixture.density = 5.f;
        playerStudentBody = world.createBody(playerBodyDef);
        playerStudentBody.createFixture(playerFixture);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateState();
                world.step(0.033f, 8, 3);
                if (debugPanel != null) {
                    Vec2 playerPosition = playerStudentBody.getPosition();
                    float[] playerPositionFloat = getPlayerLocation();
                    debugPanel.updatePlayerPosition(playerPositionFloat[0], playerPositionFloat[1]);
                    //logger.info(playerPosition.x + "," + playerPosition.y);
                    debugPanel.repaint();
                }
            }
        }, 0, 33);
        renderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mapRenderer.repaint();
            }
        }, 0, 50);
    }

    public void addCollisionArea(float x, float y, float xsize, float ysize, Entity entity) {
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyType.STATIC;
        bodyDef1.active = true;
        bodyDef1.allowSleep = true;
        bodyDef1.position = new Vec2(x + xsize / 2, y + ysize / 2);
        bodyDef1.userData = entity;
        bodyDef1.linearVelocity = new Vec2(0, 0);
        PolygonShape polygonShape1 = new PolygonShape();
        polygonShape1.setAsBox(xsize / 2, ysize / 2);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = polygonShape1;
        fixtureDef1.restitution = 0;
        fixtureDef1.friction = 0;
        Body body1 = world.createBody(bodyDef1);
        logger.info("Creating collision object: " + x + ", " + y + ", " + xsize + "," + ysize);
        body1.createFixture(fixtureDef1);
        collisionBodies.add(body1);
    }

    public World getWorld() {
        return world;
    }

    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }

    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    private void updateState() {
        float up = 0.f;
        float right = 0.f;
        if (keysPressed.contains(KeyEvent.VK_W)) {
            up+= SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_S)) {
            up-= SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_D)) {
            right+= SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_A)) {
            right-= SPEED;
        }
        if (right != 0 && up != 0) {
            right /= Math.sqrt(2 * SPEED);
            up /= Math.sqrt(2 * SPEED);
        }
        Vec2 linearVelocity = new Vec2(right, -up);

        playerStudentBody.setLinearVelocity(linearVelocity);
        //logger.info("Moving with linVel: " + linearVelocity);
    }

    public float[] getPlayerLocation() {
        Vec2 position = playerStudentBody.getPosition();
        return new float[] {position.x, position.y};
    }

    public Body getPlayerStudentBody() {
        return playerStudentBody;
    }

    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
    }

    public ArrayList<Body> getCollisionBodies() {
        return collisionBodies;
    }
}
