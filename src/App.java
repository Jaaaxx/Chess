import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Arrays;


public class App extends JPanel {
    Display display;
    MouseController mouseController;
    static BufferedImage[] images = new BufferedImage[14];

    Color wBrown = new Color(146,99,74);
    Color dBrown = new Color(251,233,199);
    Color mGreen = new Color(166, 193, 158);

    /*  7: NULL
        0: KING
        1: QUEEN
        3: PAWN
        4: KNIGHT
        5: CASTLE
        6: BISHOP   */
    String[] stringPieces = {"null", "queen", "pawn", "knight", "castle", "bishop", "king"};
    int[][] board =
        {{-4, -3, -5, -6, -1, -5, -3, -4},
         {-2, -2, -2, -2, -2, -2, -2, -2},
         {0, 0, 0, 0, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0},
         {0, 0, 0, 0, 0, 0, 0, 0},
         {2, 2, 2, 2, 2, 2, 2, 2},
         {4, 3, 5, 6, 1, 5, 3, 4}};
    int selRow = -1;
    int selCol = -1;

    boolean whiteTurn = true;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = (display.screenSize.width / 8);

        // Draw board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                g.setColor(j % 2 == 0 ? (i % 2 == 0 ? wBrown : dBrown) : (i % 2 == 0 ? dBrown : wBrown));
                if (selRow != -1 && j == selRow && i == selCol)
                    g.setColor(mGreen);
                g.fillRect(i * w, j * w, w, w);
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                BufferedImage im = images[(Math.abs(board[i][j]) * 2) + (board[i][j] >= 0 ? 0 : 1)];
                g.drawImage(im, j * w + 8, i * w + 8, null);
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

    public void onClick(MouseEvent e) {
        Point p = e.getPoint();
        int row = (int) rangeConvert(p.y - display.insetHeight, display.screenSize.height, 0, 8, 0);
        int col = (int) rangeConvert(p.x - 7, display.screenSize.width, 0, 8, 0);
        if (selRow > -1) {
            if (whiteTurn ? board[row][col] >= 0 : board[row][col] <= 0) {
                int t = board[selRow][selCol];
                board[selRow][selCol] = board[row][col];
                board[row][col] = t;
                selRow = -1;
                selCol = -1;
            }
        } else {
            if (whiteTurn) {
                if (board[row][col] > 0) {
                    selRow = row;
                    selCol = col;
                    whiteTurn = !whiteTurn;
                }
            } else {
                if (board[row][col] < 0) {
                    selRow = row;
                    selCol = col;
                    whiteTurn = !whiteTurn;
                }
            }
        }
        display.getContentPane().repaint();
    }
    public double rangeConvert(double value, double oldMax, double oldMin, double newMax, double newMin) {
        double oldRange = (oldMax - oldMin);
        double newRange = (newMax - newMin);
        return ((((value - oldMin) * newRange) / oldRange) + newMin);
    }
    public static void main(String[] args) {
        int c = -1;
        for (int i = 0; i < 14; i++) {
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
    MouseController mouseController;
    int insetHeight;

    public Display(App app) {
        super("Chess");
        screenSize = new Dimension(640, 640);
        mouseController = new MouseController(app);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        app.setPreferredSize(screenSize);
        setLocationRelativeTo(null);
        add(app);
        addMouseListener(mouseController);
        pack();
        setVisible(true);
        insetHeight = getInsets().top;
    }
}

class MouseController extends JComponent implements MouseListener {
    App app;

    public MouseController(App app) {
        this.app = app;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        app.onClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

}