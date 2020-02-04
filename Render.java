package Game;

import java.awt.Graphics;
import javax.swing.JPanel;

public class Render extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        flappyBirdGame.flappyBird.repaint(g);

    }
}