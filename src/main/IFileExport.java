import java.io.IOException;

public interface IFileExport {

    /**
     * Create a file with which to view the composition in.
     * @param fileName The name of the file you want outputted, excluding the file extension (e.g. Name, not Name.xml)
     * @throws IOException
     */
    void outputFile(String fileName) throws IOException;
}
