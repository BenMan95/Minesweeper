import java.util.Random;

public class Game {

    public static final int CELL_HIDDEN = -1;
    public static final int CELL_FLAGGED = -2;

    public static final int STATE_PLAY = 0;
    public static final int STATE_WIN = 1;
    public static final int STATE_LOSE = 2;

    private int rows;
    private int cols;

    private int numMines;
    private int numFlags;
    private int numRevealed;

    private boolean[][] mines;
    private int[][] cells;

    private int state;

    public void setBoard(int rows, int cols, int numMines) {
        this.rows = rows;
        this.cols = cols;
        this.numMines = numMines;

        mines = new boolean[rows][cols];
        cells = new int[rows][cols];
        startGame();
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getNumMines() { return numMines; }
    public int getNumFlags() { return numFlags; }
    public int getNumRevealed() { return numRevealed; }
    public int getState() { return state; }

    public boolean getMine(int r, int c) { return mines[r][c]; }
    public int getCell(int r, int c) { return cells[r][c]; }

    public boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols;
    }

    public void startGame() {
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

        // place mines and reset cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                mines[r][c] = arr[r * cols + c];
                cells[r][c] = CELL_HIDDEN;
            }
        }

        numFlags = 0;
        numRevealed = 0;
        state = STATE_PLAY;
    }

    // toggle flag if able
    // does not check for out of bounds
    public void toggleFlag(int r, int c) {
        if (state != STATE_PLAY)
            return;

        if (cells[r][c] == CELL_FLAGGED)
            cells[r][c] = CELL_HIDDEN;
        else if (cells[r][c] == CELL_HIDDEN)
            cells[r][c] = CELL_FLAGGED;
    }

    // reveal cell if able
    // also reveals cells around 0
    // does not check for out of bounds
    public void reveal(int r, int c) {
        if (state != STATE_PLAY)
            return;

        if (cells[r][c] != CELL_HIDDEN)
            return;

        if (mines[r][c]) {
            cells[r][c] = 0;
            state = STATE_LOSE;
            return;
        }

        cells[r][c] = countMines3x3(r, c);
        if (cells[r][c] == 0)
            reveal3x3(r, c);

        numRevealed++;
        if (numRevealed + numMines == rows * cols)
            state = STATE_WIN;
    }

    // reveal cells in 3x3 area if able
    // does not check for out of bounds
    public void reveal3x3(int r, int c) {
        for (int rn = r-1; rn <= r+1; rn++) {
            if (rn < 0 || rn >= rows)
                continue;
            for (int cn = c-1; cn <= c+1; cn++)
                if (cn >= 0 && cn < cols)
                    reveal(rn, cn);
        }
    }

    // count mines in a 3x3 area
    public int countMines3x3(int r, int c) {
        int count = 0;
        for (int rn = r-1; rn <= r+1; rn++) {
            if (rn < 0 || rn >= rows)
                continue;
            for (int cn = c-1; cn <= c+1; cn++)
                if (cn >= 0 && cn < cols && mines[rn][cn])
                    count++;
        }
        return count;
    }

    // count flags in a 3x3 area
    public int countFlags3x3(int r, int c) {
        int count = 0;
        for (int rn = r-1; rn <= r+1; rn++) {
            if (rn < 0 || rn >= rows)
                continue;
            for (int cn = c-1; cn <= c+1; cn++)
                if (cn >= 0 && cn < cols && cells[rn][cn] == CELL_FLAGGED)
                    count++;
        }
        return count;
    }

}
