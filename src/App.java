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

import static java.awt.Font.PLAIN;


public class App extends JPanel {
    Display display;
    MouseController mouseController;
    static BufferedImage[] images = new BufferedImage[14];

    Color wBrown = new Color(146,99,74);
    Color dBrown = new Color(251,233,199);
    Color mGreen = new Color(166, 193, 158);

    /*  0: NULL
        1: QUEEN
        2: PAWN
        3: KNIGHT
        4: ROOK
        5: BISHOP
        6: KING   */
    String[] stringPieces = {"null", "queen", "pawn", "knight", "rook", "bishop", "king"};
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
    int winner = 0;
    int enPassant = 0;
    boolean movedKingW = false;
    boolean movedKingB = false;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = (display.screenSize.width / 8);

        if (winner == -1 || winner == 1) {
            g.setColor(winner == 1 ? Color.WHITE : Color.BLACK);
            g.fillRect(0, 0, display.screenSize.width, display.screenSize.height);
            g.setColor(winner == 1 ? Color.BLACK : Color.WHITE);
            g.drawString(winner == 1 ? "White Wins!" : "Black Wins!", display.screenSize.width / 2, display.screenSize.height / 2);
            return;
        }
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

        if (selRow > -1 && (whiteTurn ? board[row][col] > 0 : board[row][col] < 0)) {
            selRow = -1;
            selCol = -1;
            display.getContentPane().repaint();
            return;
        }

        if (selRow > -1) {
            switch (Math.abs(board[selRow][selCol])) {
                // Queen
                case (1): {
                    if (selRow == row || selCol == col) {
                        for (int i = Math.min(selRow, row) + 1; i < Math.max(selRow, row); i++)
                            if (board[i][selCol] != 0)
                                return;
                        for (int i = Math.min(selCol, col) + 1; i < Math.max(selCol, col); i++)
                            if (board[selRow][i] != 0)
                                return;
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    } else if (Math.abs(selRow - row) ==  Math.abs(selCol - col)) {
                        for (int i = 1; i < Math.abs(selRow - row); i++)
                            if (board[selRow + (row < selRow ? -i : i)][selCol + (col < selCol ? -i : i)] != 0)
                                return;
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    }
                    break;
                }
                // Pawn
                case (2): {
                    if (((Math.abs(board[row][col]) > 0) || (enPassant != 0 && (
                            whiteTurn ? (row == 2 && col == Math.abs(enPassant) - 1) : (row == 5 && col == Math.abs(enPassant) - 1))))
                            ? (row == (whiteTurn ? selRow - 1 : selRow + 1)
                            && Math.abs(col - selCol) == 1) : ((selRow == (whiteTurn ? 6 : 1))
                            ? (row == (whiteTurn ? selRow - 2 : selRow + 2) ||
                            row == (whiteTurn ? selRow - 1 : selRow + 1) && col == selCol):
                            (row == (whiteTurn ? selRow - 1 : selRow + 1) && col == selCol))) {
                        // Check for pieces in the way
                        for (int i = Math.min(selRow, row) + 1; i < Math.max(selRow, row); i++)
                            if (board[i][selCol] != 0)
                                return;
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        if (enPassant != 0 && row == (whiteTurn ? 2 : 5) && col == Math.abs(enPassant) - 1) {
                            int t = board[selRow][selCol];
                            board[selRow][selCol] = 0;
                            board[row + 1][col] = 0;
                            board[row][col] = t;
                        } else {
                            int t = board[selRow][selCol];
                            board[selRow][selCol] = 0;
                            board[row][col] = t;
                        }
                        if (selRow - row == 2)
                            enPassant = selCol + 1;
                        else if (selRow - row == -2)
                            enPassant = -(selCol + 1);
                        else
                            enPassant = 0;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;
                    }
                    break;
                }
                // Knight
                case (3): {
                    if ((Math.abs(selRow - row) == 2 && Math.abs(selCol - col) == 1)
                            || (Math.abs(selRow - row) == 1 && Math.abs(selCol - col) == 2)) {
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    }
                    break;
                }
                // Rook
                case (4): {
                    if (selRow == row || selCol == col) {
                        for (int i = Math.min(selRow, row) + 1; i < Math.max(selRow, row); i++)
                            if (board[i][selCol] != 0)
                                return;
                        for (int i = Math.min(selCol, col) + 1; i < Math.max(selCol, col); i++)
                            if (board[selRow][i] != 0)
                                return;
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    }
                    break;
                }
                // Bishop
                case (5): {
                    if (Math.abs(selRow - row) ==  Math.abs(selCol - col)) {
                        for (int i = 1; i < Math.abs(selRow - row); i++)
                            if (board[selRow + (row < selRow ? -i : i)][selCol + (col < selCol ? -i : i)] != 0)
                                return;
                        if (Math.abs(board[row][col]) == 6) {
                            winner = whiteTurn ? 1 : -1;
                        }
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    }
                    break;
                }
                // King
                case (6): {
                    if (Math.abs(selRow - row) <= 1 && Math.abs(selCol - col) <= 1) {
                        if (whiteTurn)
                            movedKingW = true;
                        else
                            movedKingB = true;
                        int t = board[selRow][selCol];
                        board[selRow][selCol] = 0;
                        board[row][col] = t;

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    } else if ((whiteTurn ? !movedKingW : !movedKingB) && Math.abs(col - selCol) == 2 && row == selRow &&
                               col < selCol ? Math.abs(board[row][0]) == 4 : Math.abs(board[row][7]) == 4) {

                        for (int i = Math.min((col < selCol ? 0 : 7), selCol) + 1; i < Math.max((col < selCol ? 0 : 7), selCol); i++) {
                            if (board[selRow][i] != 0)
                                return;
                        }

                        if (col < selCol) {
                            board[row][0] = 0;
                            board[selRow][selCol] = 0;
                            board[row][col] = whiteTurn ? 6 : -6;
                            board[row][col + 1] = whiteTurn ? 4 : -4;
                        } else {
                            board[row][7] = 0;
                            board[selRow][selCol] = 0;
                            board[row][col] = whiteTurn ? 4 : -4;
                            board[row][col + 1] = whiteTurn ? 6: -6;
                        }

                        whiteTurn = !whiteTurn;selRow = -1;selCol = -1;enPassant = 0;
                    }
                    break;
                }
            }
        } else {
            if (whiteTurn) {
                if (board[row][col] > 0) {
                    selRow = row;
                    selCol = col;
                }
            } else {
                if (board[row][col] < 0) {
                    selRow = row;
                    selCol = col;
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