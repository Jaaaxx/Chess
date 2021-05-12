import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class App extends JPanel {
    Display display;
    static BufferedImage[] images = new BufferedImage[12];

    Color wBrown = new Color(146,99,74);
    Color dBrown = new Color(251,233,199);

    /* -1: NULL
   0: KING
   1: QUEEN
   3: PAWN
   4: KNIGHT
   5: CASTLE
   6: BISHOP */
    int[][] board =
        {{4, 3, 5, 0, 1, 5, 3, 4},
         {2, 2, 2, 2, 2, 2, 2, 2},
         {-1, -1, -1, -1, -1, -1, -1, -1},
         {-1, -1, -1, -1, -1, -1, -1, -1},
         {-1, -1, -1, -1, -1, -1, -1, -1},
         {-1, -1, -1, -1, -1, -1, -1, -1},
         {2, 2, 2, 2, 2, 2, 2, 2},
         {4, 3, 5, 0, 1, 5, 3, 4}};

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = (display.screenSize.width / 8);

        // Draw board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                g.setColor(j % 2 == 0 ? (i % 2 == 0 ? wBrown : dBrown) : (i % 2 == 0 ? Color.BLACK : Color.WHITE));
                g.fillRect(i * w, j * w, w, w);
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] >= 0) {
                    BufferedImage im = images[(board[i][j] * 2) + (i < 4 ? 0 : 1)];
                    g.drawImage(im, j * w, i * w, null);
                }
            }
        }
    }

    public App() {
        display = new Display(this);
        start();
    }

    public void start() {
        System.out.println("Game Started");
    }

    public static void main(String[] args) {
        int c = -1;
        for (int i = 0; i < 12; i++) {
            if (i % 2 == 0)
                c++;
            BufferedImage img;
            try {
                if (i % 2 == 0)
                    img = ImageIO.read(new File("src/img/" + c + "White.png"));
                else
                    img = ImageIO.read(new File("src/img/" + c + "Black.png"));
                images[i] = img;
            } catch (IOException ignored) {}
        }
        new App();
    }
}

class Display extends JFrame {
    Dimension screenSize;

    public Display(App app) {
        super("Chess");
        screenSize = new Dimension(640, 640);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        app.setPreferredSize(screenSize);

        setLocationRelativeTo(null);
        add(app);
        pack();
        setVisible(true);
    }
}