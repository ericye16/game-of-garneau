import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public abstract class Entity {

    protected double[] location = new double[3];
    protected boolean canMove;

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public abstract BufferedImage getSprite();

    public abstract Rectangle getRectangle();
}
