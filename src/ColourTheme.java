
import java.awt.Color;

/**
 *
 * @authors Aden Downey down0100, Daniel Wilson wils0496
 */

/*  This class handles the selection of themes by assigning 
    individual rows specific colors based on theme choice
    as well as the power-up bricks. Also increases difficulty 
    by assigning bricks that require multiple hits.
*/
public class ColourTheme {

    public enum themes {

        original, grayscale, peppermint, rhineland
    }

    private Color wallBallBat;
    
    private Color rowOne;
    private Color rowTwo;
    private Color rowThree;

    private Color powerOne;
    private Color powerTwo;
    private Color powerThree;
    private Color powerDown;

    private ColourTheme.themes theme;

    public ColourTheme(ColourTheme.themes theme) {
        setTheme(theme);
    }

    private void setTheme(themes theme) {
        this.theme = theme;
        switch (theme) {
            case grayscale:
                wallBallBat = Color.BLACK;
                rowOne = Color.BLACK;
                rowTwo = Color.GRAY;
                rowThree = Color.WHITE;
                powerOne = Color.BLUE;
                powerTwo = Color.PINK;
                powerThree = Color.MAGENTA;
                powerDown = Color.YELLOW;
                break;
            case original:
                wallBallBat = Color.WHITE;
                rowOne = Color.RED;
                rowTwo = Color.ORANGE;
                rowThree = Color.GREEN;
                powerOne = Color.BLUE;
                powerTwo = new Color(255, 98, 0);
                powerThree = Color.MAGENTA;
                powerDown = Color.BLACK;
                break;
            case peppermint:
                wallBallBat = Color.RED;
                rowOne = Color.RED;
                rowTwo = Color.PINK;
                rowThree = Color.WHITE;
                powerOne = Color.BLUE;
                powerTwo = Color.GREEN;
                powerThree = Color.MAGENTA;
                powerDown = Color.BLACK;
                break;
            case rhineland:
                wallBallBat = Color.YELLOW;
                rowOne = Color.RED;
                rowTwo = Color.BLACK;
                rowThree = Color.YELLOW;
                powerOne = Color.BLUE;
                powerTwo = Color.GREEN;
                powerThree = Color.MAGENTA;
                powerDown = Color.BLACK;
                break;
        }
    }

    public Color getColourFromRow(int row) {
        switch (row) {
            case 0:
                return getRowOne();
            case 1:
                return getRowTwo();
            default:
                return getRowThree();
        }
    }

    public Color getWallBallBat() {
        return wallBallBat;
    }
    
    public Color getRowOne() {
        return rowOne;
    }

    public Color getRowTwo() {
        return rowTwo;
    }

    public Color getRowThree() {
        return rowThree;
    }

    public Color getPowerOne() {
        return powerOne;
    }

    public Color getPowerTwo() {
        return powerTwo;
    }

    public Color getPowerThree() {
        return powerThree;
    }

    public Color getPowerDown() {
        return powerDown;
    }

    @Override
    public String toString() {
        return theme.name();
    }

    boolean isPrimaryBrick(Color brick) {
        return brick == getRowOne() || brick == getRowTwo()
                || brick == getRowThree();
    }

    boolean isPowerBrick(Color brick) {
        return brick == getPowerOne() || brick == getPowerTwo()
                || brick == getPowerThree() || brick == getPowerDown();
    }

    String getBrickLevelString(Color brick) {
        if (brick == getRowOne()) {
            return "R1";
        } else if (brick == getRowTwo()) {
            return "R2";
        } else {
            return "R3";
        } 
    }

    String getPowerUpString(Color brick) {
        if (brick == getPowerOne()) {
            return "P1";
        } else if (brick == getPowerTwo()) {
            return "P2";
        } else if (brick == getPowerThree()) {
            return "P3";
        } else {
            return "PD";
        }
    }

}
