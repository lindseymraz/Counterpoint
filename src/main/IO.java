import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class IO {

    private static LinkedList<LinkedList<CantusFirmusNode>> cantusFirmi;
    private static ArrayList<ArrayList<CantusFirmusNode>> columns;
    private static ArrayList<ArrayList<FirstSpeciesNode>> columnsFirstSpecies;
    static Composition composition;
    private static CantusFirmusNode start;
    private static CantusFirmusNode end;
    static LinkedList<CantusFirmusNode> cantusFirmus;

    private static Scanner keyboard = new Scanner(System.in);
    IO(){}

    IO(Mode mode, Key key){
        this.mode = mode;
        this.key = key;
    }

    private static boolean allowAllSixths;
    private static boolean debugMessagesOn;

    static Mode mode;
    static Key key;
    static int length; //8 to 16
    private static int upperBound; //the upper bound is allowed, nothing above it
    private static int lowerBound; //the lower bound is allowed, nothing below it
    static int tonic; //MIDI pitch value, not just pitch class
    static int climax; //MIDI pitch value, not just pitch class
    static int climaxEarlyBound; //earliest climax can occur. ex: if 3, climax can be 3rd note at earliest (this means 2nd column in array though)
    static int climaxLateBound; //latest climax can occur. ex: if 6, climax can be 6th note at latest (this means 5th column in array though)

    private static ArrayList<Integer> allLegalMoves = new ArrayList<Integer>();
    private static ArrayList<Integer> diatonicPitchClasses = new ArrayList<Integer>(7); //ranging from 0 to 11
    private static ArrayList<Integer> inRangeDiatonics = new ArrayList<Integer>();

    /**
     * Currently, all config values are set by setting the values in this function before running the script.
     * The end user will not have access to the source code to do this; this function should eventually be modified to
     * allow end user input.
     */
    private static void config() {
        allowAllSixths = false;
        CantusFirmusNode.allSixthsPrecedeFollowStepInOppDir = false;
        CantusFirmusNode.forceAtLeastTwoLeaps = true;
        debugMessagesOn = false;
        CantusFirmusNode.naturalSeventhAvoidsRaisedSeventh = false;
    }

    static void cantusFirmusInput() throws InvalidInputException {
        try {
            config();
            setUpAllLegalMoves();
            setMode();
            setKey();
            setDiatonicPitchClasses();
            System.out.println("Enter first bound as a MIDI note number, no higher than 127, no lower than 0.\nYou will be asked for a second bound after this. Feel free to input either an upper or lower bound.");
            int bound1 = Integer.parseInt(keyboard.next());
            boundOK(bound1);
            System.out.println("Enter second bound as a MIDI note number, no higher than 127, no lower than 0");
            int bound2 = Integer.parseInt(keyboard.next());
            boundOK(bound2);
            setBoundsTonicClimax(bound1, bound2);
            setLength();
            climaxPosPicker();
            setPenultPos();
            setInRangeDiatonics();
            setColumns();
            makeGetsTo();
            choosePostGenerationMove(output(start,end));
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            clearCantusFirmusInputException();
            cantusFirmusInput();
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    static void firstSpeciesInput() throws InvalidInputException {
        try {
            selectCounterpointAboveOrBelow();
        } catch(InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            firstSpeciesInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUpAllLegalMoves() {
        allLegalMoves.add(-Interval.octave.distance); //descend octave
        allLegalMoves.add(-Interval.perfectFifth.distance); //descend P5
        allLegalMoves.add(-Interval.perfectFourth.distance); //descend P4
        allLegalMoves.add(-Interval.majorThird.distance); //descend major third
        allLegalMoves.add(-Interval.minorThird.distance); //descend minor third
        allLegalMoves.add(-Interval.majorSecond.distance); //descend major second
        allLegalMoves.add(-Interval.minorSecond.distance); //descend minor second
        allLegalMoves.add(Interval.minorSecond.distance); //ascend minor second
        allLegalMoves.add(Interval.majorSecond.distance); //ascend major second
        allLegalMoves.add(Interval.minorThird.distance); //ascend minor third
        allLegalMoves.add(Interval.majorThird.distance); //ascend major third
        allLegalMoves.add(Interval.perfectFourth.distance); //ascend P4
        allLegalMoves.add(Interval.perfectFifth.distance); //ascend P5
        allLegalMoves.add(Interval.minorSixth.distance); //ascend minor sixth
        allLegalMoves.add(Interval.octave.distance); //ascend octave
        if(allowAllSixths) {
            allLegalMoves.add(-Interval.majorSixth.distance); //descend major sixth
            allLegalMoves.add(-Interval.minorSixth.distance); //descend minor sixth
            allLegalMoves.add(Interval.majorSixth.distance); //ascend major sixth
        }
        if(debugMessagesOn) {
            printAllLegalMoves();
        }
    }

    private static void setMode() throws InvalidInputException {
        System.out.println("Enter mode.\n1 for Ionian\n2 for Dorian\n3 for Phrygian\n4 for Lydian\n5 for Mixolydian\n6 for Aeolian\n7 for Locrian");
        String input = (keyboard.next());
        switch(Integer.parseInt(input)) {
            case 1: mode = Mode.IONIAN; break;
            case 2: mode = Mode.DORIAN; break;
            case 3: mode = Mode.PHRYGIAN; break;
            case 4: mode = Mode.LYDIAN; break;
            case 5: mode = Mode.MIXOLYDIAN; break;
            case 6: mode = Mode.AEOLIAN; break;
            case 7: mode = Mode.LOCRIAN; break;
            default: throw new InvalidInputException(input, " is not an integer between 1 and 7");
        }
    }

    private static void setKey() throws InvalidInputException {
        System.out.println("Enter key.\ne.g. C, D#, Eb");
        String input = (keyboard.next());
        if(input.matches("^[A-G]{1}[b#]?$")) {
            key = Key.getKeyFromString(input);
        } else {
            throw new InvalidInputException((input), " is not a valid input.");
        }
    }

    //starting with the tonic, add steps (from mode.steps) needed to jump to the next diatonic note.
    //the % 12 ensures the values are 0-11
    static void setDiatonicPitchClasses() {
        diatonicPitchClasses.add(key.offset); //adds tonic
        int hop = key.offset;
        for(int i = 0; i < 6; i++) { //not using element-based for because you need to avoid the last
            hop = (hop + mode.steps.get(i)) % 12; //item in mode.steps, which is ti2Do.
            diatonicPitchClasses.add(hop);
        }
        if(debugMessagesOn) {
            printDiatonicPitchClasses();
        }
    }

    private static void printDiatonicPitchClasses() {//for debugging and testing
        String str = "";
        for(Integer i : diatonicPitchClasses) {
            str = str + i + " ";
        }
        System.out.println(str);
    }

    private static void boundOK(int input) throws InvalidInputException {
        if(input < 0) {
            throw new InvalidInputException(Integer.toString(input), " is less than 0");
        } if(input > 127) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 127");
        } if(!isDiatonic(input)) {
            throw new InvalidInputException(Integer.toString(input), " is not diatonic");
        }
    }

    static boolean isDiatonic(int pitch) {
        return diatonicPitchClasses.contains(pitch%12);
    }

    private static void setBoundsTonicClimax(int input1, int input2) throws InvalidInputException {
        int distance = input1 - input2;
        int upper = input1;
        int lower = input2;
        if(input2 > input1) {
            distance = input2 - input1;
            upper = input2;
            lower = input1;
        }
        switch(mode) {
            case IONIAN, LYDIAN, MIXOLYDIAN: intervalSizeOK(distance, 4, 16, "major"); break;
            case DORIAN, PHRYGIAN, AEOLIAN, LOCRIAN: intervalSizeOK(distance, 3, 15, "minor"); break;
        }
        setTonic(upper, lower);
        upperBound = upper;
        lowerBound = lower;
        if(debugMessagesOn) {
            System.out.println("Tonic is " + tonic + "\nClimax is " + climax);
        }
    }

    private static void intervalSizeOK(int distance, int smallest, int greatest, String quality) throws InvalidInputException {
        if(distance < smallest) {
            throw new InvalidInputException(Integer.toString(distance), " is distance between bounds, interval must span at least a " + quality + " third");
        }
        if(distance > greatest) {
            throw new InvalidInputException(Integer.toString(distance), " is distance between bounds, interval must not exceed a " + quality + " tenth");
        }
    }

    private static void setTonic(int upper, int lower) throws InvalidInputException {
        LinkedList<Integer> tonics = findTonics(upper, lower);
        int tonicToUpperDist = (upper - tonics.get(0));
        int tonicToLowerDist = (tonics.get(0) - lower);
        boolean upperTo1DistBigger = false;
        boolean lowerTo1DistBigger = false;
        if(tonics.size() == 2) {
            if ((upper - tonics.get(1)) > tonicToUpperDist) {
                tonicToUpperDist = (upper - tonics.get(1));
                upperTo1DistBigger = true;
            }
            if ((tonics.get(1) - lower) > tonicToLowerDist) {
                tonicToLowerDist = (tonics.get(1) - lower);
                lowerTo1DistBigger = true;
            }
        }
        if(tonicToUpperDist == tonicToLowerDist) {
            isClimaxInappropriate(tonicToUpperDist); //the error message this throws isn't too helpful if you get here
            System.out.println("The program picks the climax to be the note furthest from the tonic." + "\nBoth the top and bottom note are the same distance from the tonic." + "\nPlease select whether you would like the lower or upper bound to be your tonic. Enter 0 for lower, 1 for upper.");
            String input = (keyboard.next());
            switch (Integer.parseInt(input)) {
                case 0:
                    climax = lower;
                    tonic = tonics.get(0);
                    break;
                case 1:
                    climax = upper;
                    tonic = tonics.get(0);
                    break;
                default:
                    throw new InvalidInputException((input), " is not 0 or 1");
            }
        }
        if(tonicToUpperDist > tonicToLowerDist) {
            isClimaxInappropriate(tonicToUpperDist);
            climax = upper;
            tonic = tonics.get(0);
            if(upperTo1DistBigger) { tonic = tonics.get(1); }
        } else {
            isClimaxInappropriate(tonicToLowerDist);
            climax = lower;
            tonic = tonics.get(0);
            if(lowerTo1DistBigger) { tonic = tonics.get(1); }
        }
    }

    private static LinkedList<Integer> findTonics(int upper, int lower) throws InvalidInputException {
        int hop = lower;
        boolean alreadyFoundATonic = false;
        LinkedList<Integer> tonics = new LinkedList<Integer>();
        int counter = diatonicPitchClasses.indexOf(hop%12);
        while(hop <= upper) {
            if((hop%12) == key.offset) {
                tonics.add(hop);
                if(alreadyFoundATonic) { return tonics; }
                alreadyFoundATonic = true;
            }
            hop+=mode.steps.get(counter);
            counter+=1;
            if(counter == 7) { counter = 0; }
        } if(tonics.size() == 0) { throw new InvalidInputException("", "Did not find a tonic"); } return tonics;
    }

    private static void isClimaxInappropriate(int pitch) throws InvalidInputException {
        if(((key.offset + 12) - 1) == (pitch%12)) { throw new InvalidInputException("", "Climax must not be ti"); }
    }

    private static void setLength() throws InvalidInputException {
        System.out.println("Enter length in notes. 8 minimum, 16 maximum");
        int input = Integer.parseInt(keyboard.next());
        if(input < 8) {
            throw new InvalidInputException(Integer.toString(input), " is less than 8");
        }
        if(input > 16) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 16");
        } length = input;
        if(debugMessagesOn) {
            System.out.println("Length is " + length + " notes");
        }
    }

    private static void climaxPosPicker() {
        climaxEarlyBound = (int)((Math.floor(length*.33)) + 1);
        climaxLateBound = (int)(Math.round(length*.66));
        if(debugMessagesOn) {
            System.out.println("Early bound is " + climaxEarlyBound + ", late bound is " + climaxLateBound);
        }
    }

    private static void setPenultPos() {
        Node.penultPos = (length - 2);
    } //equal to which column has the penultimate note

    private static void setInRangeDiatonics() {
        for(int i = lowerBound; i <= upperBound; i++) {
            if (isDiatonic(i)) {
                inRangeDiatonics.add(i);
            }
        }
    }

    private static void setColumns() {
        columns = new ArrayList<ArrayList<CantusFirmusNode>>(length);
        ArrayList<CantusFirmusNode> firstColumn = new ArrayList<CantusFirmusNode>();
        start = new CantusFirmusNode(tonic);
        firstColumn.add(start);
        columns.add(firstColumn);
        for(int i = 1; i < (climaxEarlyBound - 1); i++) { //you already added column 0 at the beginning, start at column 1. earliest climax may appear is the cEBrd note, that counts starting at 1 so adjust for earliest column, stop before you hit it
            ArrayList<CantusFirmusNode> a = new ArrayList<CantusFirmusNode>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                if(inRangeDiatonics.get(j) != climax) { a.add(new CantusFirmusNode(inRangeDiatonics.get(j))); } //add everything but the climax because it can't appear yet
            }
            columns.add(a);
        }
        for(int i = (climaxEarlyBound - 1); i < (climaxLateBound); i++) { //you're at the cEBrd note, so you can add the climax, stop before you hit the first thing after cLB's column, which is cLB - 1
            ArrayList<CantusFirmusNode> b = new ArrayList<CantusFirmusNode>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                b.add(new CantusFirmusNode(inRangeDiatonics.get(j)));
            }
            columns.add(b);
        }
        for(int i = climaxLateBound; i < (length - 1); i++) { //you're past the cLBth note (which was in column cLB - 1 and right now you're in column cLB), go back to not adding climax. stop before you hit last column
            ArrayList<CantusFirmusNode> c = new ArrayList<CantusFirmusNode>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                if(inRangeDiatonics.get(j) != climax) { c.add(new CantusFirmusNode(inRangeDiatonics.get(j))); }
            }
            columns.add(c);
        }
        ArrayList<CantusFirmusNode> lastColumn = new ArrayList<CantusFirmusNode>();
        end = new CantusFirmusNode(tonic);
        lastColumn.add(end);
        columns.add(lastColumn);
        switch(mode) { //if 678 doesn't look major with your diatonic notes, add in nodes required to make it look major.
            case PHRYGIAN, AEOLIAN, LOCRIAN: if (isInRange(tonic - 3)) { columns.get(length - 3).add(new CantusFirmusNode(tonic - 3)); } //intentionally doesn't break because these modes also need to do the step listed in the next case. this one raises the 6
            case DORIAN, MIXOLYDIAN: if (isInRange(tonic - 1)) { columns.get(length - 2).add(new CantusFirmusNode(tonic - 1)); } //raises the 7
            default: break;
        }
    }

    private static void makeGetsTo() {
        oneColumnGetsToNoClimax(start, 0);
        for (int j = 1; j < (climaxEarlyBound - 2); j++) { //already did first column. column cEB - 1 is first with climax nodes, so stop before you do cEB - 2, else you'd omit possible connections
            for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) { //to climax nodes
                oneColumnGetsToNoClimax(columns.get(j).get(k), j);
            }
        }
        for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) { //just one column, because it's column-minus-climax mapping onto column-with-climax, with climax has one more node than your start
            oneColumnGetsTo(columns.get((climaxEarlyBound) - 2).get(k), ((climaxEarlyBound) - 2));
        }
        for (int j = (climaxEarlyBound - 1); j < (climaxLateBound - 1); j++) { //columns with climax connect to other columns with climax
            for (int k = 0; k < inRangeDiatonics.size(); k++) {
                oneColumnGetsTo(columns.get(j).get(k), j);
            }
        }
        for (int k = 0; k < inRangeDiatonics.size(); k++) { //just one column: column with climax connects to column without climax (has one less node than column with climax)
            oneColumnGetsToNoClimax(columns.get((climaxLateBound) - 1).get(k), ((climaxLateBound) - 1));
        }
        for (int j = (climaxLateBound); j < (length - 2); j++) { //columns without climaxes connect to same size things
            for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) {
                oneColumnGetsToNoClimax(columns.get(j).get(k), j); //this can include prepenult and connections to penult, which may have one more note (their raised 6 or 7) than the other nonclimax nodes, how do we know these are getting all their
            }//diatonics connected and not accidentally touching the nondiatonics? columns.get(j).get(k), k will only go as high as the inrangediatonics indices (all the extra notes have their index higher than the last diatonic)
        }
        switch(mode) { //this all dealt with inRangeDiatonics.size, not connecting any nondiatonics. only nondiatonics are raised 7 and 6 at the end, let's make sure they connect
            case PHRYGIAN, AEOLIAN, LOCRIAN: if(isInRange(tonic - 3)) { //if raised 6 in range we put it down so it's ok to look for it
                for (int l = 0; l < columns.get(length - 3).size(); l++) { //find the raised 6 node
                    if (columns.get(length - 3).get(l).pitch == (tonic - 3)) {
                        CantusFirmusNode laNode = columns.get(length - 3).get(l); //found the raised 6, set laNode to it
                        for (int m = 0; m < columns.get(length - 2).size(); m++) {
                            if(columns.get(length - 2).get(m).pitch == (tonic - 1)) {
                                laNode.addEdge(columns.get(length - 2).get(m)); //raised 6th always will go to raised 7th
                            }
                        }
                        oneColumnGetsToSpecificNote(columns.get(length - 4).size(), 4, (tonic - 3), laNode); //connect the stuff preceding raised 6's column to raised 6
                    }
                }
            }
            case DORIAN, MIXOLYDIAN: if(isInRange(tonic - 1)) { //if raised 7 in range we put it down so it's ok to look for it
                for (int q = 0; q < columns.get(length - 2).size(); q++) { //find raised 7
                    if(columns.get(length - 2).get(q).pitch == (tonic - 1)) {
                        CantusFirmusNode tiNode = columns.get(length - 2).get(q); //found raised 7, set it to tiNode
                        oneColumnGetsToSpecificNote(columns.get(length - 3).size(), 3, (tonic - 1), tiNode); //connect stuff preceding raised 7's column to raised 7
                    }
                }
            }
            default: break;
        }
        for (int l = 0; l < columns.get(length - 2).size(); l++) { //penultimate column (column without climax) connects to tonic-only column, works for any size penultimate column
            if ((columns.get(length - 2).get(l).pitch == (tonic + mode.steps.get(0))) || (columns.get(length - 2).get(l).pitch == (tonic - 1))) { //if it's re or ti
                columns.get(length - 2).get(l).addEdge(end); //connect it to the end
            }
        }
    }

    private static void oneColumnGetsTo(CantusFirmusNode node, int currColumn) { //there's this and getsToNoClimax to accommodate for the column sizes differing. use this if the climax node is actually in that column
        for(Integer i : allLegalMoves) {
            if (inRangeDiatonics.contains(node.pitch + i)) {
                node.addEdge(columns.get(currColumn + 1).get(inRangeDiatonics.indexOf(node.pitch + i)));
            }
        }
    }

    private static void oneColumnGetsToNoClimax(CantusFirmusNode node, int currColumn) { //use if the climax node isn't in the column. notes are added to columns in the order of inRangeDiatonics, so usually the index of the note in inRangeDiatonics
        for(Integer i : allLegalMoves) { //matches it in the columns, but here the climax isn't in the columns but still is in inRangeDiatonics so depending on where the climax is, some notes may have shifted indices from where they originally were, ruining
            if (((node.pitch + i) != climax) && (inRangeDiatonics.contains(node.pitch + i))) { //anything working on this assumption, so adjust how you grab indices.
                int sub = 0;
                if (inRangeDiatonics.indexOf(node.pitch + i) > inRangeDiatonics.indexOf(climax)) {
                        sub = 1;
                    }
                node.addEdge(columns.get(currColumn + 1).get((inRangeDiatonics.indexOf(node.pitch + i)) - sub));
            }
        }
    }

    private static void oneColumnGetsToSpecificNote(int lim, int sub, int note, CantusFirmusNode addEdgeTo) {
        for(int j = 0; j < lim; j++) {
            for(Integer i : allLegalMoves) {
                if((columns.get(length - sub).get(j).pitch + i) == note) {
                    columns.get(length - sub).get(j).addEdge(addEdgeTo);
                }
            }
        }
    }

    private static boolean isInRange(int pitch) {
        return((pitch >= lowerBound) && (pitch <= upperBound));
    }

    private static void printAllLegalMoves() { //for debugging and testing!
        String str = "";
        for(int i = 0; i < allLegalMoves.size(); i++) {
            str = str + (allLegalMoves.get(i)) + " ";
        }
        System.out.println(str);
    }

    protected static String writeFirstSpecies(LinkedList<FirstSpeciesNode> line, int acc) {
        int size = (line.size() - 1);
        String str = (acc + ", ");
        for(int i = 0; (i < size); i++) {
            str = str + (line.get(i).pitch + " ");
        }
        return(str + (line.get(size).pitch + ";\n"));
    }

    private static LinkedList<LinkedList<CantusFirmusNode>> output(CantusFirmusNode from, CantusFirmusNode to) throws IOException {
        cantusFirmi = new LinkedList<LinkedList<CantusFirmusNode>>();
        from.giveRoute(to, new LinkedList<CantusFirmusNode>(), cantusFirmi);
        int size = cantusFirmi.size();
        switch(size) {
            case 0: System.out.println("Could not generate any cantus firmi with the given parameters :("); break;
            case 1: System.out.println(size + " cantus firmus generated!"); break;
            default: System.out.println(size + " cantus firmi generated!");
        }
        return cantusFirmi;
    }

    private static void clearCantusFirmusInput() {
        allLegalMoves.clear();
        diatonicPitchClasses.clear();
        inRangeDiatonics.clear();
        columns.clear();
    }

    private static void clearCantusFirmusInputException() {
        allLegalMoves.clear();
        diatonicPitchClasses.clear();
        inRangeDiatonics.clear();
    }

    private static void choosePostGenerationMove(LinkedList<? extends LinkedList<? extends Node>> melodies) throws InvalidInputException, IOException {
        System.out.print("Enter 0 to quit, 1 to start again with new parameters");
        int size = melodies.size();
        if(size > 0) {
            System.out.println(", 2 to proceed to first species without exporting to a file,\nor 3 to export to a file before proceeding.");
        } else {
            System.out.println(".");
        }
        try {
            int choose = Integer.parseInt(keyboard.next());
            switch (choose) {
                case 0: break;
                case 1: clearCantusFirmusInput();cantusFirmusInput();
                case 2: if (size != 0) { composition = new Composition((LinkedList<CantusFirmusNode>) selectAMelody(size), null, null, null, null); firstSpeciesInput();}
                case 3: if (size != 0) { export(size, melodies, false);}
                default:
                    throw new InvalidInputException(Integer.toString(choose), " is not an accepted input.");
            }
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            choosePostGenerationMove(melodies);
        }
    }

    static void export(int size, LinkedList<? extends LinkedList<? extends Node>> melodies, boolean selectedNewMelody) throws InvalidInputException {
        IFileExport saver;
        System.out.println("Enter 0 to export to a MusicXML file, or 1 to export to a CSV file.");
        int whereToSave = Integer.parseInt(keyboard.next());
        switch(whereToSave) {
            case 0: saver = new OutputMusicXML(); break;
            case 1: saver = new OutputCSV(); break;
            default:
                throw new InvalidInputException(Integer.toString(whereToSave), " is not 0 or 1.");
        }
        System.out.println("Enter 0 to export a single melody, 1 to export all of the just-generated melodies, or 2 to export the entire composition up to this point.");
        int whatToSave = Integer.parseInt(keyboard.next());
        try {
            switch (whatToSave) {
                case 0: saver.outputSingleMelody(nameFile(), selectAMelody(size)); break;
                case 1: saver.outputAllMelodicOptions(nameFile(), melodies); break;
                case 2: updateComposition(selectedNewMelody, size);
                    selectedNewMelody = true;
                    saver.outputAllCounterpoint(nameFile()); break;
                default: throw new InvalidInputException(Integer.toString(whatToSave), " is not 0, 1, or 2.");
            }
        } catch (IOException e) {
            System.out.println("File could not be output.");
            throw new RuntimeException(e);
        }
        System.out.println("File successfully created! Enter 0 to exit, 1 to output to another file, or 2 to move on to the next species.");
        int next = Integer.parseInt(keyboard.next());
        switch(next) {
            case 0: break;
            case 1: export(size, melodies, selectedNewMelody);
            case 2: updateComposition(selectedNewMelody, size); firstSpeciesInput();
            default:
                throw new InvalidInputException(Integer.toString(next), " is not 0, 1, or 2.");
        }
    }

    private static void updateComposition(boolean selectedNewMelody, int size) throws InvalidInputException {
        if(!selectedNewMelody) {
            composition = new Composition((LinkedList<CantusFirmusNode>) selectAMelody(size), null, null, null, null);
        }
    }

    private static LinkedList<? extends Node> selectAMelody(int size) throws InvalidInputException {
        try {
            LinkedList<? extends Node> selectedMelody = cantusFirmi.get(0);
            int input = 1;
            if(size > 1) {
                System.out.println("Select a generated melody. Options are from 1 to " + size + ".");
                input = Integer.parseInt(keyboard.next());
                if(input < 1) {
                    throw new InvalidInputException(Integer.toString(input), " is below 1");
                } if(input > size) {
                    throw new InvalidInputException(Integer.toString(input), " is above " + size);
                } else {
                    selectedMelody = cantusFirmi.get(input - 1);
                }
            } System.out.println("The melody that will be used is " + input + ", whose notes are:");
            String str = "";
            for(int i = 0; (i < selectedMelody.size()); i++) {
                str = str + (selectedMelody.get(i).pitch + " ");
            }
            System.out.println(str);
            if(size > 1) {
                System.out.println("Is this acceptable? Enter 1 for yes, 0 to select a different melody.");
                int acceptable = Integer.parseInt(keyboard.next());
                switch (acceptable) {
                    case 0: selectAMelody(size); break;
                    case 1:
                        cantusFirmus = (LinkedList<CantusFirmusNode>) selectedMelody; break;
                    default:
                        throw new InvalidInputException(Integer.toString(acceptable), " is not 1 or 0");
                }
            }
            return selectedMelody;
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            return selectAMelody(size);
        }
    }

    private static String nameFile() throws InvalidInputException {
        System.out.println("Type the name you would like the file to be, using only alphanumeric characters, and under 256 characters.");
        String fileName = keyboard.next();
        if(!fileName.matches("^[A-Za-z0-9]+$")) {
            return nameFile();
        } else {
            return fileName;
        }
    }

    private static void selectCounterpointAboveOrBelow() throws InvalidInputException, IOException {
        System.out.println("Enter 0 to put the counterpoint above the cantus firmus; 1 to put the counterpoint below the cantus firmus.");
        int input = Integer.parseInt(keyboard.next());
        switch(input) {
            case 0:
                output(0);
                break;
            case 1: output(1);
            break;
            default: throw new InvalidInputException(Integer.toString(input), " is not 1 or 0");
        }
    }

    private static int outputHelper(FirstSpeciesNode from, FirstSpeciesNode to) throws IOException {
        LinkedList<LinkedList<FirstSpeciesNode>> firstSpeciesLines = new LinkedList<LinkedList<FirstSpeciesNode>>();
        from.giveRoute(to, new LinkedList<FirstSpeciesNode>(), firstSpeciesLines);
        int acc = 1;
            for(LinkedList<FirstSpeciesNode> firstSpeciesLine : firstSpeciesLines) {
                System.out.print(writeFirstSpecies(firstSpeciesLine, acc));
                acc++;
            }
        int size = firstSpeciesLines.size();
        return size;
    }

    private static void setColumnsFirstSpecies(boolean CTPAbove) {
        columnsFirstSpecies = new ArrayList<ArrayList<FirstSpeciesNode>>(length);
        ArrayList<FirstSpeciesNode> firstColumn = new ArrayList<FirstSpeciesNode>();
        if(CTPAbove) {
            firstColumn.add(new FirstSpeciesNode(start.pitch)); //unison
            firstColumn.add(new FirstSpeciesNode(start.pitch + Interval.perfectFifth.distance)); //P5
            firstColumn.add(new FirstSpeciesNode(start.pitch + Interval.octave.distance)); //P8
        } else {
            firstColumn.add(new FirstSpeciesNode(start.pitch)); //unison
            firstColumn.add(new FirstSpeciesNode(start.pitch - Interval.octave.distance)); //P8
        }
        columnsFirstSpecies.add(firstColumn);
        for(int i = 1; i < length - 2; i++) {
            ArrayList<FirstSpeciesNode> aColumn = new ArrayList<FirstSpeciesNode>(); //assumes max range tenth
            int currCantusFirmusPitch = cantusFirmus.get(i).pitch;
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.minorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.minorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.majorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.majorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.perfectFifth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.perfectFifth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.minorSixth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.minorSixth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.majorSixth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.majorSixth.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.octave.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - Interval.octave.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.octave.distance + Interval.minorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - (Interval.octave.distance + Interval.minorThird.distance)));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch + Interval.octave.distance + Interval.majorThird.distance));
            aColumn.add(new FirstSpeciesNode(currCantusFirmusPitch - (Interval.octave.distance + Interval.majorThird.distance)));
            columnsFirstSpecies.add(aColumn);
        }
        ArrayList<FirstSpeciesNode> penultimateColumn = new ArrayList<FirstSpeciesNode>();
        //refactor to actually fit rule in case cantus firmus ends any way different than 2 1
        penultimateColumn.add(new FirstSpeciesNode(mode.steps.get(6) + end.pitch)); //to go with U ending
        penultimateColumn.add(new FirstSpeciesNode(mode.steps.get(6) + end.pitch + 12)); //to go with octave + ending
        penultimateColumn.add(new FirstSpeciesNode(mode.steps.get(6) + end.pitch - 12)); //to go with octave - ending
        columnsFirstSpecies.add(penultimateColumn);
        ArrayList<FirstSpeciesNode> finalColumn = new ArrayList<FirstSpeciesNode>();
        finalColumn.add(new FirstSpeciesNode(end.pitch)); //U
        finalColumn.add(new FirstSpeciesNode(end.pitch + 12)); //octave
        finalColumn.add(new FirstSpeciesNode(end.pitch - 12)); //octave
        columnsFirstSpecies.add(finalColumn);
    }

    private static void makeGetsToFirstSpecies() {
        for(int i = 0; i < length - 2; i++) {
            for(FirstSpeciesNode node : columnsFirstSpecies.get(i)) {
                for(FirstSpeciesNode nextColumnNode : columnsFirstSpecies.get(i + 1)) {
                    node.getsTo.add(nextColumnNode);
                }
            }
        }
        for(int i = 0; i < 3; i++) { //funnily enough, item x of penultimate column goes with item x of ultimate column
            columnsFirstSpecies.get(length - 2).get(i).getsTo.add((columnsFirstSpecies.get(length-1).get(i)));
        }
    }

    /**
     *
     * @param aboveOrBelow 0 is above, 1 is below
     * @return
     */
    private static void output(int aboveOrBelow) throws IOException {
        int size = 0;
        if(aboveOrBelow==0) {
            setColumnsFirstSpecies(true);
            makeGetsToFirstSpecies();
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(0));
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(1));
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(2));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(0));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(1));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(2));
            size += outputHelper(columnsFirstSpecies.get(0).get(2), columnsFirstSpecies.get(length - 1).get(0));
            size += outputHelper(columnsFirstSpecies.get(0).get(2), columnsFirstSpecies.get(length - 1).get(1));
            size += outputHelper(columnsFirstSpecies.get(0).get(2), columnsFirstSpecies.get(length - 1).get(2));
            //add combos of P1, P5, P8 above cantus tonic + P1 or P8 above cantus end, may be messy, cite
        } else {
            setColumnsFirstSpecies(false);
            makeGetsToFirstSpecies();
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(0));
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(1));
            size += outputHelper(columnsFirstSpecies.get(0).get(0), columnsFirstSpecies.get(length - 1).get(2));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(0));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(1));
            size += outputHelper(columnsFirstSpecies.get(0).get(1), columnsFirstSpecies.get(length - 1).get(2));
            //add combos of P1, P8 below cantus tonic + P1 or P8 below cantus end, may be messy, cite me
        }
        switch(size) {
            case 0: System.out.println("Could not generate any first species lines with the given parameters :("); break;
            case 1: System.out.println(size + " first species line generated!"); break;
            default: System.out.println(size + " first species lines generated!");
        }
    }
}