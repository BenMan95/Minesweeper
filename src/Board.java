import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.Timer;

public class Board extends JPanel {

    static private final int CELL_SIZE = 16;
    static private final int X_PADDING = 20;
    static private final int Y_PADDING = 63;

    public int rows;
    public int cols;
    public int num_mines;
    private boolean[][] cells;

    public Board(int rows, int cols, int mines) {
        setBoard(rows, cols, mines);
        addMouseListener(new Mouse());
    }

    public void setBoard(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        num_mines = mines;

        cells = new boolean[rows][cols];
        setMines();

        Dimension size = new Dimension(cols*CELL_SIZE + X_PADDING, rows * CELL_SIZE + Y_PADDING);
        setPreferredSize(size);
        repaint();
    }

    public void setMines() {
        // TODO
    }

    private int count3x3(int r, int c) {
        int count = 0;
        for (int rn = r-1; rn <= r+1; rn++) {
            if (rn < 0 || rn >= rows)
                continue;
            for (int cn = c-1; cn <= c+1; cn++)
                if (cn >= 0 && cn < cols)
                    if (cells[rn][cn])
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
        // TODO
    }

    private class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int button = e.getButton();
        }
    }

}
