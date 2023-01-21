/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HolyFreakinSnakes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author richard fontanese
 *
 * This is the main menu and gameplay code for Holy Freakin' Snakes, today's
 * hottest new arcade game.
 */
public class HolyFreakinSnakes extends JFrame {

    JLayeredPane menu;
    HighScores scores;
    int width;
    int height;
    ImageIcon menuBG;
    GamePanel gamePlay;
    Font deathblood;

    //Constructor sets up the window, which is filled with one large image file on the splash screen
    public HolyFreakinSnakes() throws FileNotFoundException, IOException, FontFormatException {
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("crosshairs.png").getImage(), new Point(30, 30), "custom cursor"));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        width = screenSize.width;
        height = screenSize.height;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("deathblood.ttf")));
        deathblood = new Font("deathblood", Font.PLAIN, 65);

        scores = new HighScores();
        setTitle("HOLY FREAKIN' SNAKES!!!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setVisible(true);
        menuBG = new ImageIcon("splashscreen.jpg");
        buildMenu();
        revalidate();
    }

    public void buildMenu() throws IOException, FontFormatException {

        //build the menu pane and set its size
        menu = new JLayeredPane();
        menu.setSize(width, height);

        //create the start button, which is actually the entire background, then size and position it
        JButton startButton = new JButton(menuBG);
        startButton.setBounds(0, 0, width, height);
        startButton.addActionListener(new startButtonListener());
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        menu.add(startButton, 1, 0);

        //creates a MarqueePanel object, which scrolls the top 10 high scores across the screen
        String scoreString = scores.buildString();
        MarqueePanel scoresMarquee = new MarqueePanel(scoreString, width, 50);
        scoresMarquee.setBounds(0, 600, width, 50);
        scoresMarquee.setOpaque(false);
        scoresMarquee.setDoubleBuffered(true);
        menu.add(scoresMarquee, 2, 0);

        //set menu visible and add it to the main frame
        menu.setVisible(true);
        add(menu);
    }

//Listener class for start button, removes the splash screen and starts the gameplay screen
    private class startButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            remove(menu);
            gamePlay = new GamePanel();
            add(gamePlay);
        }
    }

    //main method calls the constructor to start the program
    public static void main(String[] args) throws FileNotFoundException, IOException, FontFormatException {
        new HolyFreakinSnakes();
    }

