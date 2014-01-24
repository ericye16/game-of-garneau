import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public abstract class Entity {

    protected double[] location = new double[3];
    protected boolean canMove;
    protected double angle;

    /**
     * Set the orientation of the entity. 0 radian is facing downward and it counts clockwise.
     * @param angle The angle in radian.
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Get the orientation of the entity. 0 radian is facing downward and it counts clockwise.
     * @return The angle in radian.
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Get the location vector of the entity.
     * @return The location vector.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * Set the location vector of the entity.
     * @param location The location vector.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }

    public abstract BufferedImage getSprite();

    /**
     * Get the approximate width and height of the entity.
     * @return The rectangle representing its size.
     */
    public abstract Rectangle getRectangle();
}
