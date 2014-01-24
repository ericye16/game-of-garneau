import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameContactListener implements org.jbox2d.callbacks.ContactListener {
    private Logger logger = Logger.getLogger("GameContactListener");
    private GameEngine gameEngine;
    private MapRenderer mapRenderer;

    /**
     * Handle contacts/collisions being made.
     * @param contact The contact object representing the collision.
     */
    @Override
    public void beginContact(Contact contact) {
        //check which fixture was the player
        boolean a_is_player = contact.getFixtureA().getBody().getUserData() instanceof PlayerStudent;
        boolean b_is_player = contact.getFixtureB().getBody().getUserData() instanceof PlayerStudent;
        Fixture other;
        if (a_is_player ^ b_is_player) {
            other = a_is_player ? contact.getFixtureB() : contact.getFixtureA();
            if (other != null)
            /**
             * Deal with the different cases of the player bumping into things.
             */
                if (other.getBody().getUserData() == Entities.DOOR) { //hit a door
                    gameEngine.increasePoints(5 + 100 * mapRenderer.getCurrentFloor());
                    logger.fine("Hit a door!");
                } else if (other.getBody().getUserData() == Entities.STAIRS_DOWN) { //went down the stairs
                    mapRenderer.goDownTheStairs();
                    logger.fine("Going up the stairs.");
                } else if (other.getBody().getUserData() == Entities.STAIRS_UP) { //went up the stairs
                    mapRenderer.goUpTheStairs();
                    logger.fine("Going down the stairs.");
                } else if (other.getBody().getUserData() instanceof Enemy) {
                    gameEngine.decreasePoints(100 + 200 * mapRenderer.getCurrentFloor());
                    logger.info("Hit by an enemy!");
                }
            logger.fine("Got contact");
        }
    }

    public GameContactListener(GameEngine gameEngine) {
        super();
        this.gameEngine = gameEngine;
        this.mapRenderer = gameEngine.getMapRenderer();
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
