
/**
 *
 * @authors Aden Downey down0100, Daniel Wilson wils0496
 */

/* Class for handling the playing of custom music effects 
while the game is in progress
*/

import java.io.*;
import javazoom.jl.player.*;

public class MusicPlayer extends Thread {

    Player player;

    public static void main(String[] args) {
        try {
            File file = new File("Everlong 8bit.mp3");
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {

                Player player = new Player(bis);
                player.play();

            } catch (Exception e) {
                System.err.println(e);
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public void run() {

        try {
            File file = new File("Everlong 8bit.mp3");
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {

                player = new Player(bis);
                player.play();

            } catch (Exception e) {
                System.err.println(e);
            }

        } catch (Exception e) {
        }

    }

    public void stopMusic() {
    }

}
