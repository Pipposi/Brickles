
import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @authors Aden Downey down0100, Daniel Wilson wils0496
 */
public class HighScore implements Comparable {
    //Class for handling the High Score functionality
    //Arraylist stores Highscore objects for retrieval later
    
    private final String name;
    private final int score;

    public static void main(String[] args) {
        HighScore h1 = new HighScore("Aden", 25);
        HighScore h2 = new HighScore("Dan", 30);
        HighScore h3 = new HighScore("Tom", 999999999);
        
        ArrayList<HighScore> hs = new ArrayList<>();
        hs.add(h1);
        hs.add(h2);
        hs.add(h3);
        
        for (int i = 0; i < 4; i++) {
            System.out.println(hs.get(i));
        }
        Collections.sort(hs);
        for (int i = 0; i < 3; i++) {
            System.out.println(hs.get(i));
        }
    }
    
    HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        HighScore h2 = (HighScore) o;
        return h2.getScore() - this.score;
    }

    public String getName() {
        return this.name;
    }
    
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return this.name + ": " + String.valueOf(score);
    }

}
