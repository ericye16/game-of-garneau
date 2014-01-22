import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.logging.Logger;

public class GameContactListener implements org.jbox2d.callbacks.ContactListener {
    private Logger logger = Logger.getLogger("GameContactListener");
    @Override
    public void beginContact(Contact contact) {
        logger.info("Got contact");
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
