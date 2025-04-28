import java.util.*;

public class AStar {
    private static final int[] DX = {-1, 0, 1, 0};
    private static final int[] DY = {0, 1, 0, -1};

    private static class Node implements Comparable<Node> {
        int x, y;
        int g, h, f;

        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getF() {
            return g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.getF(), other.getF());
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

    public static List<int[]> findPath(int[][] grid, int startX, int startY, int goalX, int goalY, boolean[][] explored) {
        int rows = grid.length;
        int cols = grid[0].length;

        if (grid[startX][startY] == 1 || grid[goalX][goalY] == 1) {
            System.err.println("Başlangıç veya hedef noktası engelin üzerinde!");
            return null;
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<String, Node> allNodes = new HashMap<>();

        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = manhattanDistance(startX, startY, goalX, goalY);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            for (int i = 0; i < 4; i++) {
                int newX = current.x + DX[i];
                int newY = current.y + DY[i];

                if (!isValid(newX, newY, rows, cols) || grid[newX][newY] == 1) {
                    continue;
                }

                String key = newX + "," + newY;
                Node neighbor = allNodes.getOrDefault(key, new Node(newX, newY));
                int tentativeG = current.g + 1;

                if (!allNodes.containsKey(key) || tentativeG < neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.h = manhattanDistance(newX, newY, goalX, goalY);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    allNodes.put(key, neighbor);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    public static List<int[]> findPathWithMultipleObstacles(int[][] grid, int startX, int startY, int goalX, int goalY, boolean[][] explored) {

        int rows = grid.length;
        int cols = grid[0].length;

        if (isObstacle(grid[startX][startY]) || isObstacle(grid[goalX][goalY])) {
            System.err.println("Başlangıç veya hedef noktası engelin üzerinde!");
            return null;
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<String, Node> allNodes = new HashMap<>();

        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = manhattanDistance(startX, startY, goalX, goalY);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);
        allNodes.put(startX + "," + startY, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            for (int i = 0; i < 4; i++) {
                int newX = current.x + DX[i];
                int newY = current.y + DY[i];

                if (!isValid(newX, newY, rows, cols) || isObstacle(grid[newX][newY])) {
                    continue;
                }

                String key = newX + "," + newY;
                Node neighbor = allNodes.getOrDefault(key, new Node(newX, newY));
                int tentativeG = current.g + 1;

                if (!allNodes.containsKey(key) || tentativeG < neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.h = manhattanDistance(newX, newY, goalX, goalY);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    allNodes.put(key, neighbor);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    private static boolean isObstacle(int cellValue) {
        return cellValue == 1 || cellValue == 2 || cellValue == 3;
    }

    private static boolean isValid(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private static int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private static List<int[]> reconstructPath(Node endNode) {
        List<int[]> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(0, new int[]{current.x, current.y});
            current = current.parent;
        }
        return path;
    }
}
