import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class OutputMusicXML implements IFileExport {

    private static int fifthsValue;
    static Composition composition;
    private final String fileStart = ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<!DOCTYPE score-partwise PUBLIC\n" +
            "\t\"-//Recordare//DTD MusicXML 4.0 Partwise//EN\"\n" +
            "\t\"http://www.musicxml.org/dtds/partwise.dtd\">\n" +
            "<score-partwise version=\"4.0\">\n");
    private final String fileEnd = "</score-partwise>";

    private final String partListStart = "\t<part-list>\n";
    private final String partListEnd = "\t</part-list>\n";

    private final String partEnd = "\t</part>\n";

    private final String measureEnd = "\t\t</measure>\n";

    private final String noteStart = "\t\t\t<note>\n" +
            "\t\t\t\t<pitch>\n";
    private final String noteEnd = "\t\t\t\t</pitch>\n" +
            "\t\t\t\t<duration>4</duration>\n" +
            "\t\t\t\t<voice>1</voice>\n" +
            "\t\t\t\t<type>whole</type>\n" +
            "\t\t\t</note>\n";

    private final String endBar = "\t\t\t<barline location=\"right\">\n" +
            "\t\t\t\t<bar-style>light-heavy</bar-style>\n" +
            "\t\t\t</barline>\n";

    private final String newSystem = "\t\t\t<print new-system=\"yes\">\n" +
            "\t\t\t</print>\n";

    private final String labelStart = "\t\t\t<direction placement=\"above\">\n" +
            "\t\t\t\t<direction-type>\n" +
            "\t\t\t\t\t<words>";

    private final String labelEnd = "</words>\n" +
            "\t\t\t\t</direction-type>\n" +
            "\t\t\t</direction>\n";


    public void outputAllCounterpoint(String fileName) throws IOException {
        composition = IO.composition;
        FileWriter myWriter = new FileWriter(fileName + ".musicxml");
        String toWrite = fileStart;
        toWrite += partList();
        toWrite += partElements();
        toWrite += fileEnd;
        myWriter.write(toWrite);
        myWriter.close();
    }

    public void outputAllMelodicOptions(String fileName, LinkedList<? extends LinkedList<? extends Node>> melodies) throws IOException {
        FileWriter myWriter = new FileWriter(fileName + ".musicxml");
        String toWrite = fileStart;
        toWrite += partListSinglePart(melodies.get(0).get(0));
        toWrite += partElementsSinglePartManyMelodies(melodies.get(0).get(0), melodies);
        toWrite += fileEnd;
        myWriter.write(toWrite);
        myWriter.close();
    }

    public void outputSingleMelody(String fileName, LinkedList<? extends Node> melody) throws IOException {
        FileWriter myWriter = new FileWriter(fileName + ".musicxml");
        String toWrite = fileStart;
        toWrite += partListSinglePart(melody.get(0));
        toWrite += partElementsSinglePart(melody.get(0), melody);
        toWrite += fileEnd;
        myWriter.write(toWrite);
        myWriter.close();
    }
    /**
     *
     * @return String with XML needed to represent a <part-list>
     */

    public String partList() {
        String toReturn = partListStart;
        toReturn += partListHelper("Cantus Firmus", "CF");
        if(composition.hasFirstSpecies()) {
            toReturn += partListHelper("First Species", "1S");
        }
        if(composition.hasSecondSpecies()) {
            toReturn += partListHelper("Second Species", "2S");
        }
        if(composition.hasThirdSpecies()) {
            toReturn += partListHelper("Third Species", "3S");
        }
        if(composition.hasFourthSpecies()) {
            toReturn += partListHelper("Fourth Species", "4S");
        }
        toReturn += partListEnd;
        return toReturn;
    }

    /**
     * Modified from {@link #partList()} for outputting one part only.
     * @param node A single node from the list of melodies, used to identify which melody type the list is of.
     * @return String with XML needed to represent a <part-list>
     */

    public String partListSinglePart(Node node) {
        String toReturn = partListStart;
        if(node instanceof CantusFirmusNode) {
            toReturn += partListHelper("Cantus Firmus", "CF");
        } else if(node instanceof FirstSpeciesNode) {
            toReturn += partListHelper("First Species", "1S");
        } else if(node instanceof SecondSpeciesNode) {
            toReturn += partListHelper("Second Species", "2S");
        } else if(node instanceof ThirdSpeciesNode) {
            toReturn += partListHelper("Third Species", "3S");
        } else if(node instanceof FourthSpeciesNode) {
            toReturn += partListHelper("Fourth Species", "4S");
        }
        toReturn += partListEnd;
        return toReturn;
    }

    /**
     * @param name including whitespace characters
     * @param abbreviation abbreviation of name
     * @return String with XML needed to represent a <score-part> with the given name.
     */

    private String partListHelper(String name, String abbreviation) {
        return ("\t\t<score-part id=\"" + name.replaceAll("\\s", "") + "\">\n" +
                "\t\t\t<part-name>" + name + "</part-name>\n" +
                "\t\t\t<part-abbreviation>" + abbreviation + "</part-abbreviation>\n" +
                "\t\t</score-part>\n");
    }

    /**
     * @return A String with XML for almost all of the rest of the score, which is <part> elements.
     */

    private String partElements() {
        String toReturn = "";
        toReturn += partElement(composition.cantusFirmus, "CantusFirmus");
        if(composition.hasFirstSpecies()) {
            toReturn += partElement(composition.firstSpecies, "FirstSpecies");
        }
        if(composition.hasSecondSpecies()) {
            toReturn += partElement(composition.secondSpecies, "SecondSpecies");
        }
        if(composition.hasThirdSpecies()) {
            toReturn += partElement(composition.thirdSpecies, "ThirdSpecies");
        }
        if(composition.hasFourthSpecies()) {
            toReturn += partElement(composition.fourthSpecies, "FourthSpecies");
        }
        return toReturn;
    }

    /**
     * Modified from {@link #partElements()} for outputting many melodies from one part only.
     * @param node A single node from the list of melodies, used to identify which melody type the list is of.
     * @param melodies list of melodies
     * @return A String with XML for almost all of the rest of the score, which is <part> elements.
     */

    private String partElementsSinglePartManyMelodies(Node node, LinkedList<? extends LinkedList<? extends Node>> melodies) {
        String toReturn = "";
        if(node instanceof CantusFirmusNode) {
            toReturn = partElementSinglePart(melodies, "CantusFirmus");
        }
        else if(node instanceof FirstSpeciesNode) {
            toReturn = partElementSinglePart(melodies, "FirstSpecies");
        }
        else if(node instanceof SecondSpeciesNode) {
            toReturn = partElementSinglePart(melodies, "SecondSpecies");
        }
        else if(node instanceof ThirdSpeciesNode) {
            toReturn = partElementSinglePart(melodies, "ThirdSpecies");
        }
        else if(node instanceof FourthSpeciesNode) {
            toReturn = partElementSinglePart(melodies, "FourthSpecies");
        }
        return toReturn;
    }

    private String partElementsSinglePart(Node node, LinkedList<? extends Node> melody) {
        String toReturn = "";
        if(node instanceof CantusFirmusNode) {
            toReturn = partElement(melody, "CantusFirmus");
        }
        else if(node instanceof FirstSpeciesNode) {
            toReturn = partElement(melody, "FirstSpecies");
        }
        else if(node instanceof SecondSpeciesNode) {
            toReturn = partElement(melody, "SecondSpecies");
        }
        else if(node instanceof ThirdSpeciesNode) {
            toReturn = partElement(melody, "ThirdSpecies");
        }
        else if(node instanceof FourthSpeciesNode) {
            toReturn = partElement(melody, "FourthSpecies");
        }
        return toReturn;
    }

    /**
     *
     * @param nodes a list of nodes making up the melodic line for the given part
     * @param ID the part ID
     * @return all XML holding measure data for the given part
     */
    private String partElement(LinkedList<? extends Node> nodes, String ID) {
        String toReturn = "\t<part id=\"" + ID + "\">\n";
        fifthsValue = calculateFifths();
        toReturn += measures(nodes, false, 0);
        toReturn += partEnd;
        return toReturn;
    }

    /**
     * Modified from {@link #partElement(LinkedList, String)} for outputting many melodies from one part only.
     * @param nodes a list of melodies
     * @param ID the part ID
     * @return all XML holding measure data for the given part
     */
    private String partElementSinglePart(LinkedList<? extends LinkedList<? extends Node>> nodes, String ID) {
        String toReturn = "\t<part id=\"" + ID + "\">\n";
        fifthsValue = calculateFifths();
        int count = 1;
        for(LinkedList<? extends Node> melody : nodes) {
            toReturn += measures(melody,true, count);
            count++;
        }
        toReturn += partEnd;
        return toReturn;
    }

    //TODO: add changing clef based on range :(

    /**
     *
     * @param nodes a list of nodes making up the melodic line for the given part
     * @param newline true if outputting all melodies in a single type, else false
     * @param count which number melody this is
     * @return all measure XML for a part: from <measure number="1"> to the final </measure> closing the last measure
     */
    private String measures(LinkedList<? extends Node> nodes, boolean newline, int count) {
        String toReturn = "\t\t<measure number=\"1\">\n";
        if(newline) { toReturn += newSystem; }
        toReturn += "\t\t\t<attributes>\n" +
                "\t\t\t\t<divisions>1</divisions>\n" +
                "\t\t\t\t<key>\n";
        if(fifthsValue >= 7 || fifthsValue <= -7) {
            /*
            LinkedList<String> listOfAccidentalsOnLines = new LinkedList<String>();
            switch(fifthsValue) {
                case 13: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>E</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                case 12: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>A</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                case 11: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>D</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                case 10: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>G</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                case 9: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>C</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                case 8: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>F</key-step>\n" +
                            "\t\t\t\t\t<key-alter>2</key-alter>\n");
                break;
                case -13: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>C</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                case -12:  listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>G</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                case -11: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>D</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                case -10: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>A</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                case -9: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>E</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                case -8: listOfAccidentalsOnLines.addFirst("\t\t\t\t\t<key-step>B</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-2</key-alter>\n");
                break;
            }
            switch(fifthsValue) {
                case 8: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>C</key-step>\n" +
                            "\t\t\t\t\t<key-alter>1</key-alter>\n");
                case 9: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>G</key-step>\n" +
                            "\t\t\t\t\t<key-alter>1</key-alter>\n");
                case 10: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>D</key-step>\n" +
                        "\t\t\t\t\t<key-alter>1</key-alter>\n");
                case 11: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>A</key-step>\n" +
                        "\t\t\t\t\t<key-alter>1</key-alter>\n");
                case 12: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>E</key-step>\n" +
                        "\t\t\t\t\t<key-alter>1</key-alter>\n");
                case 13: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>B</key-step>\n" +
                        "\t\t\t\t\t<key-alter>1</key-alter>\n");
                break;
                case -8: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>E</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n");
                case -9: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>A</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n");
                case -10: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>D</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n");
                case -11: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>G</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n");
                case -12: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>C</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n");
                case -13: listOfAccidentalsOnLines.add("\t\t\t\t\t<key-step>F</key-step>\n" +
                        "\t\t\t\t\t<key-alter>-1</key-alter>\n" +
                        "\t\t\t\t\t<key-octave number=\"7\">4</key-octave>\n");
                    break;
            }
            for(String accidentalOnLine : listOfAccidentalsOnLines) {
                toReturn += accidentalOnLine;
            } */
            IO.mode = Mode.IONIAN;
            switch(fifthsValue) {
                case 8: fifthsValue = -4; IO.key = Key.Aflat; break;
                case 9: fifthsValue = -3; IO.key = Key.Eflat; break;
                case 10: fifthsValue = -2; IO.key = Key.Bflat; break;
                case 11, -13: fifthsValue = -1; IO.key = Key.F; break;
                case 12, -12: fifthsValue = 0; IO.key = Key.C; break;
                case 13, -11: fifthsValue = 1; IO.key = Key.G; break;
                case -10: fifthsValue = 2; IO.key = Key.D; break;
                case -9: fifthsValue = 3; IO.key = Key.A; break;
                case -8: fifthsValue = 4; IO.key = Key.E; break;
            }
        }
        toReturn += "\t\t\t\t\t<fifths>" + fifthsValue + "</fifths>\n" +
                    "\t\t\t\t\t<mode>" + IO.mode.toString().toLowerCase() + "</mode>\n" +
                    "\t\t\t\t</key>\n" +
                    "\t\t\t\t<time>\n" +
                    "\t\t\t\t\t<beats>4</beats>\n" +
                    "\t\t\t\t\t<beat-type>4</beat-type>\n" +
                    "\t\t\t\t</time>\n" +
                    "\t\t\t\t<clef>\n" +
                    "\t\t\t\t\t<sign>G</sign>\n" +
                    "\t\t\t\t\t<line>2</line>\n" +
                    "\t\t\t\t</clef>\n" +
                    "\t\t\t</attributes>\n";
        if(newline) { toReturn += labelStart + "Melody " + count + labelEnd; }
        toReturn += noteStart;
        String[] MIDItoSPNResult = MIDItoSPN(nodes.get(0).pitch);
        toReturn += "\t\t\t\t\t<step>" + MIDItoSPNResult[0] + "</step>\n";
        String accidental = MIDItoSPNResult[1];
        switch(accidental) {
            case "#": toReturn += "\t\t\t\t\t<alter>" + "1" + "</alter>\n"; break;
            case "##": toReturn += "\t\t\t\t\t<alter>" + "2" + "</alter>\n"; break;
            case "b": toReturn += "\t\t\t\t\t<alter>" + "-1" + "</alter>\n"; break;
            case "bb": toReturn += "\t\t\t\t\t<alter>" + "-2" + "</alter>\n"; break;
            case "": break;
        }
        toReturn += "\t\t\t\t\t<octave>" + MIDItoSPNResult[2] + "</octave>\n";
        toReturn += noteEnd;
        toReturn += measureEnd;
        for(int i = 1; i < IO.length; i++) {
            toReturn += "\t\t<measure number=\"" + (i + 1) + "\">\n";
            toReturn += noteStart;
            MIDItoSPNResult = MIDItoSPN(nodes.get(i).pitch);
            toReturn += "\t\t\t\t\t<step>" + MIDItoSPNResult[0] + "</step>\n";
            accidental = MIDItoSPNResult[1];
            switch(accidental) {
                case "#": toReturn += "\t\t\t\t\t<alter>" + "1" + "</alter>\n"; break;
                case "##": toReturn += "\t\t\t\t\t<alter>" + "2" + "</alter>\n"; break;
                case "b": toReturn += "\t\t\t\t\t<alter>" + "-1" + "</alter>\n"; break;
                case "bb": toReturn += "\t\t\t\t\t<alter>" + "-2" + "</alter>\n"; break;
                case "": break;
            }
            toReturn += "\t\t\t\t\t<octave>" + MIDItoSPNResult[2] + "</octave>\n";
            toReturn += noteEnd;
            if(i == IO.length - 1) {
                toReturn += endBar;
            }
            toReturn += measureEnd;
        }
        return toReturn;
    }

    /**
     * Given a key and mode, calculates how many flats or sharps there are.
     * @return the count of flats or sharps, with a negative sign added if flat
     */
    static int calculateFifths() {
        int toReturn = 0;
        switch(IO.key) {
            case Cflat: toReturn = -7; break;
            case C: break;
            case Csharp: toReturn = 7; break;
            case Dflat: toReturn = -5; break;
            case D: toReturn = 2; break;
            case Dsharp: toReturn = 9; break;
            case Eflat: toReturn = -3; break;
            case E: toReturn = 4; break;
            case Esharp: toReturn = 11; break;
            case Fflat: toReturn = -8; break;
            case F: toReturn = -1; break;
            case Fsharp: toReturn = 6; break;
            case Gflat: toReturn = -6; break;
            case G: toReturn = 1; break;
            case Gsharp: toReturn = 8; break;
            case Aflat: toReturn = -4; break;
            case A: toReturn = 3; break;
            case Asharp: toReturn = 10; break;
            case Bflat: toReturn = -2; break;
            case B: toReturn = 5; break;
            case Bsharp: toReturn = 12; break;
        }
        switch(IO.mode) {
            case IONIAN: break;
            case DORIAN: toReturn -= 2; break;
            case PHRYGIAN: toReturn -= 4; break;
            case LYDIAN: toReturn += 1; break;
            case MIXOLYDIAN: toReturn -= 1; break;
            case AEOLIAN: toReturn -= 3; break;
            case LOCRIAN: toReturn -= 5; break;
        }
        return toReturn;
    }

    /**
     * Converts a MIDI pitch value to Scientific Pitch Notation.
     * @param pitch MIDI pitch value
     * @return A string array of 3 items.
     * The 0th item is the note name without the accidental.
     * The 1st item is the accidental in simple ASCII: ##, #, b, or bb; an empty string if there is no accidental.
     * The 2nd item is the octave.
     * Given C#4, the 0th item is C, the 1st is #, the 2nd is 4.
     * Given B3, the 0th item is C, the 1st is the empty string, the 2nd is 3.
     */
    static String[] MIDItoSPN(int pitch) {
        String[] toReturn = new String[]{"", "", ""};
        switch(pitch % 12) {
            case 0:
                toReturn[0] = "C";
                if(fifthsValue == 7) {
                    toReturn[0] = "B";
                    toReturn[1] = "#";
                }
                break;
            case 1:
                if(fifthsValue <= -4) {
                    toReturn[0] = "D";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "C";
                    toReturn[1] = "#";
                }
                break;
            case 2:
                toReturn[0] = "D";
                break;
            case 3:
                if(fifthsValue <= -2) {
                    toReturn[0] = "E";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "D";
                    toReturn[1] = "#";
                }
                break;
            case 4:
                if(fifthsValue == -7) {
                    toReturn[0] = "F";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "E";
                }
                break;
            case 5:
                if(fifthsValue >= 6) {
                    toReturn[0] = "E";
                    toReturn[1] = "#";
                } else {
                    toReturn[0] = "F";
                }
                break;
            case 6:
                if(fifthsValue <= -5) {
                    toReturn[0] = "G";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "F";
                    toReturn[1] = "#";
                }
                break;
            case 7:
                toReturn[0] = "G";
                break;
            case 8:
                if(fifthsValue <= -3) {
                    toReturn[0] = "A";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "G";
                    toReturn[1] = "#";
                }
                break;
            case 9:
                toReturn[0] = "A";
                break;
            case 10:
                if(fifthsValue <= -1) {
                    toReturn[0] = "B";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "A";
                    toReturn[1] = "#";
                }
                break;
            case 11:
                if(fifthsValue <= -6) {
                    toReturn[0] = "C";
                    toReturn[1] = "b";
                } else {
                    toReturn[0] = "B";
                }
                break;
        }
        int secondArrayItem = ((pitch / 12) - 1);
        //readjust for edge case: a Cb equivalent to B4 is Cb5, a B# equivalent to C5 is B#4;
        //since the octave number comes from where an accidental-free note would be
        if(toReturn[0].equals("C") && toReturn[1].contains("b")) {
            secondArrayItem += 1;
        }
        if(toReturn[0].equals("B") && toReturn[1].contains("#")) {
            secondArrayItem -= 1;
        }
        toReturn[2] = Integer.toString(secondArrayItem);
        return toReturn;
    }
}

