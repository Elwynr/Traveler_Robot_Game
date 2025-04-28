import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Engel {
    private int[][] grid;
    private final int WALL = 1;
    private final int PATH = 0;

    public Engel(int rows, int cols) {
        this.grid = new int[rows][cols];
    }

    public void generateRandomMaze() {
        for (int i = 0; i < grid.length; i++) {
            Arrays.fill(grid[i], WALL);
        }

        Random rand = new Random();
        recursiveBacktracker(1, 1, rand);

        grid[1][1] = PATH;
        grid[grid.length - 2][grid[0].length - 2] = PATH;
    }

    private void recursiveBacktracker(int x, int y, Random rand) {
        grid[x][y] = PATH;

        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        shuffleArray(directions, rand);

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValid(newX, newY) && grid[newX][newY] == WALL) {
                grid[x + dir[0] / 2][y + dir[1] / 2] = PATH;
                recursiveBacktracker(newX, newY, rand);
            }
        }
    }

    private boolean isValid(int x, int y) {
        return x > 0 && x < grid.length - 1 && y > 0 && y < grid[0].length - 1;
    }

    private void shuffleArray(int[][] array, Random rand) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public void fillGridFromURL(int[][] grid, String url) {
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++)
                    grid[row][col] = Integer.parseInt(String.valueOf(line.charAt(col)));
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error reading grid from URL: " + e.getMessage());
        }
    }

    private class Node implements Comparable<Node> {
        int x, y;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(
                    Math.abs(x - (grid.length - 2)) + Math.abs(y - (grid[0].length - 2)),
                    Math.abs(other.x - (grid.length - 2)) + Math.abs(other.y - (grid[0].length - 2))
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
