import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: Yaning
 * Date: 16/01/14
 * Time: 8:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerStudent extends Entity {

    private int sleep;
    private int grades;
    private int social;

    /**
     * Initializes a playable student
     * Their location is near the office entrance
     */
    public PlayerStudent () {
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
    protected boolean canMoveTo(double[] newLoc) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BufferedImage getSprite() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