//Private class for game play
    public class GamePanel extends JLayeredPane {

        //spawnTime and biteTime control the behavior of vicious jungle snakes
        final int spawnTime = 900;
        final int moveTime = 50;
        Player currentPlayer;
        String currentStats;
        JLabel jungleLabel;
        JLabel statsLabel;
        int bgPosition;
        int jungleWidth;
        int jungleHeight;
        boolean gameOver;
        Timer spawnTimer;

        //constructor sets up the gameplay screen and starts a snake attack
        public GamePanel() {
            setSize(width, height);
            setLayout(null);
            setVisible(true);
            currentPlayer = new Player(scores);
            currentStats = "Health: " + currentPlayer.getHealth()
                    + " Snakes Killed: " + currentPlayer.getSnakeScore()
                    + " Skulls Killed: " + currentPlayer.getSkullScore();
            statsLabel = new JLabel(currentStats);
            statsLabel.setForeground(Color.red);
            statsLabel.setBackground(Color.WHITE);

            ImageIcon jungle = new ImageIcon("junglescroll.jpg");
            jungleLabel = new JLabel(jungle);
            add(jungleLabel, 1, 0);
            jungleWidth = jungle.getIconWidth();
            jungleHeight = jungle.getIconHeight();
            jungleLabel.setBounds(0, 0, jungleWidth, jungleHeight);
            bgPosition = 0;
            bgScrollListener bgScroller = new bgScrollListener();
            Timer bgTimer = new Timer(5, bgScroller);
            bgTimer.start();

            add(statsLabel, 3, 0);
            statsLabel.setBounds(10, 10, 500, 30);

            repaint();
            revalidate();
            new SnakeAttack();
        }

        //This is what happens when you die
        public void gameOverScreen() {
            gameOver = true;
            spawnTimer.stop();
            JOptionPane.showMessageDialog(null, "YOU HAVE SUCCUMBED"
                    + "\nHowever, you have managed to take down "
                    + currentPlayer.getSnakeScore() + " snakes and "
                    + currentPlayer.getSkullScore() + " skulls"
                    + "\n\n TOTAL SCORE: " + currentPlayer.getTotalScore());
            //score is checked against the top ten
            currentPlayer.checkHighScores();
            gamePlay.removeAll();
            remove(gamePlay);
            repaint();
            revalidate();
            try {
                buildMenu();
            } catch (IOException | FontFormatException ex) {
                Logger.getLogger(HolyFreakinSnakes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Listener that scrolls the background with every tick of the timer
        private class bgScrollListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                bgPosition = bgPosition - 15;
                jungleLabel.setBounds(bgPosition, 0, jungleWidth, jungleHeight);
                if (bgPosition == -2 * jungleWidth / 3) {
                    bgPosition = 0;
                }
                repaint();
                revalidate();
            }
        }

        //SnakeAttack class initiates a bombardment of snakes
        private class SnakeAttack {

            //constructor sets up a timer for creating new snakes
            public SnakeAttack() {
                CreateCreatureListener creatureListener = new CreateCreatureListener();
                spawnTimer = new Timer(spawnTime, creatureListener);
                spawnTimer.start();
                gameOver = false;
            }

            //Listener to create snakes
            private class CreateCreatureListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {

                    if (currentPlayer.getHealth() > 0) {
                        new Snake();
                        int createSkullRoll = ThreadLocalRandom.current().nextInt(0, 10);
                        if (currentPlayer.getSnakeScore() >= 20 && createSkullRoll == 5) {
                            new Skull();
                        }
                    }
                }
            }
        }
        

        public class Creature {

            JButton button;
            Boolean inactive = false;
            int X;
            int Y;
            int XSpeed;
            int YSpeed;
            int type = 0;
            int biteTime;
            Timer biteTimer;
            Timer movementTimer;
            int xBoundary;
            int yBoundary;
            int xWidth;
            int yHeight;
            ImageIcon snake = new ImageIcon("snake.png");
            ImageIcon snakebiteIcon = new ImageIcon("snakebite.png");
            ImageIcon deadSnake = new ImageIcon("deadsnake.gif");
            ImageIcon explosion = new ImageIcon("explosion.gif");
            ImageIcon skull = new ImageIcon("flamingskull.gif");
            KillListener killListen=new KillListener();
            MovementListener moveListen=new MovementListener();
            BiteListener biteListen=new BiteListener();

            public Creature() {
                
            }
            
            public void setIconBounds(){
                switch (type) {
                    case 0:
                        xWidth=snake.getIconWidth();
                        yHeight=snake.getIconWidth();
                        xBoundary = width - xWidth;
                        yBoundary = height - yHeight;
                        break;
                    case 1:
                        xWidth=skull.getIconWidth();
                        yHeight=skull.getIconHeight();
                        xBoundary = width - xWidth;
                        yBoundary = height - yHeight;
                        break;
                }
            }

            protected class MovementListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //Snake turns around if it hits the edge of the screen
                    if (X <= 0 || X >= xBoundary) {
                        XSpeed = -XSpeed;
                    }
                    if (Y <= 0 && inactive == false || Y >= yBoundary && inactive == false) {
                        YSpeed = -YSpeed;
                    }
                    //snake is removed if it goes off the bottom of the screen, this only happens if the snake is dead or has bitten
                    //in which case the boolean inactive is set to true
                    if (Y >= height) {
                        gamePlay.remove(button);
                    }
                    //X and Y position changed by adding XSpeed and YSpeed
                    X += XSpeed;
                    Y += YSpeed;
                    //Repaint the snake
                    button.setBounds(X, Y, xWidth, yHeight);
                    repaint();
                    revalidate();
                }
            }

            protected class BiteListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    inactive = true;
                    biteTimer.removeActionListener(this);
                    biteTimer.stop();

                    //if your health has not reached zero, you lose one health point and the game continues
                    if (currentPlayer.getHealth() > 0) {
                        switch (type) {
                            case 0:
                                currentPlayer.hit();
                                break;
                            case 1:
                                currentPlayer.skullHit();
                                break;
                        }
                        button.setEnabled(false);
                        XSpeed = 0;
                        YSpeed = 15;
                    }

                    currentStats = "Health: " + currentPlayer.getHealth()
                            + " Snakes Killed: " + currentPlayer.getSnakeScore()
                            + " Skulls Killed: " + currentPlayer.getSkullScore();
                    statsLabel.setText(currentStats);
                    statsLabel.repaint();
                    revalidate();

                    //if your health reaches zero, the game ends.
                    if (currentPlayer.getHealth() <= 0 && gameOver == false) {
                        gameOver = true;
                        gameOverScreen();
                    }
                }
            }

            protected class KillListener implements ActionListener {

                @Override
                public void actionPerformed(ActionEvent e) {
                    biteTimer.stop();
                    switch (type) {
                        case 0: {
                            button.setIcon(deadSnake);
                            if (inactive == false) {
                                currentPlayer.point();
                            }break;
                        }

                        case 1: {
                            button.setIcon(explosion);
                            if (inactive == false) {
                                currentPlayer.skullPoint();
                            }
                        }break;
                    }
                    inactive = true;
                    //updates the stats bar
                    currentStats = "Health: " + currentPlayer.getHealth()
                            + " Snakes Killed: " + currentPlayer.getSnakeScore()
                            + " Skulls Killed: " + currentPlayer.getSkullScore();
                    statsLabel.setText(currentStats);
                    statsLabel.repaint();
                    XSpeed = 0;
                    YSpeed = 15;
                }
            }
        }

        //snake class controls each individual snake
        public class Snake extends Creature {

            public Snake() {
                type = 0;
                setIconBounds();
                button = new JButton(snake);
                button.setDisabledIcon(snakebiteIcon);
                button.addActionListener(killListen);
                add(button, 2, 0);
                X = ThreadLocalRandom.current().nextInt(0, width - snake.getIconWidth());
                Y = ThreadLocalRandom.current().nextInt(0, height - snake.getIconHeight());
                XSpeed = ThreadLocalRandom.current().nextInt(-10, 10);
                YSpeed = ThreadLocalRandom.current().nextInt(-10, 10);
                button.setBounds(X, Y, snake.getIconWidth(), snake.getIconHeight());
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.repaint();
                revalidate();
                biteTime = ThreadLocalRandom.current().nextInt(1000, 5000);
                biteTimer = new Timer(biteTime, biteListen);
                movementTimer = new Timer(moveTime, moveListen);
                biteTimer.start();
                movementTimer.start();
            }
        }

        private class Skull extends Creature {

            KillListener skullKillListen = new KillListener();

            public Skull() {
                type = 1;
                setIconBounds();
                button = new JButton(skull);
                button.setDisabledIcon(skull);
                button.addActionListener(skullKillListen);
                add(button, 2, 0);
                X = ThreadLocalRandom.current().nextInt(0, width - skull.getIconWidth());
                Y = ThreadLocalRandom.current().nextInt(0, height - skull.getIconHeight());
                XSpeed = ThreadLocalRandom.current().nextInt(-10, 10);
                YSpeed = ThreadLocalRandom.current().nextInt(-10, 10);
                button.setBounds(X, Y, skull.getIconWidth(), skull.getIconHeight());
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.repaint();
                revalidate();
                biteTime = ThreadLocalRandom.current().nextInt(1000, 5000);
                biteTimer = new Timer(biteTime, biteListen);
                movementTimer = new Timer(moveTime, moveListen);
                biteTimer.start();
                movementTimer.start();
            }
        }
    }
}
