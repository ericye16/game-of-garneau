import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

public class GameEngine {
    final static float SPEED = 4.f;

    private ArrayList<Entity> entities;
    private MapRenderer mapRenderer;
    private World world;
    private PlayerStudent playerStudent;
    private Body playerStudentBody;
    private HashSet<Integer> keysPressed = new HashSet<Integer>();
    private Logger logger = Logger.getLogger("GameEngine");
    private Timer gameTimer;
    private Timer renderTimer;
    private DebugPanel debugPanel;
    private ArrayList<Body> collisionBodies = new ArrayList<Body>();
    private GameContactListener gameContactListener;

    /**
     * Performance settings. If you have issues with running the game, try lowering some of these.
     */
    final private static int RENDER_FPS = 60; //number of frames to render per second
    final private static int PHYSICS_FPS = 30; //number of physics time steps per second
    final private static int VELOCITY_ITERATIONS = 8; //quality of velocity calculations
    final private static int POSITION_ITERATIONS = 6; //quality of position calculations

    public GameEngine(final MapRenderer mapRenderer) {
        assert(mapRenderer != null);
        this.mapRenderer = mapRenderer;
        gameContactListener = new GameContactListener(this);
        playerStudent = new PlayerStudent();

        resetWorld();
    }

    public void addCollisionArea(float x, float y, float xsize, float ysize, Object userdata) {
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyType.STATIC;
        bodyDef1.active = true;
        bodyDef1.allowSleep = true;
        bodyDef1.position = new Vec2(x + xsize / 2, y + ysize / 2);
        bodyDef1.userData = userdata;
        bodyDef1.linearVelocity = new Vec2(0, 0);
        PolygonShape polygonShape1 = new PolygonShape();
        polygonShape1.setAsBox(xsize / 2, ysize / 2);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = polygonShape1;
        fixtureDef1.restitution = 0;
        fixtureDef1.friction = 0;
        Body body1 = world.createBody(bodyDef1);
        logger.fine("Creating collision object: " + x + ", " + y + ", " + xsize + "," + ysize);
        body1.createFixture(fixtureDef1);
        collisionBodies.add(body1);
    }

    private void addWalls() {
        class VertexPair {
            public Vec2 a, b;
            public VertexPair(Vec2 a, Vec2 b) {
                this.a = a;
                this.b = b;
            }
        }
        int side1 = 21;
        int side2 = 21;
        VertexPair[] walls = new VertexPair[] {
                new VertexPair(
                        new Vec2 (0, 0),
                        new Vec2(0, side1)
                ),
                new VertexPair(
                        new Vec2(0, side1),
                        new Vec2(side2, side1)
                ),
                new VertexPair(
                        new Vec2(side2, side1),
                        new Vec2(side2, 0)
                ),
                new VertexPair(
                        new Vec2(side2, 0),
                        new Vec2(0, 0)
                )
        };
        for (VertexPair vertexPair: walls) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.STATIC;
            bodyDef.active = true;
            bodyDef.allowSleep = true;
            FixtureDef fixtureDef = new FixtureDef();
            EdgeShape edgeShape = new EdgeShape();
            edgeShape.set(vertexPair.a, vertexPair.b);
            fixtureDef.shape = edgeShape;
            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
        }
    }

    public void resetWorld() {
        this.world = new World(new Vec2(0, 0));
        world.setAllowSleep(true);
        world.setContinuousPhysics(false);
        world.setContactListener(gameContactListener);
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
        addWalls();
        logger.info("Simulating physics at " + 1000 / PHYSICS_FPS);
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateState();
                world.step(1.f / PHYSICS_FPS, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
                if (debugPanel != null) {
                    float[] playerPositionFloat = getPlayerLocation();
                    debugPanel.updatePlayerPosition(playerPositionFloat[0], playerPositionFloat[1]);
                    //logger.info(playerPosition.x + "," + playerPosition.y);
                    debugPanel.repaint();
                }
            }
        }, 0, 1000 / PHYSICS_FPS);
        logger.info("Rendering at " + 1000 / RENDER_FPS);
        renderTimer = new Timer();
        renderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mapRenderer.repaint();
            }
        }, 0, 1000 / RENDER_FPS);
        collisionBodies.clear();
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
        Vec2 linearVelocity = new Vec2(right, -up);

        playerStudentBody.setLinearVelocity(linearVelocity);
        //logger.info("Moving with linVel: " + linearVelocity);
    }

    public float[] getPlayerLocation() {
        Vec2 position = playerStudentBody.getPosition();
        return new float[] {position.x, position.y};
    }

    public void setPlayerLocation(double[] location) {
        playerStudent.setLocation(location);
    }

    public void stopAll() {
        renderTimer.cancel();
        gameTimer.cancel();
    }

    public MapRenderer getMapRenderer() {
        return mapRenderer;
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
