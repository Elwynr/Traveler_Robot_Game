import java.util.*;
import java.io.*;


public class Robot {
    private int x, y;
    private int goalX, goalY;
    private int[][] grid;
    private boolean[][] explored;
    private List<int[]> currentPath;
    private List<int[]> visitedCells;
    private long startTime;
    private int moveCount;
    private Stack<int[]> backtrackStack;
    private Set<String> deadEnds;
    private boolean picker;

    public Robot(int[][] grid, int startX, int startY, int goalX, int goalY) {
        this.grid = grid;
        this.x = startX;
        this.y = startY;
        this.goalX = goalX;
        this.goalY = goalY;
        this.explored = new boolean[grid.length][grid[0].length];
        this.visitedCells = new ArrayList<>();
        this.moveCount = 0;
        this.startTime = System.currentTimeMillis();
        this.backtrackStack = new Stack<>();
        this.deadEnds = new HashSet<>();

        exploreCurrentPosition();
    }

    public void saveToFile(boolean picker) {
        String fileName = picker ? "problem1.txt" : "problem2.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("Adımlar: " + moveCount + "\n");
            writer.write("Geçen zaman: " + getElapsedTimeSeconds() + " saniye\n");
            writer.write("Ziyaret Edilen Hücreler: \n");

            for (int[] cell : visitedCells) {
                writer.write(Arrays.toString(cell) + "\n");
            }

            writer.write("\n=====================\n");
        } catch (IOException e) {
            System.out.println("Dosyaya yazma hatası: " + e.getMessage());
        }
    }


    public void exploreCurrentPosition() {
        explored[x][y] = true;
        visitedCells.add(new int[]{x, y});
        backtrackStack.push(new int[]{x, y});

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValid(newX, newY)) {
                explored[newX][newY] = true;
            }
        }
    }

    public boolean moveOneStep() {
        if (hasReachedGoal()) {
            if (currentPath == null) {
                currentPath = new ArrayList<>(visitedCells);
            }
            saveToFile(picker);
            return false;
        }

        List<int[]> plannedPath = AStar.findPath(grid, x, y, goalX, goalY, explored);

        if (plannedPath == null || plannedPath.isEmpty() || isDeadEnd(x, y)) {
            // Backtrack to the last decision point
            if (!backtrackStack.isEmpty()) {
                deadEnds.add(x + "," + y);
                int[] lastPos = backtrackStack.pop();
                x = lastPos[0];
                y = lastPos[1];
                moveCount++;
                visitedCells.add(new int[]{x, y});
                return true;
            }
            return false;
        }

        if (plannedPath.size() > 1) {
            int[] nextPos = plannedPath.get(1);
            x = nextPos[0];
            y = nextPos[1];
            moveCount++;
            exploreCurrentPosition();
            return true;
        }

        return false;
    }

    public boolean moveOneStepWithObstacles() {
        if (hasReachedGoal()) {
            if (currentPath == null) {
                currentPath = new ArrayList<>(visitedCells);
            }
            saveToFile(picker);
            return false;
        }

        List<int[]> plannedPath = AStar.findPathWithMultipleObstacles(grid, x, y, goalX, goalY, explored);

        if (plannedPath == null || plannedPath.isEmpty() || isDeadEnd(x, y)) {
            if (!backtrackStack.isEmpty()) {
                deadEnds.add(x + "," + y);
                int[] lastPos = backtrackStack.pop();
                x = lastPos[0];
                y = lastPos[1];
                moveCount++;
                visitedCells.add(new int[]{x, y});
                return true;
            }
            return false;
        }

        if (plannedPath.size() > 1) {
            int[] nextPos = plannedPath.get(1); // Bir sonraki adımı alıyoruz
            x = nextPos[0];
            y = nextPos[1];
            moveCount++;
            exploreCurrentPosition(); // Mevcut konumu keşfet
            return true;
        }

        return false;
    }



    private boolean isDeadEnd(int x, int y) {
        return deadEnds.contains(x + "," + y);
    }

    public boolean hasReachedGoal() {
        return x == goalX && y == goalY;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < grid.length &&
                y >= 0 && y < grid[0].length &&
                grid[x][y] == 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean[][] getExplored() { return explored; }
    public List<int[]> getVisitedCells() { return visitedCells; }
    public List<int[]> getCurrentPath() { return currentPath; }
    public int getMoveCount() { return moveCount; }
    public double getElapsedTimeSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }
}