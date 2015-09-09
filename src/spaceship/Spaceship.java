
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;
    boolean test;
    
    
    
    sound zsound = null;
    sound bgSound = null;
    boolean zsoundDonePlaying;
    boolean bgsoundDonePlaying;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    boolean rocketRight;
    int rocketXSpeed;
    int rocketYSpeed;

//variables for missiles
//    int numMissiles;
//    int missileX[] =new int[numMissiles];
//    int missileY[] =new int[numMissiles];
//    int missileXSpeed[] =new int[numMissiles];
//    int missileYSpeed[] =new int[numMissiles];
//    boolean missileFired;
//    boolean missileRight[]=new boolean[numMissiles];
    
    Missile missiles[] =new Missile[Missile.numMissiles];
    
// variables for stars
    int numStars =10;
    int whichStar;
    boolean starVis[];
    int starXPos[];
    int starYPos[];
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();
                    

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) 
                {
                    rocketYSpeed++;
                } 
                else if (e.VK_DOWN == e.getKeyCode()) 
                {
                    rocketYSpeed--;
                } 
                else if (e.VK_LEFT == e.getKeyCode()) 
                {
                    rocketXSpeed--;
                } 
                else if (e.VK_RIGHT == e.getKeyCode()) 
                {
                    rocketXSpeed++;
                }
                else if (e.VK_INSERT == e.getKeyCode()) 
                {
                    zsound = new sound("ouch.wav");                    
                }
                else if(e.VK_SPACE == e.getKeyCode())
                {
                    for(int i=0; i<missiles.length; i++)
                    {
                        missiles[i].active =true;
                    }
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
        
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        
        for (int i =0; i<numStars;i++)
        {
            if (starVis[i])
            {
                g.setColor(Color.yellow);
                drawCircle(getX(starXPos[i]),getYNormal(starYPos[i]),0.0,1.0,1.0);
            }
        }
        
        
        
        for (int i =0;i<missiles.length;i++)
        {
          if(missiles[i].active)
              g.setColor(Color.red);
              g.fillRect(missiles[i].xpos,missiles[i].ypos,7,3);
        }
        
        
        double rocketXScale =1.0;
        if (rocketRight)
        {
            rocketXScale =-1.0;
        }
        g.setColor(Color.GREEN);
        g.fillRect(getX(rocketXPos-7), getYNormal(rocketYPos+7), 26, 13);
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,rocketXScale,1.0 );
        
        if(test)
        {
            g.setColor(Color.GREEN);
            g.fillRect(getWidth2()*3/4, getHeight2()*3/4, 20, 20);
        }

        
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        
        test =false;
        zsoundDonePlaying =false;
        
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketRight=false;
        rocketXSpeed =0;
        rocketYSpeed =0;
        
        Missile.current =0;
        missiles =new Missile[Missile.numMissiles];
        for (int i =0; i<missiles.length; i++)
        {
            missiles[i] =new Missile();
            missiles[i].xpos =rocketXPos;
            missiles[i].ypos =rocketYPos;
        }
        
        
        starVis =new boolean[numStars];
        starXPos =new int[numStars];
        starYPos =new int[numStars];
        
        for (int i =0; i<numStars;i++)
        {
            starVis[i] =true;
            starXPos[i] =getX((int) (Math.random()*getWidth2()));
            starYPos[i] =getYNormal((int) (Math.random()*getHeight2()));
        }
        whichStar =-1;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
        }
        for (int i =0; i<numStars;i++)
        {
            starXPos[i]-=rocketXSpeed;
            if (starXPos[i] <= -20)
            {
                starXPos[i] =getWidth2()+19;
            }
            if (starXPos[i] >= getWidth2()+20)
            {
                starYPos[i] =getYNormal((int) (Math.random()*getHeight2()));
                starXPos[i] =-19;
            }
        }
        
//        for (int i =0; i<numMissiles;i++)
//        {
//            if (missileFired)
//            {
//                missileX[i] =rocketXPos;
//                missileY[i] =rocketYPos;
//            }
//        }
        
        for (int i =0; i<missiles.length;i++)
        {
            if(missiles[i].active)
            {
                
            }
        }
        
        
        zsoundDonePlaying =false;
        boolean hit =false;
        for (int i=0;i<numStars;i++)
        {
            
            if (starXPos[i]-20 < rocketXPos && 
                starXPos[i]+20 > rocketXPos &&
                starYPos[i]-20 < rocketYPos &&
                starYPos[i]+20 > rocketYPos)
            {
//                test =true;
                
                    hit =true;
                    if (whichStar != i)
                    {
                        zsound = new sound("ouch.wav"); 
                        zsoundDonePlaying =true;
                    } 
                whichStar = i;
            }
        }
        if(!hit)
                whichStar =-1;

        if (rocketXSpeed <0)
            rocketRight =true;
            
        else if (rocketXSpeed >0)
            rocketRight =false;
        
        if (rocketXSpeed >=10)
        {
            rocketXSpeed =10;
        }
        if (rocketXSpeed <=-10)
        {
            rocketXSpeed =-10;
        }
        if (rocketYSpeed >=5)
        {
            rocketYSpeed =5;
        }
        if (rocketYSpeed <=-5)
        {
            rocketYSpeed =-5;
        }
        
        rocketYPos+=rocketYSpeed;
        
        if (rocketYPos >=getHeight2())
        {
            rocketYSpeed =0;
            rocketYPos =getHeight2();
        }
        
        if (rocketYPos <=0)
        {
            rocketYSpeed =0;
            rocketYPos =0;
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }

}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}


class Missile
{
    public static int current;
    public static final int numMissiles = 10;
    public int xpos;
    public int ypos;
    public boolean active;
    public boolean right;
    
    Missile()
    {
        xpos =0;
        ypos =0;
        right =true;
        active =false;
    }
}