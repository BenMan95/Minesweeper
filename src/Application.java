import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class Application extends JFrame {

    private Board board;

    public Application() {
        setResizable(false);
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        board = new Board(16, 30, 99);
        add(board);
        makeMenu();
        pack();
        setLocationRelativeTo(null);
    }

    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem menuItem;

        menuItem = new JMenuItem("Set difficulty");
        menu.add(menuItem);

        menuBar.add(menu);
        menuItem.addActionListener(e -> {
            JTextField rowField = new JTextField(5);
            JTextField colField = new JTextField(5);
            JTextField minesField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("rows:"));
            panel.add(rowField);
            panel.add(Box.createHorizontalStrut(15));
            panel.add(new JLabel("cols:"));
            panel.add(colField);
            panel.add(Box.createHorizontalStrut(15));
            panel.add(new JLabel("mines:"));
            panel.add(minesField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Set size", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int rows = Integer.parseInt(rowField.getText());
                    int cols = Integer.parseInt(colField.getText());
                    int mines = Integer.parseInt(colField.getText());
                    board.setBoard(rows, cols, mines);
                    pack();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Input must be integers!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}