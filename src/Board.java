import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class Board extends JComponent implements ActionListener {

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

    private Game game;

    private Timer timer;
    private int time;

    private int mouseButton; // 0 if no button pressed
    private int clickArea; // 0 if board clicked, 1 if face clicked
    private int mouseX;
    private int mouseY;

    private int width;
    private int height;

    public Board(int rows, int cols, int mines) {
        addMouseListener(new Mouse());
        timer = new Timer(1000, this);

        game = new Game();
        setBoard(rows, cols, mines);
        startGame();
        repaint();
    }

    public void setBoard(int rows, int cols, int mines) {
        game.setBoard(rows, cols, mines);
        width = cols*CELL_SIZE+20;
        height = rows*CELL_SIZE+63;
        Dimension size = new Dimension(width*SCALE, height*SCALE);
        setPreferredSize(size);
    }

    public void startGame() {
        game.startGame();
        timer.stop();
        time = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(2,2);
        drawBackground(g2d);
        drawHeaders(g2d);

        for (int r = 0; r < game.getRows(); r++)
            for (int c = 0; c < game.getCols(); c++)
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

        int faceX = game.getCols()*CELL_SIZE/2;
        g2d.fillRect(faceX-1, 15, 24, 24);
        g2d.fillRect(faceX, 16, 24, 24);

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
        int remMines = game.getNumMines() - game.getNumFlags();
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
        String filename;
        if (game.getState() == Game.STATE_WIN)
            filename = "face_win.png";
        else if (game.getState() == Game.STATE_LOSE)
            filename = "face_lose.png";
        else {
            if (mouseButton == MouseEvent.BUTTON1 || mouseButton == MouseEvent.BUTTON2)
                filename = "face_reveal.png";
            else
                filename = "face_smile.png";
        }

        Image img = images.get(filename);
        g2d.drawImage(img, game.getCols()*CELL_SIZE/2, 16, this);
    }

    private void drawCell(Graphics2D g2d, int r, int c) {
        String filename;
        if (game.getCell(r,c) == Game.CELL_FLAGGED)
            filename = "flag.png";
        else
            filename = String.format("mines%d.png", game.count3x3(r,c));

        Image img = images.get(filename);
        g2d.drawImage(img, c*CELL_SIZE + 12, r*CELL_SIZE + 55, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        time++;
        repaint();
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
        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

}