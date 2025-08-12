import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class OutputCSV implements IFileExport {
    static Composition composition;
    public void outputAllCounterpoint(String fileName) throws IOException {
        composition = IO.composition;
        FileWriter myWriter = new FileWriter(fileName + ".csv");
        String toWrite = writeMelody(composition.cantusFirmus, 0);
        if(composition.hasFirstSpecies()) {
            toWrite += writeMelody(composition.firstSpecies, 1);
        }
        if(composition.hasSecondSpecies()) {
            toWrite += writeMelody(composition.secondSpecies, 2);
        }
        if(composition.hasThirdSpecies()) {
            toWrite += writeMelody(composition.thirdSpecies, 3);
        }
        if(composition.hasFourthSpecies()) {
            toWrite += writeMelody(composition.fourthSpecies, 4);
        }
        myWriter.write(toWrite);
        myWriter.close();
    }

    public void outputAllMelodicOptions(String fileName, LinkedList<? extends LinkedList<? extends Node>> melodies) throws IOException {
        FileWriter myWriter = new FileWriter(fileName + ".csv");
        int acc = 0;
        for (LinkedList<? extends Node> melody : melodies) {
            myWriter.write(writeMelody(melody, acc));
            acc++;
        }
        myWriter.close();
    }

    public void outputSingleMelody(String fileName, LinkedList<? extends Node> melody) throws IOException {
        composition = IO.composition;
        FileWriter myWriter = new FileWriter(fileName + ".csv");
        myWriter.write(writeMelody(melody, 0));
        myWriter.close();
    }

    protected static String writeMelody(LinkedList<? extends Node> melody, int acc) {
        int size = (melody.size() - 1);
        String str = (acc + ", ");
        for(int i = 0; (i < size); i++) {
            str = str + (melody.get(i).pitch + " ");
        }
        return(str + (melody.get(size).pitch + ";\n"));
    }

}
