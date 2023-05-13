import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Random;

public class Board extends JPanel {

    static private final int CELL_SIZE = 16;
    static private final int X_PADDING = 20;
    static private final int Y_PADDING = 63;

    private int rows;
    private int cols;
    private int num_mines;
    private boolean[][] mines;
    private boolean[][] revealed;

    private int width;
    private int height;

    public Board(int rows, int cols, int mines) {
        setBoard(rows, cols, mines);
        addMouseListener(new Mouse());
    }

    public void setBoard(int rows, int cols, int num_mines) {
        this.rows = rows;
        this.cols = cols;
        this.num_mines = num_mines;

        mines = new boolean[rows][cols];
        setMines();

        revealed = new boolean[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                revealed[r][c] = false;

        width = cols*CELL_SIZE + X_PADDING;
        height = rows*CELL_SIZE + Y_PADDING;
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        repaint();
    }

    public void setMines() {
        // generate mines
        int size = rows*cols;
        boolean[] arr = new boolean[size];
        for (int i = 0; i < size; i++)
            arr[i] = i < num_mines;

        // shuffle mines
        Random rand = new Random();
        for (int i = 1; i < size; i++) {
            int j = rand.nextInt(i);
            boolean temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        // place mines
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                mines[r][c] = arr[r*cols + c];
    }

    private int count3x3(int r, int c) {
        int count = 0;
        for (int rn = r-1; rn <= r+1; rn++) {
            if (rn < 0 || rn >= rows)
                continue;
            for (int cn = c-1; cn <= c+1; cn++)
                if (cn >= 0 && cn < cols)
                    if (mines[rn][cn])
                        count++;
        }
        return count;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawBoard(g2d);
    }

    private void drawBoard(Graphics2D g2d) {

    }

    private class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int button = e.getButton();
        }
    }

}
