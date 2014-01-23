import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import sun.util.logging.resources.logging;

import java.util.logging.Logger;

public class GameContactListener implements org.jbox2d.callbacks.ContactListener {
    private Logger logger = Logger.getLogger("GameContactListener");
    @Override
    public void beginContact(Contact contact) {
        boolean a_is_player = contact.getFixtureA().getBody().getUserData() instanceof PlayerStudent;
        boolean b_is_player = contact.getFixtureB().getBody().getUserData() instanceof PlayerStudent;
        Fixture other;
        if (a_is_player ^ b_is_player) {
            other = a_is_player ? contact.getFixtureB() : contact.getFixtureA();
            if (other != null && other.getBody().getUserData() == Entities.DOOR) {
                logger.fine("Hit a door!");
            }
            logger.fine("Got contact");
        }
    }

    @Override
    public void endContact(Contact contact) {
        logger.info("Un-got contact");
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
