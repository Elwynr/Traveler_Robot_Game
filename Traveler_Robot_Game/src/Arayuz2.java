import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

public class Arayuz2 extends JFrame {
    private JPanel gridPanel;
    private int rows = 11;
    private int cols = 11;
    private Engel engel;
    private Robot robot;
    private Timer animationTimer;
    private boolean[][] fogOfWar;
    private boolean isRunning = false;
    private JLabel statusLabel;
    private List<int[]> finalPath;
    private static boolean picker = false;

    public Arayuz2() {
        setTitle("Labirent Keşif Robotu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel label = new JLabel("Matris Boyutu (örn: 11x11):");
        JTextField sizeInput = new JTextField(5);
        JButton generateButton = new JButton("Labirent Oluştur");
        JButton runButton = new JButton("Çalıştır");
        JButton showResultButton = new JButton("Sonuç Göster");
        showResultButton.setEnabled(false);

        inputPanel.add(label);
        inputPanel.add(sizeInput);
        inputPanel.add(generateButton);
        inputPanel.add(runButton);
        inputPanel.add(showResultButton);
        add(inputPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Hazır");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        gridPanel = new JPanel();
        add(gridPanel, BorderLayout.CENTER);

        generateButton.addActionListener(e -> {
            String input = sizeInput.getText();
            if (input.matches("\\d+x\\d+")) {
                String[] parts = input.split("x");
                rows = Math.max(5, Integer.parseInt(parts[0]));
                cols = Math.max(5, Integer.parseInt(parts[1]));
                if (rows % 2 == 0) rows++;
                if (cols % 2 == 0) cols++;
            }
            resetMaze();
            showResultButton.setEnabled(false);
            isRunning = false;
            if (animationTimer != null) {
                animationTimer.stop();
            }
            fogOfWar = new boolean[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    fogOfWar[i][j] = false;
                }
            }
            refreshGrid(false);
        });

        runButton.addActionListener(e -> {
            if (!isRunning) {
                fogOfWar = new boolean[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        fogOfWar[i][j] = true;
                    }
                }
                startExploration();
                showResultButton.setEnabled(true);
                isRunning = true;
            }
        });

        showResultButton.addActionListener(e -> {
            if (animationTimer != null) {
                animationTimer.stop();
            }
            while (robot.moveOneStep()) {
            }
            fogOfWar = new boolean[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    fogOfWar[i][j] = false;
                }
            }
            finalPath = robot.getCurrentPath();
            refreshGrid(true);
            updateStatus();

        });

        resetMaze();
        setVisible(true);
    }

    private void resetMaze() {
        engel = new Engel(rows, cols);
        engel.generateRandomMaze();
        robot = new Robot(engel.getGrid(), 1, 1, rows-2, cols-2);
        fogOfWar = new boolean[rows][cols];
        finalPath = null;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fogOfWar[i][j] = false;
            }
        }
        refreshGrid(false);
    }

    private void startExploration() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExploration();
            }
        });

        updateFogOfWar(robot.getX(), robot.getY());
        animationTimer.start();
    }

    private void updateExploration() {
        if (robot.moveOneStep()) {
            updateFogOfWar(robot.getX(), robot.getY());
            refreshGrid(false);
            updateStatus();

        } else {
            if (robot.hasReachedGoal()) {
                animationTimer.stop();
                finalPath = robot.getCurrentPath();
                robot.saveToFile(picker);
                statusLabel.setText(String.format("Hedef bulundu! Toplam süre: %.2f sn, Gezilen kare: %d",
                        robot.getElapsedTimeSeconds(), robot.getMoveCount()));

            }
        }
    }

    private void updateFogOfWar(int x, int y) {
        int[] dx = {-1, 0, 1, 0, -1, -1, 1, 1};
        int[] dy = {0, 1, 0, -1, -1, 1, -1, 1};

        fogOfWar[x][y] = false;
        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                fogOfWar[newX][newY] = false;
            }
        }
    }

    private void refreshGrid(boolean showFinal) {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(rows, cols));
        int[][] grid = engel.getGrid();
        List<int[]> visitedCells = robot.getVisitedCells();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                if (fogOfWar[i][j]) {
                    cell.setBackground(Color.DARK_GRAY);
                } else {
                    if (grid[i][j] == 1) {
                        cell.setBackground(Color.BLACK);
                    } else {
                        cell.setBackground(Color.WHITE);

                        for (int[] visited : visitedCells) {
                            if (visited[0] == i && visited[1] == j) {
                                cell.setBackground(Color.LIGHT_GRAY);
                                break;
                            }
                        }

                        if (showFinal && finalPath != null) {
                            for (int[] pathCell : finalPath) {
                                if (pathCell[0] == i && pathCell[1] == j) {
                                    cell.setBackground(Color.YELLOW);
                                    break;
                                }
                            }
                        }
                    }
                }

                if (i == robot.getX() && j == robot.getY()) {
                    cell.setBackground(Color.BLUE);
                }

                if (i == 1 && j == 1) cell.setBackground(Color.GREEN);
                if (i == rows-2 && j == cols-2) cell.setBackground(Color.RED);

                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void updateStatus() {
        statusLabel.setText(String.format("Geçen süre: %.2f sn, Gezilen kare: %d",
                robot.getElapsedTimeSeconds(), robot.getMoveCount()));
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Arayuz2());
    }
}
