import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class Board extends JComponent implements ActionListener {

    static private final int AREA_OTHER = 0;
    static private final int AREA_FACE = 1;
    static private final int AREA_BOARD = 2;

    static private final int CELL_SIZE = 16;
    static private final int SCALE = 2;

    static private final File ASSETS = new File("sprites");
    static private final Map<String, Image> IMAGES = new HashMap<>();
    static {{
        for (File f : Objects.requireNonNull(ASSETS.listFiles())) {
            ImageIcon icon = new ImageIcon(f.getPath());
            Image image = icon.getImage();
            IMAGES.put(f.getName(), image);
        }
    }}

    private final Game game;

    private final Timer timer;
    private int time;

    private boolean button1; // left click
    private boolean button2; // middle click
    private boolean button3; // right click

    private int areaClicked;
    private int areaHovered;

    private int mouseRow;
    private int mouseCol;

    private int width;
    private int height;

    public Board(int rows, int cols, int mines) {
        // add mouse controls
        MouseAdapter mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        // set timer
        timer = new Timer(0, this);
        timer.setDelay(1000);

        // start game
        game = new Game();
        setBoard(rows, cols, mines);
        repaint();
    }

    public void setBoard(int rows, int cols, int mines) {
        // set size
        game.setBoard(rows, cols, mines);
        width = cols * CELL_SIZE + 20;
        height = rows * CELL_SIZE + 63;
        Dimension size = new Dimension(width*SCALE, height*SCALE);
        setPreferredSize(size);

        startGame();
    }

    public void startGame() {
        game.startGame();
        timer.stop();
        time = 0;
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

        // draw borders for face
        int faceX = width/2 - 10;
        g2d.fillRect(faceX-1, 15, 25, 25);
        g2d.fillRect(faceX, 16, 25, 25);

        // draw corners
        Image img;
        img = IMAGES.get("corner1.png");
        g2d.drawImage(img, 0, 0, this);
        img = IMAGES.get("corner2.png");
        g2d.drawImage(img, width-img.getWidth(this), 0, this);
        img = IMAGES.get("corner3.png");
        g2d.drawImage(img, 0, height-img.getHeight(this), this);
    }

    private void drawHeaders(Graphics2D g2d) {
        String filename;
        Image image;

        // get displayed mine count
        String minesDisplay;
        if (game.getState() == Game.STATE_WIN)
            minesDisplay = "000";
        else {
            int remMines = game.getNumMines() - game.getNumFlags();
            if (remMines > 999) minesDisplay = "999";
            else if (remMines < -99) minesDisplay = "-99";
            else minesDisplay = String.format("%03d", remMines);
        }

        // get displayed time
        String timeDisplay;
        if (time > 999) timeDisplay = "999";
        else if (time < -99) timeDisplay = "-99";
        else timeDisplay = String.format("%03d", time);

        // draw counters
        for (int i = 0; i < 3; i++) {
            filename = String.format("counter%c.png", minesDisplay.charAt(i));
            image = IMAGES.get(filename);
            g2d.drawImage(image, 17+13*i, 16, this);

            filename = String.format("counter%c.png", timeDisplay.charAt(i));
            image = IMAGES.get(filename);
            g2d.drawImage(image, width-54+13*i, 16, this);
        }

        // draw face
        filename = faceFile();
        image = IMAGES.get(filename);
        g2d.drawImage(image, game.getCols() * CELL_SIZE / 2, 16, this);
    }

    private void drawBoard(Graphics2D g2d) {
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                String filename = cellFile(r, c);
                Image img = IMAGES.get(filename);
                g2d.drawImage(img, c * CELL_SIZE + 12, r * CELL_SIZE + 55, this);
            }
        }
    }

    // get the filename for the correct face image
    private String faceFile() {
        if (areaClicked == AREA_FACE && areaHovered == AREA_FACE)
            return "face_click.png";

        if (game.getState() == Game.STATE_WIN)
            return "face_win.png";

        if (game.getState() == Game.STATE_LOSE)
            return "face_lose.png";

        if (areaClicked == AREA_BOARD && (button1 || button2))
            return "face_reveal.png";

        return "face_smile.png";
    }

    // get the filename for the correct cell image
    private String cellFile(int r, int c) {
        int cell = game.getCell(r, c);

        // display flag on flagged cells
        if (cell == Game.CELL_FLAGGED) {
            // show a different image on loss if a flag is incorrectly placed
            if (game.getState() != Game.STATE_PLAY && !game.getMine(r, c))
                return "flag_bad.png";

            return "flag.png";
        }

        if (cell == Game.CELL_HIDDEN) {
            if (game.getState() == Game.STATE_WIN)
                return "flag.png";

            if (game.getState() == Game.STATE_LOSE) {
                if (game.getMine(r, c))
                    return "mine.png";
                return "hidden.png";
            }

            if (areaClicked != AREA_BOARD)
                return "hidden.png";

            int dist = Math.max(Math.abs(r - mouseRow), Math.abs(c - mouseCol));

            if (button2 && dist <= 1)
                return "mines0.png";

            if (button1) {
                if (button3 && dist <= 1)
                    return "mines0.png";

                if (dist == 0)
                    return "mines0.png";
            }

            return "hidden.png";
        }

        if (game.getMine(r, c))
            return "mine_bad.png";

        return String.format("mines%d.png", game.countMines3x3(r,c));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(SCALE, SCALE);

        drawBackground(g2d);
        drawHeaders(g2d);
        drawBoard(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        time++;
        repaint();
    }

    private class Mouse extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            updatePos(e);
            areaClicked = areaHovered;

            // update held buttons
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> button1 = true;
                case MouseEvent.BUTTON2 -> button2 = true;
                case MouseEvent.BUTTON3 -> button3 = true;
            }

            // toggle flag on right click
            if (!button1 && !button2 && button3 && game.inBounds(mouseRow, mouseCol))
                game.toggleFlag(mouseRow, mouseCol);

            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            updatePos(e);

            // actions if board clicked
            if (areaClicked == AREA_BOARD && areaHovered == AREA_BOARD) {

                if (button2 || (button1 && button3)) { // reveal 3x3 area on middle click or left+right click
                    int count = game.getCell(mouseRow, mouseCol);
                    int flags = game.countFlags3x3(mouseRow, mouseCol);

                    if (count == flags)
                        game.reveal3x3(mouseRow, mouseCol);

                } else if (button1) { // reveal single cell on left click
                    // on first click, reshuffle mines until enough cell revealed
                    if (game.getNumRevealed() == 0) {
                        while (game.countMines3x3(mouseRow, mouseCol) > 0)
                            game.setMines();
                        timer.start();
                    }

                    game.revealCell(mouseRow, mouseCol);
                }

                if (game.getState() != Game.STATE_PLAY)
                    timer.stop();
            }

            // restart game when face clicked
            if (areaClicked == AREA_FACE && areaHovered == AREA_FACE)
                startGame();

            // update held buttons
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> button1 = false;
                case MouseEvent.BUTTON2 -> button2 = false;
                case MouseEvent.BUTTON3 -> button3 = false;
            }

            areaClicked = AREA_OTHER;
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            updatePos(e);
            repaint();
        }

        // update mouse position
        private void updatePos(MouseEvent e) {
            // get unscaled coordinates
            int x = e.getX() / SCALE;
            int y = e.getY() / SCALE;

            // set row and column position on board
            mouseRow = (y - 55) / CELL_SIZE;
            mouseCol = (x - 12) / CELL_SIZE;
            if (y < 55) mouseRow--;
            if (x < 12) mouseCol--;

            // check mouse area
            areaHovered = checkArea(x, y);
        }

        // checks what area a coordinate is over
        private int checkArea(int x, int y) {
            int rx, ry;

            // check for coordinate over face
            rx = x - game.getCols() * CELL_SIZE / 2;
            ry = y - 16;
            if (rx >= 0 && ry >= 0 && rx < 24 && ry < 24)
                return AREA_FACE;

            // check for coordinate over board
            rx = x - 12;
            ry = y - 55;
            if (rx >= 0 && ry >= 0 && rx < width-20 && ry < height-63)
                return AREA_BOARD;

            return AREA_OTHER;
        }
    }
}