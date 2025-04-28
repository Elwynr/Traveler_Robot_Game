import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Izgara {

    private int[][] grid;
    private int rows;
    private int cols;

    public Izgara() {
    }

    public void readGridFromURL(String url) {
        try (BufferedReader reader = new BufferedReader(new FileReader(url))) {
            String line;
            int tempRows = 0;
            int tempCols = 0;

            while ((line = reader.readLine()) != null) {
                tempCols = Math.max(tempCols, line.length());
                tempRows++;
            }

            this.rows = tempRows;
            this.cols = tempCols;
        } catch (IOException e) {
            System.err.println("Error reading grid dimensions from URL: " + e.getMessage());
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
