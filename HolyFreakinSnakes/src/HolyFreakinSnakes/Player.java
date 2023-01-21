/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HolyFreakinSnakes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author richard fontanese
 *
 * The Player class handles health and scoring
 */
public class Player {

    int health;
    int snakeScore;
    int skullScore;
    int totalScore;
    HighScores highScores;

    //constructor sets up a new player
    public Player(HighScores h) {
        health = 50;
        snakeScore = 0;
        skullScore = 0;
        totalScore = 0;
        highScores = h;
    }

    //if you are bitten you lose health
    public void hit() {
        health--;
    }

    public void skullHit() {
        health = health - 10;
    }

    //returns your current health
    public int getHealth() {
        return health;
    }

    //awards a point
    public void point() {
        snakeScore++;
        calcTotalScore();
    }

    public void skullPoint() {
        skullScore++;
        calcTotalScore();
    }

    public void calcTotalScore() {
        totalScore = snakeScore + (2 * skullScore);
    }

    //returns your current snakeScore
    public int getSnakeScore() {
        return snakeScore;
    }

    public int getSkullScore() {
        return skullScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    //checks your current snakeScore against the list of high scores.  If you have beaten a high snakeScore, yours is recorded in its place
    public void checkHighScores() {
        if (highScores.checkForHighScore(totalScore) == true) {
            try {
                highScores.record(totalScore);
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
