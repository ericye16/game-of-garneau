import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * An Enemy object.
 */
public class Enemy extends Entity{

    private Logger logger = Logger.getLogger("Enemy");
    private Rectangle rectangle = new Rectangle(16, 16);
    private static BufferedImage sprite;

    /**
     * Constructor for the Enemy object. Reads in the sprite if it hasn't been yet.
     */
    public Enemy() {
        if (sprite == null) {
            try {
                sprite = ImageIO.read(new File("res/sprites/enemy.png"));
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe("Could not read sprite");
                throw new InternalError();
            }
        }
    }

    /**
     * Set the location of the enemy.
     * @param location The location vector.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }

    /**
     * Get the sprite.
     * @return
     */
    @Override
    public BufferedImage getSprite() {
        return sprite;
    }

    /**
     * Get the sprite, but statically.
     * @return
     */
    public static BufferedImage getSpriteStatic() {
        return sprite;
    }

    /**
     * Get the approximate width of the enemy.
     * @return
     */
    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
