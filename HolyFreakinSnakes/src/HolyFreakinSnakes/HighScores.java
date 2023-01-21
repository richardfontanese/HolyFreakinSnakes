/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HolyFreakinSnakes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author richard
 * 
 * HighScores reads and writes a CSV file to store a list of the top ten scores
 */
public class HighScores {

    String[][] scoresArray;
    String scoreString;
    int scorePosition;
    File highscores;

    //When the constructor is called, the file highscores.csv is read in
    public HighScores() throws FileNotFoundException {
        scoresArray = new String[10][2];
        highscores = new File("highscores.csv");
        Scanner scanScores = new Scanner(highscores);
        String line;
        String[] nameAndScore;
        int i = 0;

        //The high scores are saved to an array
        while (scanScores.hasNext()) {
            line = scanScores.next();
            nameAndScore = line.split(",");
            scoresArray[i][0] = nameAndScore[0];
            scoresArray[i][1] = nameAndScore[1];
            i++;
        }
    }

    //Builds a string that is a list of all high scores, used for the MarqueePanel
    public String buildString() {
        scoreString = "HIGH SCORES:          ";
        for (int i = 0; i < 10; i++) {
            scoreString = scoreString + "#" + (i + 1) + "  " + scoresArray[i][0] + "  " + scoresArray[i][1] + "          ";
        }
        return scoreString;
    }

    //Returns true if the input score is a high score, saves the position to scorePosition
    public Boolean checkForHighScore(int playerScore) {
        Boolean isHighScore = false;
        for (int i = 0; i < 10; i++) {
            if (playerScore >= Integer.parseInt(scoresArray[i][1])) {
                isHighScore = true;
                scorePosition = i;
                break;
            }
        }
        return isHighScore;
    }

    //prompts the user to input their name for high scores, does not accept a null input and removes all spaces or commas.  the correct position in the top 10 scores list is then found, and the file written.
    public void record(int playerScore) throws IOException {
        String playerName = null;
        while (playerName == null) {
            playerName = JOptionPane.showInputDialog("Congratulations! "
                    + "You've beaten the #" + (scorePosition + 1) + " high score! "
                    + "\nPlease enter your name:");
        }
        playerName = playerName.replaceAll("\\p{Z}", "");
        playerName = playerName.replaceAll(",", "");

        for (int i = 8; i >= scorePosition; i = i - 1) {
            scoresArray[i + 1][0] = scoresArray[i][0];
            scoresArray[i + 1][1] = scoresArray[i][1];
        }

        scoresArray[scorePosition][0] = playerName;
        String playerScoreString = "" + playerScore;
        scoresArray[scorePosition][1] = playerScoreString;

        try (PrintWriter writeScores = new PrintWriter(highscores)) {
            for (int i = 0; i < 10; i++) {
                writeScores.println(scoresArray[i][0] + "," + scoresArray[i][1]);
            }
        }
    }
}
