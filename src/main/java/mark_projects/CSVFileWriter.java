package mark_projects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class that represents the structure of a {@link CSVFileWriter CSV File Writer}
 *
 * @author Luis Marques
 */
public class CSVFileWriter {
    /**
     * String reference to the output folder of the files created
     */
    private final String OUTPUT_FOLDER = "robot_champ_project";

    /**
     * File reference to the file
     */
    private File file;

    /**
     * FileWriter reference to the file writer
     */
    private FileWriter writer;

    /**
     * boolean reference to define if the file was already created or not
     */
    private boolean newFile = false;

    /**
     * Creates an instance of a {@link CSVFileWriter CSV File Writer} with a filepath
     *
     * @param filename String name of the file to write the csv (it must be a .csv file)
     * @param header   String[] array of strings to write the header of the csv file
     * @throws IOException IO Exception
     */
    public CSVFileWriter(String filename, String[] header) throws IOException {
        String folderPath = Paths.get(System.getProperty("user.dir"), OUTPUT_FOLDER).toString();
        String filepath = Paths.get(folderPath, filename).toString();

        File folder = new File(folderPath);
        file = new File(filepath);

        if (!file.exists()) newFile = true;

        if (folder.exists()) file.createNewFile();
        else {
            if (folder.mkdir()) file.createNewFile();
            else throw new IOException("Failed to create directory '" + folder + "'");
        }

        writer = new FileWriter(file, true);

        if (newFile) writeLine(header);
    }

    /**
     * Writes a list of string arrays in the csv file
     *
     * @param list List of string arrays
     * @throws IOException IO Exception
     */
    public void writeAtOnce(List<String[]> list) throws IOException {
        for (String[] stringArray : list) writeLine(stringArray);
    }

    /**
     * Writes a line into the file created
     *
     * @param data String array of the data to write in the line
     * @throws IOException IO Exception
     */
    public void writeLine(String[] data) throws IOException {
        writer.append(data[0]);
        for (int i = 1; i < data.length; i++) writer.append(",").append(data[i]);
        writer.append("\n");
    }

    /**
     * Closes the file writer
     *
     * @throws IOException IO Exception
     */
    public void close() throws IOException {
        writer.close();
    }
}
