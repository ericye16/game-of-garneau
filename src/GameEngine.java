import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Class to handle the motion and interaction with the physics and collisions involved.
 * Extensively uses the jbox2d physics engine. More information can be found here:
 * http://jbox2d.org/
 */
public class GameEngine {
    final static float SPEED = 4.f;
    static float ENEMY_SPEED = 0.7f * SPEED;

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
    private ArrayList<Body> enemyBodies = new ArrayList<Body>();
    private int numEnemiesPerLevel = 0;
    private boolean paused = false;

    /**
     * Performance settings. If you have issues with running the game, try lowering some of these.
     */
    final private static int RENDER_FPS = 60; //number of frames to render per second
    final private static int PHYSICS_FPS = 30; //number of physics time steps per second
    final private static int VELOCITY_ITERATIONS = 8; //quality of velocity calculations
    final private static int POSITION_ITERATIONS = 6; //quality of position calculations

    /**
     * Constructor for the GameEngine class.
     * @param mapRenderer The MapRenderer to attach to the Game Engine
     */
    public GameEngine(final MapRenderer mapRenderer) {
        assert(mapRenderer != null);
        this.mapRenderer = mapRenderer;
        gameContactListener = new GameContactListener(this);
        playerStudent = new PlayerStudent();

        resetWorld();
    }

    /**
     * Add a collision area to the world, such as a classroom or a door.
     * @param x The x-location of the left top corner of the area, in tiles.
     * @param y The y-location of the left top corner of the area in tiles.
     * @param xsize The width of the area in tiles.
     * @param ysize The height of the area in tiles.
     * @param userdata Any special data to attach to the area, such as its type.
     */
    public void addCollisionArea(float x, float y, float xsize, float ysize, Object userdata) {
        //begin defining the collision area
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyType.STATIC;
        bodyDef1.active = true;
        bodyDef1.allowSleep = true;
        bodyDef1.position = new Vec2(x + xsize / 2, y + ysize / 2);
        bodyDef1.userData = userdata;
        bodyDef1.linearVelocity = new Vec2(0, 0);
        bodyDef1.fixedRotation = true;
        //give it some shape
        PolygonShape polygonShape1 = new PolygonShape();
        polygonShape1.setAsBox(xsize / 2, ysize / 2);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = polygonShape1;
        fixtureDef1.restitution = 0;
        fixtureDef1.friction = 0;
        Body body1 = world.createBody(bodyDef1);
        logger.fine("Creating collision object: " + x + ", " + y + ", " + xsize + "," + ysize);
        while (body1.m_world.isLocked())
            ;//wait for it to unlock before we add the fixture, otherwise we get a null pointer exception
        body1.createFixture(fixtureDef1);
        collisionBodies.add(body1);
    }

