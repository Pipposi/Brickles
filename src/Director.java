
import com.sun.glass.events.KeyEvent;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @authors Aden Downey down0100, Daniel Wilson wils0496
 * 
 * A Director for a program to play Brickles.
 *
 * <p>
 * The program uses the {@link BrickWall BrickWall} component and implements the
 * {@link CollisionListener CollisionListener} interface to respond to
 * {@link CollisionEvent CollisionEvents}.</p>
 *
 * @see BrickWall
 * @see CollisionEvent
 * @see CollisionListener
 *
 */
public class Director extends JPanel implements CollisionListener, ActionListener {
    
    //Variable Declaration
    private BrickWall wall = new BrickWall();
    private int numberBricks;
    private int bricksHit;
    private ColourTheme theme = new ColourTheme(ColourTheme.themes.original);
    private ColourTheme themeToChange = theme;

    private boolean wallBools[] = {true, false, true, true};
    int numWalls = 3;
    int difficulty = 1;

    private Timer powerUpTimer;
    private int timerIterations = 0;

    int rowStart = 5;
    int numRows = 3;
    int increaser = 1;

    JPanel lifePanel;
    JPanel scorePanel;
    JLabel scoreLabel = new JLabel();
    int currentScore = 0;
    int multiplier = 1;
    ArrayList<JButton> livesArray = new ArrayList<>();
    private int maxLives = 4;

    private ArrayList<HighScore> highScores = new ArrayList<>();

    JDialog selectionContainer;
    boolean okToContinue = true;

    /**
     * Constructs a Director to run the program.
     *
     * @param window the main window for the program
     * @param args command line arguments (currently unused)
     */
    public Director(JFrame window, String[] args) {
        //Constructs the game panel
        JMenuBar menus = new JMenuBar();
        window.setJMenuBar(menus);
        menus.add(makeMenu());

        initSelectionContainer();

        scorePanel = new JPanel();
        scorePanel.add(scoreLabel);
        currentScore = 0;
        setScoreLabel();
        readHighScores();

        lifePanel = new JPanel();

        JPanel game = new JPanel();
        game.add(makeGamePanel(), BorderLayout.CENTER);

        setWindowLayout(window, game);

        powerUpTimer = new Timer(100, (ActionEvent e) -> {
            //Handles user power-up for a set time limit
            if (timerIterations >= 80) {
                Color current = wall.getWallColor();
                if (current != Color.WHITE) {
                    recolour(Color.WHITE);
                } else {
                    recolour(Color.CYAN);
                }
            }
            if (timerIterations++ == 100) {
                revertPowerUp();
                powerUpTimer.stop();
            }
        });
    }

