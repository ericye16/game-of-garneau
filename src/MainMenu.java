import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowFocusListener;

public class MainMenu extends JFrame implements ActionListener, WindowListener, WindowFocusListener
{
    private BufferedImage titleImage;
    private JButton bPlay = new JButton ("Play");
    private JButton bInst = new JButton ("Instructions");
    private JButton bQuit = new JButton ("Quit");
    private JButton bSett = new JButton ("Settings");
    // private Settings settingsWindow = new Settings ();
    // private GameScreen gameScreenWindow = new GameScreen ();
    // private Instructions instructionsWindow = new Instructions ();
    private Color[] colorScheme = new Color [5];
    private int[] backgroundChoices = new int [2];
    private String[] playerNames = new String [4];
    private JPanel screen = new JPanel (new BorderLayout ());

    public MainMenu ()
    {
	this.setTitle ("Game Of Garneau v.1.0: Main Menu");

	// colorScheme = settingsWindow.getColors ();
	bPlay.setBackground (colorScheme [1]);
	bInst.setBackground (colorScheme [2]);
	bQuit.setBackground (colorScheme [3]);
	bSett.setBackground (colorScheme [4]);

	// playerNames = settingsWindow.getNames ();

	// gameScreenWindow.setSettings (colorScheme, playerNames);

	bPlay.addActionListener (this);
	bInst.addActionListener (this);
	bQuit.addActionListener (this);
	bSett.addActionListener (this);

	setSize (400, 400);
	setResizable (false);

	// gameScreenWindow.setVisible (false);
	// gameScreenWindow.setResizable (false);
	// 
	// instructionsWindow.setVisible (false);
	// instructionsWindow.setResizable (false);
	// 
	// settingsWindow.setVisible (false);
	// settingsWindow.setResizable (false);

	try
	{
	    titleImage = ImageIO.read (this.getClass ().getResource ("game.png"));
	}
	catch (IOException e)
	{
	    //erherhah
	}
	JLabel title = new JLabel (new ImageIcon (titleImage));
	title.setPreferredSize (new Dimension (320, 130));

	JPanel options = new JPanel (new GridLayout (0, 1));
	options.setSize (new Dimension (300, 300));
	options.add (bPlay);
	options.add (bInst);
	options.add (bSett);
	options.add (bQuit);

	screen.add (options, BorderLayout.CENTER);
	screen.add (title, BorderLayout.NORTH);
	screen.setBorder (BorderFactory.createEmptyBorder (40, 40, 40, 40));
	screen.setBackground (colorScheme [0]);

	this.getContentPane ().add (screen);
	setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
	addWindowListener (this);
	addWindowFocusListener (this);

    }


    public void windowGainedFocus (WindowEvent e)
    {
	// colorScheme = settingsWindow.getColors ();

	screen.setBackground (colorScheme [0]);
	bPlay.setBackground (colorScheme [1]);
	bInst.setBackground (colorScheme [2]);
	bQuit.setBackground (colorScheme [3]);
	bSett.setBackground (colorScheme [4]);

	this.getRootPane ().revalidate ();
	this.getContentPane ().repaint ();

	// playerNames = settingsWindow.getNames ();

	// gameScreenWindow.setSettings (colorScheme, playerNames);
    }


    public void windowLostFocus (WindowEvent e)
    {

    }


    public void windowClosed (WindowEvent arg0)
    {
    }


    public void windowActivated (WindowEvent arg0)
    {
    }


    public void windowClosing (WindowEvent arg0)
    {
	System.exit (0);
    }


    public void windowDeactivated (WindowEvent arg0)
    {
    }


    public void windowDeiconified (WindowEvent arg0)
    {
    }


    public void windowIconified (WindowEvent arg0)
    {
    }


    public void windowOpened (WindowEvent arg0)
    {
    }


    public void actionPerformed (ActionEvent e)
    {
	JButton temp = (JButton) e.getSource ();

	// if (temp.equals (bPlay))
	// {
	//     gameScreenWindow.setVisible (true);
	// }
	// 
	// else if (temp.equals (bInst))
	//     instructionsWindow.setVisible (true);
	// 
	// else if (temp.equals (bSett))
	//     settingsWindow.setVisible (true);
	// 
	// else
	//     System.exit (0);
    }


    public static void main (String s[])
    {
	MainMenu main = new MainMenu ();
	main.setVisible (true);
    }
}
