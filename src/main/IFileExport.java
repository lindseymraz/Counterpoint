import java.io.IOException;
import java.util.LinkedList;

public interface IFileExport {

    /**
     * Create a file with which to view the whole composition created thus far, as symbolized in the {@link Composition} object, in.
     * @param fileName The name of the file you want outputted, excluding the file extension (e.g. Name, not Name.xml)
     * @throws IOException
     */
    void outputAllCounterpoint(String fileName) throws IOException;

    /**
     * Create a file with which to view all of the current melodic options for the species being generated thus far. For example, all generated cantus firmi, or all generated first species nodes.
     * @param fileName The name of the file you want outputted, excluding the file extension (e.g. Name, not Name.xml)
     * @param melodies The list of all generated melodies. Either a list of cantus firmi, first species, second species, third species, or fourth species.
     * @throws IOException
     */
    void outputAllMelodicOptions(String fileName, LinkedList<? extends LinkedList<? extends Node>> melodies) throws IOException;

    /**
     * Create a file with which to view a single melodic option for the species being generated thus far.
     * @param fileName The name of the file you want outputted, excluding the file extension (e.g. Name, not Name.xml)
     * @param melody The melody to output
     * @throws IOException
     */
    void outputSingleMelody(String fileName, LinkedList<? extends Node> melody) throws IOException;
}