    private void setWindowLayout(JFrame window, JPanel game) {
        //Constructs game panel to show game board, lives, and score to user 
        window.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        window.getContentPane().add(scorePanel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        window.getContentPane().add(lifePanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        game.setLayout(new GridLayout(1, 1));

        window.add(game, gbc);
        window.setMinimumSize(window.getPreferredSize());

    }

    private void setScoreLabel() {
        //Displays current score to user in game panel
        scoreLabel.setText("Bricks Cleared: " + bricksHit + "/" + numberBricks
                + " | Score: " + String.valueOf(currentScore * multiplier));
    }

    private void clearLives() {
        //Clears lives form the array
        livesArray.removeAll(livesArray);
        lifePanel.removeAll();

    }

    private void initLives() {
        clearLives();
        for (int i = 0; i < this.maxLives; i++) {
            addLife();
        }
        lifePanel.validate();
    }

    private void initLives(int numLives) {
        clearLives();
        for (int i = 0; i < numLives; i++) {
            addLife();
        }
        lifePanel.validate();
    }

    private void addLife() {
        //Adds lives shown to user above game panel
        JButton jb = new JButton("❤");
        jb.setForeground(Color.red);
        lifePanel.add(jb);
        livesArray.add(jb);
        lifePanel.validate();
    }

    private void colourBricks(ColourTheme theme) {
        //Colours the bricks based on theme selection
        //Also changes the color of bricks based on whether they are multiple-hit bricks
        Color startColour = theme.getRowOne();
        switch (difficulty) {
            case 0: // EASY
                startColour = theme.getRowThree();
                break;
            case 1: // MED
                startColour = theme.getRowTwo();
                break;
            case 2: // HARD
                startColour = theme.getRowOne();
                break;
        }
        for (int i = rowStart; i < rowStart + numRows; i++) {
            for (int j = 0; j < wall.getColumns() + 1; j++) {
                wall.setBrick(i, j, startColour);
            }
            switch (i % rowStart) {
                case 0:
                    startColour = theme.getRowTwo();
                    if (difficulty < 2) {

                    } else {
                        break;
                    }
                case 1:
                    startColour = theme.getRowThree();
                    break;
                default:
                    startColour = theme.getRowThree();
                    break;
            }
        }
        Random ran = new Random();
        wall.setBrick(ran.nextInt(numRows) + rowStart, ran.nextInt(wall.getColumns()), theme.getPowerOne());
        wall.setBrick(ran.nextInt(numRows) + rowStart, ran.nextInt(wall.getColumns()), theme.getPowerTwo());
        wall.setBrick(ran.nextInt(numRows) + rowStart, ran.nextInt(wall.getColumns()), theme.getPowerThree());
        wall.setBrick(ran.nextInt(numRows) + rowStart, ran.nextInt(wall.getColumns()), theme.getPowerDown());

        numberBricks = numRows * wall.getColumns();
        setScoreLabel();
    }

    private void clearBricks() {
        //Clears the bricks on the game board
        for (int i = rowStart; i < rowStart + numRows; i++) {
            for (int j = 0; j <= wall.getColumns(); j++) {
                wall.setBrick(i, j, null);
            }
        }
        wall.repaint();
    }

    /**
     * Handler for {@link CollisionEvent CollisionEvents}.
     *
     * @param e the event
     */
    @Override
    public void collisionDetected(CollisionEvent e) {
        int collision = e.getTarget();
        switch (collision) {
            //Plays custom sound when brick is hit. Allpies power-ups or power-downs based on which brick is hit. 
            //Handles scores for hititng specific bricks
            case CollisionEvent.BRICK:
                makeNoise();
                Color brickColor = wall.getBrick(e.getRow(), e.getColumn());            
                wall.setBrick(e.getRow(), e.getColumn(), null);                         
                bricksHit++;
                if (brickColor == theme.getRowOne()) {
                    wall.setBrick(e.getRow(), e.getColumn(), theme.getRowTwo());
                    bricksHit--;
                    currentScore += 5;
                } else if (brickColor == theme.getRowTwo()) {
                    wall.setBrick(e.getRow(), e.getColumn(), theme.getRowThree());
                    bricksHit--;
                    currentScore += 3;
                } else if (brickColor == theme.getRowThree()) {
                    currentScore += 10;
                } else if (brickColor == theme.getPowerOne()) {
                    startPowerUpTimer("powerUpOne");
                } else if (brickColor == theme.getPowerTwo()) {
                    startPowerUpTimer("powerUpTwo");
                } else if (brickColor == theme.getPowerThree()) {
                    startPowerUpTimer("powerUpThree");
                } else if (brickColor == theme.getPowerDown()) {
                    powerDown();
                }
                setScoreLabel();

                if (numberBricks == bricksHit) {
                    //Resets the gameboard if user successfully clears all bricks
                    JOptionPane.showMessageDialog(this, "LEVEL UP");
                    resetBoard(true);
                }
                break;

            case CollisionEvent.WALL:
                break;

            case CollisionEvent.MISS:
                //Removes a life for missing the ball with the paddle
                try {
                    JButton b = livesArray.remove(livesArray.size() - 1);
                    lifePanel.remove(b);
                    lifePanel.repaint();
                    lifePanel.revalidate();
                    if (livesArray.isEmpty()) {
                        throw new IndexOutOfBoundsException();
                    }
                } catch (IndexOutOfBoundsException ex) {
                    //Game Over dialog box, also prompt user to save high score if relevant
                    JOptionPane.showMessageDialog(this, "GAME OVER\nSCORE: " + String.valueOf(currentScore * multiplier));
                    if (isHighScore()) {
                        String name = JOptionPane.showInputDialog("High score! Please enter your name");
                        if (name == null || name.equals("")) {
                            name = "4CHINZ!!1!!!!11!1";
                        }
                        highScores.add(new HighScore(name, currentScore * multiplier));
                        Collections.sort(highScores);
                        writeHighScores();
                    }
                    resetBoard(false);
                }
                break;

            case CollisionEvent.BAT:
                break;
        }
    }

    private JComponent makeGamePanel() {
        //Build the game panel using the BrickWall object
        wall = new BrickWall();
        wall.setBackground(new Color(43, 130, 126));
        wall.addCollisionListener(this);
        wall.setReportMask(CollisionEvent.BRICK | CollisionEvent.MISS);
        resetBoard(false);
        this.repaint();
        return wall;
    }

    private void resetBoard(boolean continuing) {
        //Resets the game board to user's default config settings
        theme = themeToChange;
        bricksHit = 0;
        wall.resetBall();
        if (!continuing) {
            showWallSelectDialog();
            currentScore = 0;
            setScoreLabel();
            wall.setBallSpeed(new BrickWall().getBallSpeed());
            wall.setBatSize(new BrickWall().getBatSize());

            revertPowerUp();
        } else {
            if (livesArray.size() < maxLives) {
                addLife();
            }
            powerDown();
            colourBricks(theme);
        }
        wall.setBallSmoothness(3);
        wall.setBallSpeed(6);
    }

    private JMenu makeMenu() {
        //Constructs the File menu for the game panel and provides the contained options
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        JMenu themes = new JMenu("Themes");
        for (ColourTheme.themes c : ColourTheme.themes.values()) {
            JMenuItem jmi = new JMenuItem(c.toString());
            themes.add(jmi);
            jmi.addActionListener(this);
        }

        menu.add(themes);
        menu.addSeparator();

        JMenuItem hs = new JMenuItem("High Scores");
        hs.setMnemonic(KeyEvent.VK_H);
        hs.addActionListener(this);
        menu.add(hs);

        JMenuItem hsr = new JMenuItem("Reset High Scores");
        hsr.setMnemonic(KeyEvent.VK_H);
        hsr.addActionListener(this);
        menu.add(hsr);
        menu.addSeparator();

        JMenuItem save = new JMenuItem("Save");
        save.setMnemonic(KeyEvent.VK_S);
        save.addActionListener(this);
        menu.add(save);

        JMenuItem load = new JMenuItem("Load");
        load.setMnemonic(KeyEvent.VK_L);
        load.addActionListener(this);
        menu.add(load);
        menu.addSeparator();

        JMenuItem reset = new JMenuItem("Reset");
        reset.setMnemonic(KeyEvent.VK_R);
        reset.addActionListener(this);
        menu.add(reset);
        menu.addSeparator();

        JMenuItem quit = new JMenuItem("Quit");
        quit.setMnemonic(KeyEvent.VK_Q);
        quit.addActionListener(this);
        menu.add(quit);

        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Processes the user's selections form the File menu
        String command = e.getActionCommand();
        String[] yesNoOpt = {"YES", "NO"};
        String themes = "";
        for (ColourTheme.themes c : ColourTheme.themes.values()) {
            themes += c.toString();
        }
        //Checks user confirmation to quit or reset, and prompts to save
        if (command.equals("Quit")) {
            int i = JOptionPane.showOptionDialog(this, "Are you sure you want to quit?", "Quit Game", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, yesNoOpt, 0);
            if (i == 0) {
                int j = JOptionPane.showOptionDialog(this, "Do you want to save your current state?", "Save Game", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, yesNoOpt, 0);
                if (j == 0) {
                    writeState();
                }
                System.exit(0);
            }
        } else if (command.equals("Reset")) {
            int i = JOptionPane.showOptionDialog(this, "Are you sure you want to reset?", "Reset Game", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, yesNoOpt, 0);
            if (i == 0) {
                int j = JOptionPane.showOptionDialog(this, "Do you want to save your current state?", "Save Game", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, yesNoOpt, 0);
                if (j == 0) {
                    writeState();
                }
                clearBricks();
                resetBoard(false);
            }
        } else if (command.equals("High Scores")) {
            showScoreBoard();
        } else if (command.equals("Reset High Scores")) {
            resetScoreBoard();
        } else if (command.equals("Save")) {
            writeState();
        } else if (command.equals("Load")) {
            try {
                readState();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), "Uh-oh. Something went wrong.");
                resetBoard(false);
            }
        } else if (themes.contains(command)) {
            JOptionPane.showMessageDialog(this, "Theme will be changed when board resets.");
            this.themeToChange = new ColourTheme(ColourTheme.themes.valueOf(command));
        }
    }

