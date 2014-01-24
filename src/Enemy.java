import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Enemy extends Entity{

    private Logger logger = Logger.getLogger("Enemy");
    private Rectangle rectangle = new Rectangle(16, 16);
    private static BufferedImage sprite;

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

    public void setLocation(double[] location) {
        this.location = location;
    }

    @Override
    public BufferedImage getSprite() {
        return sprite;
    }

    public static BufferedImage getSpriteStatic() {
        return sprite;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