    /**
     * Add the bordering walls to the world.
     */
    private void addWalls() {
        //an edge is defined by a pair of vertices
        class VertexPair {
            public Vec2 a, b;
            public VertexPair(Vec2 a, Vec2 b) {
                this.a = a;
                this.b = b;
            }
        }
        int side1 = 21;
        int side2 = 21;
        //all of the walls
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
        //create all of the walls
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

    /**
     * Add a number of enemies to the world.
     */
    private void addEnemies() {
        enemyBodies.clear();
        for (int i = 0; i < numEnemiesPerLevel; i++) {
            addEnemy();
        }
    }

    /**
     * Get an ArrayList of Body objects that represent the enemies in the game.
     * @return The ArrayList of Bodies.
     */
    public ArrayList<Body> getEnemyBodies() {
        return enemyBodies;
    }

    /**
     * Add a single enemy body randomly in the world, and append it to the ArrayList of existing bodies.
     */
    private void addEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.active = true;
        bodyDef.allowSleep = true;
        bodyDef.position = new Vec2((float) Math.random() * 20, (float) Math.random() * 20);
        bodyDef.userData = new Enemy();
        ((Enemy) bodyDef.userData).setLocation(new double[] {bodyDef.position.x, bodyDef.position.y});
        bodyDef.linearVelocity = new Vec2();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(.25f, .25f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        Body enemyBody = world.createBody(bodyDef);
        while (world.isLocked())
            ;
        enemyBody.createFixture(fixtureDef);
        enemyBodies.add(enemyBody);
    }

    /**
     * Reset the physics world, for example when going up or down stairs. Clears and re-adds everything required.
     */
    public void resetWorld() {
        //create the world
        this.world = new World(new Vec2(0, 0));
        world.setAllowSleep(true);
        world.setContinuousPhysics(false);
        world.setContactListener(gameContactListener);

        //begin defining the player
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

        //add the walls, enemies, etc.
        addWalls();
        increaseDifficulty();
        addEnemies();
        logger.info("Simulating physics at " + 1000 / PHYSICS_FPS);
        gameTimer = new Timer();

        logger.info("Rendering at " + 1000 / RENDER_FPS);
        renderTimer = new Timer();
        collisionBodies.clear();
    }

    /**
     * Increase the difficulty of the <i>next</i> world by adding more and faster enemies.
     */
    public void increaseDifficulty() {
        numEnemiesPerLevel++;
        ENEMY_SPEED += .1f;
    }

    /**
     * Decrease the difficulty of the <i>next</i> world by removing enemies and making them slower.
     */
    public void decreaseDifficult() {
        if (numEnemiesPerLevel > 1) {
            numEnemiesPerLevel--;
        }
        if (ENEMY_SPEED > 0.7f) {
            ENEMY_SPEED -= .1f;
        }
    }

    /**
     * Start the timers that control the physics and render loops.
     */
    public void go() {
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
        renderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mapRenderer.repaint();
            }
        }, 0, 1000 / RENDER_FPS);
    }

    /**
     * Stop and reset the timers that control the physics and render loop.
     */
    private void pause() {
        gameTimer.cancel();
        renderTimer.cancel();
        gameTimer = new Timer();
        renderTimer = new Timer();
    }

    /**
     * Handle a key being pressed.
     * @param e The keyEvent.
     */
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (paused) {
                go();
            } else {
                pause();
            }
            paused = !paused;
        }
    }

    /**
     * Handle a key being released.
     * @param e The keyEvent.
     */
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    /**
     * Update the state (i.e. velocities) of all entities in the world.
     * This is called once every physics loop, right before the physics engine takes time step.
     */
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
        if (right != 0 || up != 0) {
            //set the angle so the renderer can orient us properly
            playerStudent.setAngle(Math.atan2(-right, -up));
        }

        enemyAI();

        for (Body enemyBody: enemyBodies) {
            syncEnemyPosition(enemyBody);
        }
    }

    /**
     * Control the enemy's direction and heading. Currently this is implemented by simply
     * pointing the enemy at the player regardless of any obstructions, but it becomes
     * difficult quickly enough.
     */
    private void enemyAI() {
        for (Body enemyBody: enemyBodies) {
            Vec2 delta_position = playerStudentBody.getPosition().sub(enemyBody.getPosition()); //we want to go this way
            delta_position.normalize();
            delta_position = delta_position.mul(ENEMY_SPEED);
            enemyBody.setLinearVelocity(delta_position);

            //calculate our angle and set it so the renderer can orient it properly
            double theta = Math.atan2(-delta_position.x, delta_position.y);
            Enemy enemy = (Enemy) enemyBody.getUserData();
            enemy.setAngle(theta);
        }
    }

    /**
     * Synchronize the Enemy class's position vector with the Body class's position vector.
     * This is because the Enemy class's position vector is used by the renderer whereas
     * the Body class's position vector is used by the physics engine.
     * @param enemyBody The Body (with Enemy in its userData field) to synchronize.
     */
    private void syncEnemyPosition(Body enemyBody) {
        Enemy enemy = (Enemy) enemyBody.getUserData();
        Vec2 location = enemyBody.getPosition();
        enemy.setLocation(new double[] {location.x, location.y});
    }

    /**
     * Get the location, in tiles, of the player.
     * @return The location of the player.
     */
    public float[] getPlayerLocation() {
        Vec2 position = playerStudentBody.getPosition();
        return new float[] {position.x, position.y};
    }

    /**
     * Set the initial location of a player, in tiles. Used for changing floors.
     * @param location The location of the player.
     */
    public void setPlayerLocation(double[] location) {
        playerStudent.setLocation(location);
    }

    /**
     * Stop all the timers without creating new ones, for example when switching floors.
     */
    public void stopAll() {
        renderTimer.cancel();
        gameTimer.cancel();
    }

    /**
     * Get the MapRenderer object associated with this GameEngine.
     * @return The MapRenderer associated.
     */
    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }

    /**
     * Unused.
     * @return
     */
    public Body getPlayerStudentBody() {
        return playerStudentBody;
    }

    /**
     * Get the PlayerStudent entity object.
     * @return The PlayerStudent.
     */
    public PlayerStudent getPlayerStudent() {
        return playerStudent;
    }

    /**
     * Set the DebugPanel to send messages to.
     * @param debugPanel The DebugPanel.
     */
    public void setDebugPanel(DebugPanel debugPanel) {
        this.debugPanel = debugPanel;
    }

    /**
     * Get the ArrayList of collision areas (rooms, doors, etc). For debugging.
     * @return The ArrayList of Collision Bodies.
     */
    public ArrayList<Body> getCollisionBodies() {
        return collisionBodies;
    }
}