    private void startPowerUpTimer(String brickColor) {
        //Starts a power up timer to give a user a power-up based on whether the user hits a power-up brick
        if (powerUpTimer.isRunning()) {
            revertPowerUp();
            powerUpTimer.stop();
        }
        switch (brickColor) {
            case "powerUpOne":
                wall.setBatSize(40);
                recolour(theme.getPowerOne());
                break;
            case "powerUpTwo":
                wall.setWalls(true, true, true, true);
                recolour(theme.getPowerTwo());
                break;
            case "powerUpThree":
                wall.setBallSize(30);
                recolour(theme.getPowerThree());
                break;
            case "powerDown":
                wall.setWalls(false, false, false, false);
                break;
        }
        powerUpTimer.start();
    }


    private void revertPowerUp() {
        //Reverts user to default status after a power up timer has expired
        setWallToDefaults();
        wall.setBatSize(new BrickWall().getBatSize());
        wall.setBallSize(new BrickWall().getBallSize());
        recolour(theme.getWallBallBat());

        timerIterations = 0;
    }

    private void powerDown() {
        //Sets different power-downs based on whether the user hits a power-down brick
        Random ran = new Random();
        switch (ran.nextInt(3)) {
            case 0:
                wall.setBatSize(wall.getBatSize() - 5);
                JOptionPane.showMessageDialog(this, "BAT DECREASE!", "BAT DECREASE", JOptionPane.WARNING_MESSAGE);
                break;
            case 1:
                wall.setBallSpeed(wall.getBallSpeed() + 1);
                JOptionPane.showMessageDialog(this, "SPEED UP!", "SPEED UP", JOptionPane.WARNING_MESSAGE);
                break;
            case 2:
                startPowerUpTimer("powerDown");
                break;
        }
        wall.resetBall();
    }

