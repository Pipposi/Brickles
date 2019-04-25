
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

/**
 *
 * @authors Aden Downey down0100, Daniel Wilson wils0496
 */

/* This class handles the initial configuration window where
    where the user specifies their game characteristics
*/
public class ConfigSelector extends JPanel {

    public static JCheckBox top = new JCheckBox();
    public static JCheckBox left = new JCheckBox();
    public static JCheckBox right = new JCheckBox();
    public static JCheckBox bottom = new JCheckBox();
    public static JSpinner js;
    public static JSpinner jls;
    int multi = 1;
    JRadioButton[] radioArr = new JRadioButton[3];

    public ConfigSelector() {
        this.setLayout(new GridLayout(1, 0));

        JPanel wallSelector = wallSelection();
        this.add(wallSelector);
        JPanel lifeSelector = lifeSelection();
        this.add(lifeSelector);
        JPanel rowSelector = rowSelection();
        this.add(rowSelector);
        JPanel dificultySelector = difficultySelection();
        this.add(dificultySelector);

        this.setMinimumSize(this.getPreferredSize());

    }

    private JPanel wallSelection() {
        //Defines the config JPanel that allows the user to select 
        //where there will be fixed walls for the ball to bounce off of
        JPanel selection = new JPanel(new GridBagLayout());
        selection.setBorder(new TitledBorder("WALL MODE: CLASSIC"));

        JLabel multiplier = new JLabel("Mulitplier: " + multi);
        addActionListeners(multiplier);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;

        // TOP
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        top.setSelected(true);
        selection.add(top, gbc);

        // LEFT
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        left.setSelected(true);
        selection.add(left, gbc);

        // RIGHT
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        gbc.gridy = 1;
        right.setSelected(true);
        selection.add(right, gbc);

        // BOTTOM
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        bottom.setSelected(false);
        selection.add(bottom, gbc);

        // CENTRE
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        gbc.gridy = 1;
        selection.add(multiplier, gbc);

        return selection;
    }

    private JPanel rowSelection() {
        // Defines the config JPanel that allows user to specify number of rows in game
        JPanel selection = new JPanel(new GridBagLayout());
        selection.setBorder(new TitledBorder("ROW SELECTION"));
        JLabel jl = new JLabel("Enter the number of rows");
        SpinnerModel sm = new SpinnerNumberModel(3, 1, 6, 1);
        js = new JSpinner(sm);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        selection.add(jl, gbc);

        gbc.gridy = 1;
        selection.add(js, gbc);

        return selection;
    }

    private JPanel lifeSelection() {
        //Defines the config JPanel that allows the user to specify the starting number of lives
        JPanel selection = new JPanel(new GridBagLayout());
        selection.setBorder(new TitledBorder("LIFE SELECTION"));
        JLabel jl = new JLabel("Enter the number of lives to start");
        SpinnerModel sm = new SpinnerNumberModel(3, 1, 5, 1);
        jls = new JSpinner(sm);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        selection.add(jl, gbc);

        gbc.gridy = 1;
        selection.add(jls, gbc);

        return selection;
    }

    private void addActionListeners(JLabel multiplier) {
        //Providews a multiplier based on configuration settings
        //Also provides brief description of wall mode difficulty
        ArrayList<JCheckBox> jArr = new ArrayList<>();
        jArr.add(top);
        jArr.add(bottom);
        jArr.add(left);
        jArr.add(right);
        for (JCheckBox b : jArr) {
            b.addActionListener((ActionEvent e) -> {
                if (b.isSelected()) {
                    multiplier.setText("Multiplier: " + --multi);
                } else {
                    multiplier.setText("Multiplier: " + ++multi);
                }
                JPanel p = (JPanel) top.getParent();
                if (!top.isSelected() && !bottom.isSelected() && left.isSelected() && right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: PONG"));
                } else if (!top.isSelected() && !bottom.isSelected() && !left.isSelected() && !right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: ALL-IN"));
                } else if (top.isSelected() && !bottom.isSelected() && left.isSelected() && right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: CLASSIC"));
                } else if (top.isSelected() && bottom.isSelected() && left.isSelected() && right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: BABY"));
                } else if (!top.isSelected() && bottom.isSelected() && !left.isSelected() && !right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: INVERSED"));
                } else if (!top.isSelected() && bottom.isSelected() && left.isSelected() && right.isSelected()) {
                    p.setBorder(new TitledBorder("WALL MODE: UPSIDE DOWN"));
                } else {
                    p.setBorder(new TitledBorder("WALL MODE: CUSTOM"));
                }
            });
        }
    }

    private JPanel difficultySelection() {
        //Defines the config JPanel that allows the user to select the overall difficulty of the game
        JPanel selection = new JPanel(new BorderLayout());
        selection.setBorder(new TitledBorder("DIFFICULTY SELECTION"));
        ButtonGroup group = new ButtonGroup();

        JRadioButton low = new JRadioButton("LOW");
        radioArr[0] = low;
        group.add(low);
        selection.add(low, BorderLayout.WEST);

        JRadioButton medium = new JRadioButton("MEDIUM");
        radioArr[1] = medium;
        group.add(medium);
        selection.add(medium, BorderLayout.CENTER);
        medium.setSelected(true);

        JRadioButton high = new JRadioButton("HIGH");
        radioArr[2] = high;
        group.add(high);
        selection.add(high, BorderLayout.EAST);

        return selection;
    }

    //The folowing methods provide a means to save the selected configuration
    public boolean[] getCheckboxes() {
        boolean[] arr = {top.isSelected(), bottom.isSelected(), right.isSelected(), left.isSelected()};
        return arr;
    }

    public int getSpinnerValue() {
        return (int) js.getValue();
    }

    public int getLifeSpinnerValue() {
        return (int) jls.getValue();
    }

    public int getDifficulty() {
        for (int i = 0; i < 3; i++) {
            if (radioArr[i].isSelected()) {
                return i;
            }
        }
        return 1;
    }

}
