import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.logging.Logger;

public class GameContactListener implements org.jbox2d.callbacks.ContactListener {
    private Logger logger = Logger.getLogger("GameContactListener");
    private GameEngine gameEngine;
    private MapRenderer mapRenderer;
    @Override
    public void beginContact(Contact contact) {
        boolean a_is_player = contact.getFixtureA().getBody().getUserData() instanceof PlayerStudent;
        boolean b_is_player = contact.getFixtureB().getBody().getUserData() instanceof PlayerStudent;
        Fixture other;
        if (a_is_player ^ b_is_player) {
            other = a_is_player ? contact.getFixtureB() : contact.getFixtureA();
            if (other != null)
                if (other.getBody().getUserData() == Entities.DOOR) {
                    logger.fine("Hit a door!");
                } else if (other.getBody().getUserData() == Entities.STAIRS_DOWN) {
                    mapRenderer.goDownTheStairs();
                    logger.fine("Going up the stairs.");
                } else if (other.getBody().getUserData() == Entities.STAIRS_UP) {
                    mapRenderer.goUpTheStairs();
                    logger.fine("Going down the stairs.");
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
