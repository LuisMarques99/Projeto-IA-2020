package mark_projects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class CSVFileWriter {
    private final String OUTPUT_FOLDER = "output_files";
    private File file;
    private FileWriter writer;

    public CSVFileWriter(String filepath) throws IOException {
        String folderPath = Paths.get(System.getProperty("user.dir"), OUTPUT_FOLDER).toString();
        String fullPath = Paths.get(folderPath, filepath).toString();

        File folder = new File(folderPath);
        this.file = new File(fullPath);

        if (folder.exists()) file.createNewFile();
        else {
            if (folder.mkdir()) file.createNewFile();
            else throw new IOException("Failed to create directory '" + folder + "'");
        }

        this.writer = new FileWriter(file);
    }

    public void writeLine(String[] data) throws IOException {
        writer.append(data[0]);
        for (int i = 1; i < data.length; i++) writer.append(",").append(data[i]);
        writer.append("\n");
    }

    public void close() throws IOException {
        writer.close();
    }
}
