package mark_projects;

import mark2.CSVFileWriter;

import java.io.IOException;

/**
 * Class that runs all the demos used to test the algorithms
 */
public class App {

    /**
     * Main method
     * @param args args
     */
    public static void main(String[] args) {
        // App main
        CSVFileWriter file;
        try {
            // Creates a file
            file = new CSVFileWriter("teste.csv");

            // Writes header
            file.writeLine(new String[]{"Name", "Life Points", "Pos X", "Pos Y"});

            // Writes data
            file.writeLine(new String[]{"\"Robot 1\"", "100", "34", "23"});
            file.writeLine(new String[]{"\"Robot 2\"", "134", "45", "75"});
            file.writeLine(new String[]{"\"Robot 3\"", "76", "65", "196"});

            // Closes the file
            file.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
