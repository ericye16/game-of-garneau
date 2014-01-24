import sun.util.logging.resources.logging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PlayerStudent extends Entity {
    private Logger logger = Logger.getLogger("PlayerStudent");
    private int sleep;
    private int grades;
    private int social;
    private Rectangle rectangle = new Rectangle(16, 16);
    private BufferedImage sprite;

    /**
     * Initializes a playable student
     * Default location is near the office entrance, on the first floor
     */
    public PlayerStudent () {
        try {
            sprite = ImageIO.read(new File("res/sprites/playerstudent.png"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe(e.getLocalizedMessage());
            throw new InternalError("Could not load sprite");
        }
        sleep = 100;
        grades = 70;
        social = 0;
        canMove = true;
        location[0] = 9;
        location[1] = 19;
        location[2] = 0;
    }

    public void decSleep () {
        if(sleep > 0){
            sleep--;
        }
    }

    public void addSleep () {
        if(sleep < 100){
            sleep++;
        }
    }

    public int getSleep(){
        return sleep;
    }

    public void decGrades (){
        if(grades>0){
            grades--;
        }
    }

    public void addGrades (int add){
        if(grades + add <= 100) {
            grades += add;
        }else if (grades + add > 100){
            grades = 100;
        }
    }

    public int getGrades (){
        return grades;
    }

    public void addSocial(int add){
        if(social + add <= 100){
            social += add;
        }else if (social + add > 100) {
            social = 100;
        }
    }

    public int getSocial(){
        return social;
    }

    @Override
    public BufferedImage getSprite() {
        return sprite;
    }

    @Override
    public Rectangle getRectangle() {
        return rectangle;
    }
}
