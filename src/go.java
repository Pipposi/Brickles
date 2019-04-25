
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * A starter class for a Director-pattern Swing application program.
 */
public class go {

    /**
     * Runs the program by creating a Director and passing it the command-line
     * arguments
     */
    public static void main(String args[]) {
        try {
            com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Green", "", "");
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        } catch (Exception e) {
            System.out.println(e);
        }
        JFrame window = new JFrame();
        Director director = new Director(window, args);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.pack();
        window.setVisible(true);
    }
}
