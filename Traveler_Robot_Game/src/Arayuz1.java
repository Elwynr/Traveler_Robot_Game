import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class Arayuz1 extends JFrame {
    private JTable gridTable;
    private String currentURL = "url1.txt";
    private int startX, startY, goalX, goalY;
    private boolean[][] fogOfWar;
    private Robot robot;
    private Timer animationTimer;
    private JLabel statusLabel;
    private boolean isRunning = false;
    private int[][] currentGrid;
    private JButton runButton;
    private JButton showResultButton;
    private static boolean picker = true;

    public Arayuz1() {
        setTitle("Robot Labirent Gezgini");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton toggleButton = new JButton("URL Değiştir");
        runButton = new JButton("Çalıştır");
        showResultButton = new JButton("Sonuç Göster");
        showResultButton.setEnabled(false);

        controlPanel.add(toggleButton);
        controlPanel.add(runButton);
        controlPanel.add(showResultButton);
        add(controlPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Hazır");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        gridTable = new JTable();
        gridTable.setDefaultRenderer(Object.class, new GridCellRenderer());
        gridTable.setRowHeight(30);
        gridTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        gridTable.setEnabled(false);

        JPanel gridPanel = new JPanel(new BorderLayout());
        gridPanel.add(gridTable, BorderLayout.CENTER);
        add(gridPanel, BorderLayout.CENTER);

        toggleButton.addActionListener(e -> toggleURL());
        runButton.addActionListener(e -> startExploration());
        showResultButton.addActionListener(e -> showResult());

        loadGridFromURL(currentURL);

        setVisible(true);
    }

    private class GridCellRenderer implements TableCellRenderer {
        private final DefaultTableCellRenderer renderer;

        public GridCellRenderer() {
            renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int col) {
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (fogOfWar != null && fogOfWar[row][col]) {
                c.setBackground(Color.lightGray);
                renderer.setText("");
                return c;
            }

            int cellValue = value != null ? Integer.parseInt(value.toString()) : 0;

            if (row == startX && col == startY) {
                c.setBackground(Color.red);
            } else if (row == goalX && col == goalY) {
                c.setBackground(Color.yellow);
            } else if (robot != null && row == robot.getX() && col == robot.getY()) {
                c.setBackground(Color.black);
            } else {
                switch (cellValue) {
                    case 0:
                        c.setBackground(Color.white);
                        break;
                    case 1:
                        c.setBackground(Color.pink);
                        break;
                    case 2:
                        c.setBackground(Color.magenta);
                        break;
                    case 3:
                        c.setBackground(Color.blue);
                        break;
                    default:
                        c.setBackground(Color.black);
                }
            }

            if (robot != null) {
                for (int[] visited : robot.getVisitedCells()) {
                    if (visited[0] == row && visited[1] == col && cellValue == 0) {
                        c.setBackground(Color.green);
                    }
                }
            }

            renderer.setText(value != null ? value.toString() : "");
            return c;
        }
    }

    private void toggleURL() {
        clearRobotPath();
        currentGrid = null;
        startX = startY = goalX = goalY = -1;
        currentURL = currentURL.equals("url1.txt") ? "url2.txt" : "url1.txt";
        loadGridFromURL(currentURL);
        showResultButton.setEnabled(false);
        runButton.setEnabled(true);
        statusLabel.setText("URL changed: " + currentURL);
    }

    private void clearRobotPath() {
        if (robot != null) {
            robot = new Robot(currentGrid, startX, startY, goalX, goalY);
            fogOfWar = new boolean[currentGrid.length][currentGrid[0].length];
            for (int i = 0; i < fogOfWar.length; i++) {
                Arrays.fill(fogOfWar[i], true);
            }
        }
    }

    private void loadGridFromURL(String url) {
        Izgara izgara = new Izgara();
        izgara.readGridFromURL(url);

        int rows = izgara.getRows();
        int cols = izgara.getCols();
        currentGrid = new int[rows][cols];

        Engel engel = new Engel(rows, cols);
        engel.fillGridFromURL(currentGrid, url);

        assignRandomStartAndEnd(currentGrid, rows, cols);

        DefaultTableModel model = new DefaultTableModel(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                model.setValueAt(currentGrid[row][col], row, col);
            }
        }
        gridTable.setModel(model);

        fogOfWar = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fogOfWar[i][j] = false;
            }
        }

        resizeColumnWidth();
        isRunning = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    private void startExploration() {
        if (isRunning || currentGrid == null) return;

        for (int i = 0; i < fogOfWar.length; i++) {
            Arrays.fill(fogOfWar[i], true);
        }

        isRunning = true;
        runButton.setEnabled(false);
        showResultButton.setEnabled(true);

        robot = new Robot(currentGrid, startX, startY, goalX, goalY);
        updateFogOfWar(robot.getX(), robot.getY());

        animationTimer = new Timer(500, e -> {
            if (!robot.moveOneStepWithObstacles()) {
                animationTimer.stop();
                if (robot.hasReachedGoal()) {
                    statusLabel.setText(String.format("Hedef bulundu! Toplam süre: %.2f sn, Gezilen kare: %d",
                            robot.getElapsedTimeSeconds(), robot.getMoveCount()));
                }
            }
            updateFogOfWar(robot.getX(), robot.getY());
            gridTable.repaint();
            updateStatus();
        });

        animationTimer.start();
    }

    private void showResult() {
        if (robot == null) return;

        if (animationTimer != null) {
            animationTimer.stop();
        }

        for (int i = 0; i < fogOfWar.length; i++) {
            Arrays.fill(fogOfWar[i], false);
        }

        while (!robot.hasReachedGoal()) {
            robot.moveOneStepWithObstacles();
        }

        gridTable.repaint();
        updateStatus();

        robot.saveToFile(picker);

        statusLabel.setText(String.format("Sonuç kaydedildi! Toplam süre: %.2f sn, Gezilen kare: %d",
                robot.getElapsedTimeSeconds(), robot.getMoveCount()));
    }

    private void assignRandomStartAndEnd(int[][] grid, int rows, int cols) {
        Random rand = new Random();
        do {
            startX = rand.nextInt(rows);
            startY = rand.nextInt(cols);
        } while (grid[startX][startY] != 0);

        do {
            goalX = rand.nextInt(rows);
            goalY = rand.nextInt(cols);
        } while (grid[goalX][goalY] != 0 || (startX == goalX && startY == goalY));
    }

    private void updateFogOfWar(int x, int y) {
        int[] dx = {-1, 0, 1, 0, -1, -1, 1, 1};
        int[] dy = {0, 1, 0, -1, -1, 1, -1, 1};

        fogOfWar[x][y] = false;
        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValid(newX, newY)) {
                fogOfWar[newX][newY] = false;
            }
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < currentGrid.length && y >= 0 && y < currentGrid[0].length;
    }

    private void updateStatus() {
        if (robot != null) {
            statusLabel.setText(String.format("Geçen süre: %.2f sn, Gezilen kare: %d",
                    robot.getElapsedTimeSeconds(), robot.getMoveCount()));
        }
    }

    private void resizeColumnWidth() {
        for (int column = 0; column < gridTable.getColumnCount(); column++) {
            TableColumn tableColumn = gridTable.getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(30);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Arayuz1());
    }
}
