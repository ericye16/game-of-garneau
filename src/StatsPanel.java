import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {

    public StatsPanel (int width, int height)                    //constructor initializes size
    {
        this.setPreferredSize (new Dimension(width, height));
    }

    public void paintComponent(Graphics g)
    {
        int barHeight = (int)(getHeight()*5.0/7.0);
        int barWidth = (int)(getWidth()/9.0);

        int sleepx = (int)(getWidth()/9.0);
        int sleepy;
        int socialx = (int)(getWidth()/3.0);
        int socialy;
        int gradesx = (int)(getWidth()*5.0/9.0);
        int gradesy;
        int bladderx = (int)(getWidth()*7.0/9.0);


        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.GRAY);
        g2.drawRect(sleepx,(int)(barHeight/5.0), barWidth, barHeight);         //meter bars
        g2.drawRect(socialx,(int)(barHeight/5.0), barWidth, barHeight);
        g2.drawRect(gradesx,(int)(barHeight/5.0), barWidth, barHeight);
        g2.drawRect(bladderx,(int)(barHeight/5.0), barWidth, barHeight);

        g2.setColor(Color.red);
        //g2.drawRect(
        repaint();
    }

}
