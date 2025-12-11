import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Entry point utama untuk game Brick Blast.
 * Membuat dan menampilkan jendela game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            GamePanel gamePanel = new GamePanel();

            frame.setTitle("Brick Blast - Java Edition");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Set ukuran yang diinginkan untuk panel game
            gamePanel.setPreferredSize(new Dimension(700, 600));
            frame.add(gamePanel);
            frame.pack();

            // Posisikan jendela di tengah layar
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - frame.getWidth()) / 2;
            int y = (screenSize.height - frame.getHeight()) / 2;
            frame.setLocation(x, y);

            frame.setVisible(true);
        });
    }
}
