import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public abstract class Entity {
    private static Logger logger = Logger.getLogger("Entity");

    protected double[] location = new double[3];
    protected boolean canMove;

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    /**
     * Must be overridden in subclasses. Returns true if the newLoc on the board is reachable.
     * @param newLoc A double array of length 3.
     * @return True if the location is reachable.
     */
    protected abstract boolean canMoveTo(double[] newLoc);

    public boolean move(double[] move) {
        double[] newLoc = location.clone();
        for (int i = 0; i < newLoc.length; i++) {
            newLoc[i] += move[i];
        }
        boolean success;
        if (canMoveTo(newLoc)) {
            location = newLoc;
            success = true;
            logger.info("Successfully moved to " + newLoc);
        } else {
            success = false;
            logger.warning("Unsuccessful move to " + newLoc);
        }
        return success;
    }

    public boolean canMove() {
        return canMove;
    }

    public abstract BufferedImage getSprite();
}
