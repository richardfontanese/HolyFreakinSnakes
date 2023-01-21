/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HolyFreakinSnakes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;



/**
 *
 * @author richard fontanese
 *
 * The MarqueePanel creates a scrolling text effect, used to scroll the high
 * scores through the opening splash screen
 */
public class MarqueePanel extends JPanel implements ActionListener {

    JLabel marqueeText;
    Timer timer = new Timer(10, this);
    int position;
    int height;
    int width;
    String scores;
    int textwidth;

    public MarqueePanel(String scores, int width, int height) throws FontFormatException, IOException {

        //Loads the deathblood font, a scary, bloody looking font
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("deathblood.ttf")));

        //Sets up the panel size, a JLabel for text and the text color
        this.height = height;
        this.width = width;
        this.scores = scores;
        setSize(width, height);
        setLayout(null);
        marqueeText = new JLabel(scores);
        marqueeText.setForeground(Color.red);
        marqueeText.setBackground(Color.black);
        marqueeText.setOpaque(true);
        
        //set font and calculate the width of JLabel marqueeText in order to set bounds so that text is not truncated
        Font deathblood;
        deathblood = new Font("deathblood", Font.PLAIN, 45);
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        textwidth = (int) (deathblood.getStringBounds(scores, frc).getWidth());
        
        //Set the initial position of the text, start the timer for animation
        marqueeText.setFont(deathblood);
        marqueeText.setDoubleBuffered(true);
        position = width;
        marqueeText.setBounds(position, 10, textwidth, 40);
        add(marqueeText);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        //every time the timer goes off, the text moves one pixel to the left
        position = position - 1;
        marqueeText.setBounds(position, 10, textwidth, 30);
        revalidate();

        //if the text gets all the way off the left side of the screen, it starts over at the original position
        if (position == -(textwidth)) {
            position = width;
            marqueeText.setBounds(position, 10, textwidth, 30);
        }
    }
}