    private void recolour(Color colour) {
        //recolours the game components based on theme selection
        wall.setBatColor(colour);
        wall.setWallColor(colour);
        wall.setBallColor(colour);
    }

    private void showWallSelectDialog() {
        selectionContainer.setVisible(true);
    }

    private void calcMultiplier() {
        //Calculates multiplier based on wall config selection
        int multi = 4;
        for (boolean b : wallBools) {
            if (b) {
                multi--;
            }
        }
        multiplier = multi;
    }

    private void initSelectionContainer() {
        //Handles the game initialization when the user selects "START" in the config panel
        ConfigSelector wallSelectionPanel = new ConfigSelector();
        JButton confirmButton = new JButton("START");
        confirmButton.setBackground(Color.GREEN);
        confirmButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                SwingUtilities.windowForComponent(wallSelectionPanel).dispose();
                okToContinue = false;
            }
        });
        wallSelectionPanel.add(confirmButton);

        selectionContainer = new JDialog(new JFrame(), "Initialise the game", true);

        //Creates the File Menu for config window with all contained options 
        JMenuBar menuBar = new JMenuBar();
        JMenu selectionContainerFile = new JMenu("File");
        JMenuItem hs = new JMenuItem("High Scores");
        selectionContainerFile.add(hs);
        hs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScoreBoard();
            }
        });

        JMenuItem hsr = new JMenuItem("Reset High Scores");
        selectionContainerFile.add(hsr);
        hsr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetScoreBoard();
            }
        });
        selectionContainerFile.addSeparator();

        JMenuItem load = new JMenuItem("Load");
        selectionContainerFile.add(load);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionContainer.dispose();
                try {
                    readState();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(new JFrame(), "Uh-oh. Something went wrong.");
                    resetBoard(false);
                }
            }
        });

        menuBar.add(selectionContainerFile);
        selectionContainer.setJMenuBar(menuBar);

        selectionContainer.setContentPane(wallSelectionPanel);
        selectionContainer.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                //System.exit(0);
                windowClosed(e);
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                int i = 0;
                for (boolean b : wallSelectionPanel.getCheckboxes()) {
                    wallBools[i++] = b;
                }
                setWallToDefaults();
                calcMultiplier();

                clearBricks();
                setMaxLives(wallSelectionPanel.getLifeSpinnerValue());
                changeNumRows(wallSelectionPanel.getSpinnerValue());
                setDifficulty(wallSelectionPanel.getDifficulty());
                colourBricks(theme);
            }
        });
        selectionContainer.pack();                          // Pack containers first,
        selectionContainer.setLocationRelativeTo(null);     // Then set location relative to null
        selectionContainer.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

    }

    private void makeNoise() {
        //Starts the music player to make the custom sounds of the game
        new MusicPlayer().start();
    }

    private void setMaxLives(int lives) {
        //Sets the maximum number of lives as per the users defined configuration
        this.maxLives = lives;
        initLives();
    }

    private void changeNumRows(int numRows) {
        //Sets the number of rows based on the users defined configuration
        this.numRows = numRows;
    }

    private void writeState() {
        //Writes the current state of the game to a specified file for later retrieval 
        try {
            String fileName = JOptionPane.showInputDialog("Please enter a filename to write to");
            File file = new File(fileName);
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);

            System.out.println("Writing to: " + fileName);
            writer.write(theme.toString() + "\n");
            writer.write(currentScore + "\n");
            writer.write(livesArray.size() + "\n");
            for (int i = 0; i < wallBools.length; i++) {
                writer.write(wallBools[i] + "$");
            }
            writer.write("\n" + multiplier + "\n" + numRows + "\n");
            for (int i = rowStart; i < rowStart + numRows; i++) {
                for (int j = 1; j <= wall.getColumns(); j++) {

                    Color brick = wall.getBrick(i, j);
                    if (theme.isPrimaryBrick(brick)) {
                        writer.write(theme.getBrickLevelString(brick));
                    } else if (theme.isPowerBrick(brick)) {
                        writer.write(theme.getPowerUpString(brick));
                    } else {
                        writer.write("NA");
                    }

                    writer.write("$");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {

        }
    }

    private void readState() throws Exception {
        //Reads a previously saved game state from a specified file and presents this saved game to the user
        FileReader fr = null;
        try {
            String fileName = JOptionPane.showInputDialog("Please enter a filename to load");
            File file = new File(fileName);
            fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);

            this.theme = new ColourTheme(ColourTheme.themes.valueOf(reader.readLine()));
            this.currentScore = Integer.valueOf(reader.readLine());
            setScoreLabel();

            initLives(Integer.valueOf(reader.readLine()));

            String walls = reader.readLine();
            int index = 0;
            for (String b : walls.split("[$]")) {
                this.wallBools[index++] = Boolean.valueOf(b);
            }
            setWallToDefaults();
            clearBricks();
            this.multiplier = Integer.valueOf(reader.readLine());
            this.numRows = Integer.valueOf(reader.readLine());
            numberBricks = this.numRows * 12;
            bricksHit = 0;
            for (int i = 0; i < numRows; i++) {
                String[] bricks = reader.readLine().split("[$]");
                int count = 1;
                for (String s : bricks) {
                    paintIndivBrick(s, i, count++);
                }
            }
            setScoreLabel();
            wall.repaint();
        } catch (Exception ex) {
            throw new Exception("Error reading from file");
        } finally {
            try {
                fr.close();
            } catch (Exception ex) {

            }
        }

    }

    private void paintIndivBrick(String level, int row, int column) {
        switch (level) {
            //Handles the creation of power-up bricks, and creates the multiple-hit bricks for higher difficulty levels
            case "R1":
                wall.setBrick(row + rowStart, column, theme.getRowOne());
                break;
            case "R2":
                wall.setBrick(row + rowStart, column, theme.getRowTwo());
                break;
            case "R3":
                wall.setBrick(row + rowStart, column, theme.getRowThree());
                break;
            case "P1":
                wall.setBrick(row + rowStart, column, theme.getPowerOne());
                break;
            case "P2":
                wall.setBrick(row + rowStart, column, theme.getPowerTwo());
                break;
            case "P3":
                wall.setBrick(row + rowStart, column, theme.getPowerThree());
                break;
            case "PD":
                wall.setBrick(row + rowStart, column, theme.getPowerDown());
                break;
            default:
                bricksHit++;
                wall.setBrick(row + rowStart, column, null);
        }

    }

    private void setWallToDefaults() {
        //Returns the walls of the game to their default selections
        wall.setWalls(wallBools[0], wallBools[1], wallBools[2], wallBools[3]);
    }

    private void setDifficulty(int difficulty) {
        //Changes the difficulty of the game
        this.difficulty = difficulty;
    }

    private boolean isHighScore() {
        //Checks last entry in high score list to see if new score is a high score
        try {
            if (currentScore > highScores.get(4).getScore()) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    private void showScoreBoard() {
        //Displays the contents of the High scores list in a JOptionPane
        String scores = "";
        int count = 0;
        for (HighScore s : highScores) {
            scores += s + "\n";
            if (count++ == 4) {
                break;
            }
        }
        JOptionPane.showMessageDialog(this, scores, "SCOREBOARD", JOptionPane.INFORMATION_MESSAGE);

    }

    private void writeHighScores() {
        //Saves current high scores to a text file for later use
        File file = new File("highScores.txt");
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 0; i < 5 && i < highScores.size(); i++) {
                HighScore hs = highScores.get(i);
                writer.write(hs.getName() + "$" + hs.getScore() + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Director.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readHighScores() {
        //Reads High Scores from the saved score txt file  and adds them to the scoreboard
        FileReader fr = null;
        try {
            String fileName = "highScores.txt";
            File file = new File(fileName);
            fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            for (int i = 0; i < 5; i++) {
                String[] line = reader.readLine().split("[$]");
                highScores.add(new HighScore(line[0], Integer.valueOf(line[1])));
            }
        } catch (Exception e) {

        }
    }

    private void resetScoreBoard() {
        //Allows user to reset the High Scores 
        highScores.clear();
        writeHighScores();
    }
}
