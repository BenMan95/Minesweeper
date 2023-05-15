import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class Board extends JComponent {

    static private final int CELL_SIZE = 16;
    static private final int SCALE = 2;

    static private File assets = new File("sprites");
    static private Map<String, Image> images;

    {{
        images = new HashMap<>();
        for (File f : assets.listFiles()) {
            ImageIcon icon = new ImageIcon(f.getPath());
            Image image = icon.getImage();
            images.put(f.getName(), image);
        }
    }}

    private int rows;
    private int cols;
    private int numMines;
    private int numRevealed;
    private boolean[][] mines;
    private boolean[][] revealed;

    private int time;

    private int width;
    private int height;

    public Board(int rows, int cols, int mines) {
        setBoard(rows, cols, mines);
        addMouseListener(new Mouse());
    }

    public void setBoard(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        mines = new boolean[rows][cols];
        revealed = new boolean[rows][cols];
        setMines();

        width = cols*CELL_SIZE+20;
        height = rows*CELL_SIZE+63;
        Dimension size = new Dimension(width*SCALE, height*SCALE);
        setPreferredSize(size);
        repaint();
    }

    public void setMines() {
        // generate mines
        int size = rows*cols;
        boolean[] arr = new boolean[size];
        for (int i = 0; i < size; i++)
            arr[i] = i < numMines;

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

        // reset revealed spaces
        numRevealed = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                revealed[r][c] = false;
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
        g2d.scale(2,2);
        drawBackground(g2d);
        drawHeaders(g2d);

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                drawCell(g2d, r, c);
    }

    private void drawBackground(Graphics2D g2d) {
        // fill backgrounds
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(192, 192, 192));
        g2d.fillRect(3, 3, width, height);

        // fill white borders
        g2d.setColor(Color.WHITE);
        g2d.fillRect(10, 44, width-15, 2);
        g2d.fillRect(10, height-8, width-15, 3);
        g2d.fillRect(width-8, 53, 3, height-58);

        // fill grey borders
        g2d.setColor(new Color(128, 128, 128));
        g2d.fillRect(9, 9, width-15, 2);
        g2d.fillRect(9, 52, width-15, 3);
        g2d.fillRect(9, 52, 3, height-58);
        g2d.fillRect(cols*CELL_SIZE/2-1, 15, 24, 24);
        g2d.fillRect(cols*CELL_SIZE/2, 16, 24, 24);

        // draw corners
        Image img;
        img = images.get("corner1.png");
        g2d.drawImage(img, 0, 0, this);
        img = images.get("corner2.png");
        g2d.drawImage(img, width-img.getWidth(this), 0, this);
        img = images.get("corner3.png");
        g2d.drawImage(img, 0, height-img.getHeight(this), this);
    }

    private void drawHeaders(Graphics2D g2d) {
        // draw mine count
        String disp;
        int remMines = numMines - numRevealed;
        if (remMines > 999) disp = "999";
        else if (remMines < -99) disp = "-99";
        else disp = String.format("%03d", remMines);

        for (int i = 0; i < 3; i++) {
            String filename = String.format("counter%c.png", disp.charAt(i));
            Image img = images.get(filename);
            g2d.drawImage(img, 17+13*i, 16, this);
        }

        // draw timer
        if (time > 999) disp = "999";
        else if (time < -99) disp = "-99";
        else disp = String.format("%03d", time);

        for (int i = 0; i < 3; i++) {
            String filename = String.format("counter%c.png", disp.charAt(i));
            Image img = images.get(filename);
            g2d.drawImage(img, width-54+13*i, 16, this);
        }

        // draw face
        // TODO: add other faces
        String filename = "face_smile.png";
        Image img = images.get(filename);
        g2d.drawImage(img, cols*CELL_SIZE/2, 16, this);
    }

    private void drawCell(Graphics2D g2d, int r, int c) {
        // TODO: set correct images
        String filename;
        if (mines[r][c]) filename = "flag.png";
        else filename = String.format("mines%d.png", count3x3(r,c));

        Image img = images.get(filename);
        g2d.drawImage(img, c*CELL_SIZE + 12, r*CELL_SIZE + 55, this);
    }

    private class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int button = e.getButton();
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            int button = e.getButton();
        }
    }

}
