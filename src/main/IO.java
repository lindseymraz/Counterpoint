import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class IO {

    private static ArrayList<ArrayList<Node>> columns;
    private static Node start;
    private static Node end;

    private static Scanner keyboard = new Scanner(System.in);
    IO(){}

    private static boolean allowAllSixths;
    private static boolean writeToFile;
    private static boolean debugMessagesOn;

    private static Mode mode;
    private static int key; //0 is C, 11 is B
    private static int length; //8 to 16
    private static int upperBound; //the upper bound is allowed, nothing above it
    private static int lowerBound; //the lower bound is allowed, nothing below it
    private static int tonic; //the actual pitch value, stuff like 60, not just pitch class
    static int climax; //actual pitch value, not just pitch class
    static int climaxEarlyBound; //earliest climax can occur
    static int climaxLateBound; //latest climax can occur

    private static ArrayList<Integer> allLegalMoves = new ArrayList<Integer>();
    private static ArrayList<Integer> diatonicPitchClasses = new ArrayList<Integer>(7); //ranging from 0 to 11
    private static ArrayList<Integer> inRangeDiatonics = new ArrayList<Integer>();

    private static void config() {
        allowAllSixths = false;
        Node.allSixthsPrecedeFollowStepInOppDir = false;
        Node.forceAtLeastTwoLeaps = true;
        writeToFile = true;
        debugMessagesOn = true;
    }

    static void input() throws InvalidInputException {
        try {
            config();
            setUpAllLegalMoves();
            if(debugMessagesOn) {
                printAllLegalMoves();
            }
            System.out.println("Enter mode.\n1 for Ionian\n2 for Dorian\n3 for Phrygian\n4 for Lydian\n5 for Mixolydian\n6 for Aeolian\n7 for Locrian");
            setMode(Integer.parseInt(keyboard.next()));
            System.out.println("Enter key.\n0 for C, 11 for B, no higher or lower");
            setKey(Integer.parseInt(keyboard.next()));
            setDiatonicPitchClasses();
            if(debugMessagesOn) {
                printDiatonicPitchClasses();
            }
            System.out.println("Enter bound 1, no higher than 127, no lower than 0");
            int bound1 = Integer.parseInt(keyboard.next());
            boundOK(bound1);
            System.out.println("Enter bound 2, no higher than 127, no lower than 0");
            int bound2 = Integer.parseInt(keyboard.next());
            boundOK(bound2);
            setBoundsTonicClimax(bound1, bound2);
            if(debugMessagesOn) {
                System.out.println("Tonic is " + tonic + "\nClimax is " + climax);
            }
            System.out.println("Enter length. 8 min, 16 max");
            setLength(Integer.parseInt(keyboard.next()));
            if(debugMessagesOn) {
                System.out.println("Length is " + length);
            }
            climaxPosPicker();
            setPenultPos();
            if(debugMessagesOn) {
                System.out.println("Early bound is " + climaxEarlyBound + ", late bound is " + climaxLateBound);
            }
            setInRangeDiatonics();
            setColumns();
            makeGetsTo();
            output(start, end);
            chooseReset();
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            clearInputException();
            input();
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    private static void setUpAllLegalMoves() {
        allLegalMoves.add(-12); //descend octave
        allLegalMoves.add(-7); //descend P5
        allLegalMoves.add(-5); //descend P4
        allLegalMoves.add(-4); //descend major third
        allLegalMoves.add(-3); //descend minor third
        allLegalMoves.add(-2); //descend major second
        allLegalMoves.add(-1); //descend minor second
        allLegalMoves.add(1); //ascend minor second
        allLegalMoves.add(2); //ascend major second
        allLegalMoves.add(3); //ascend minor third
        allLegalMoves.add(4); //ascend major third
        allLegalMoves.add(5); //ascend P4
        allLegalMoves.add(7); //ascend P5
        allLegalMoves.add(8); //ascend minor sixth
        allLegalMoves.add(12); //ascend octave
        if(allowAllSixths) {
            allLegalMoves.add(-9); //descend major sixth
            allLegalMoves.add(-8); //descend minor sixth
            allLegalMoves.add(9); //ascend major sixth
        }
    }

    private static void setMode(int input) throws InvalidInputException {
        switch(input) {
            case 1: mode = Mode.IONIAN; break;
            case 2: mode = Mode.DORIAN; break;
            case 3: mode = Mode.PHRYGIAN; break;
            case 4: mode = Mode.LYDIAN; break;
            case 5: mode = Mode.MIXOLYDIAN; break;
            case 6: mode = Mode.AEOLIAN; break;
            case 7: mode = Mode.LOCRIAN; break;
            default: throw new InvalidInputException(Integer.toString(input), " is not an integer between 1 and 7");
        }
    }

    private static void setKey(int input) throws InvalidInputException {
        switch(input) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11: key = input; break;
            default: throw new InvalidInputException(Integer.toString(input), " is not an integer between 0 and 11");
        }
    }

    //starting with the tonic, add steps (from mode.steps) needed to jump to the next diatonic note.
    //the % 12 ensures the values are 0-11
    private static void setDiatonicPitchClasses() {
        diatonicPitchClasses.add(key); //adds tonic
        int hop = key;
        for(int i = 0; i < 6; i++) { //not using element-based for because you need to avoid the last
            hop = (hop + mode.steps.get(i)) % 12; //item in mode.steps, which is ti2Do.
            diatonicPitchClasses.add(hop);
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

    private static boolean isDiatonic(int pitch) {
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
        int whichTonic = 0;
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
        if(tonicToUpperDist == tonicToLowerDist) { throw new InvalidInputException("", "Is bottom or top climax? It's equidistant, unclear"); }
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
            if((hop%12) == key) {
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
        if(((key + 12) - 1) == (pitch%12)) { throw new InvalidInputException("", "Climax must not be ti"); }
    }

    private static void setLength(int input) throws InvalidInputException {
        if(input < 8) {
            throw new InvalidInputException(Integer.toString(input), " is less than 8");
        }
        if(input > 16) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 16");
        } length = input;
    }

    private static void climaxPosPicker() {
        climaxEarlyBound = (int)((Math.floor(length*.33)) + 1);
        climaxLateBound = (int)(Math.round(length*.66));
    }

    private static void setPenultPos() {
        Node.penultPos = (length - 2);
    }

    private static void setInRangeDiatonics() {
        for(int i = lowerBound; i <= upperBound; i++) {
            if (isDiatonic(i)) {
                inRangeDiatonics.add(i);
            }
        }
    }

    private static void setColumns() {
        columns = new ArrayList<ArrayList<Node>>(length);
        ArrayList<Node> firstColumn = new ArrayList<Node>();
        start = new Node(tonic);
        firstColumn.add(start);
        columns.add(firstColumn);
        for(int i = 1; i < (climaxEarlyBound - 1); i++) {
            ArrayList<Node> a = new ArrayList<Node>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                if(inRangeDiatonics.get(j) != climax) { a.add(new Node(inRangeDiatonics.get(j))); }
            }
            columns.add(a);
        }
        for(int i = (climaxEarlyBound - 1); i < (climaxLateBound); i++) {
            ArrayList<Node> b = new ArrayList<Node>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                b.add(new Node(inRangeDiatonics.get(j)));
            }
            columns.add(b);
        }
        for(int i = climaxLateBound; i < (length - 1); i++) {
            ArrayList<Node> c = new ArrayList<Node>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                if(inRangeDiatonics.get(j) != climax) { c.add(new Node(inRangeDiatonics.get(j))); }
            }
            columns.add(c);
        }
        ArrayList<Node> lastColumn = new ArrayList<Node>();
        end = new Node(tonic);
        lastColumn.add(end);
        columns.add(lastColumn);
        switch(mode) {
            case PHRYGIAN, AEOLIAN, LOCRIAN: if (isInRange(tonic - 3)) { columns.get(length - 3).add(new Node(tonic - 3)); }
            case DORIAN, MIXOLYDIAN: if (isInRange(tonic - 1)) { columns.get(length - 2).add(new Node(tonic - 1)); }
            default: break;
        }
    }

    private static void makeGetsTo() {
        oneColumnGetsToNoClimax(start, 0);
        for (int j = 1; j < (climaxEarlyBound - 2); j++) {
            for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) {
                oneColumnGetsToNoClimax(columns.get(j).get(k), j);
            }
        }
        for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) {
            oneColumnGetsTo(columns.get((climaxEarlyBound) - 2).get(k), ((climaxEarlyBound) - 2));
        }
        for (int j = (climaxEarlyBound - 1); j < (climaxLateBound - 1); j++) {
            for (int k = 0; k < inRangeDiatonics.size(); k++) {
                oneColumnGetsTo(columns.get(j).get(k), j);
            }
        }
        for (int k = 0; k < inRangeDiatonics.size(); k++) {
            oneColumnGetsToNoClimax(columns.get((climaxLateBound) - 1).get(k), ((climaxLateBound) - 1));
        }
        for (int j = (climaxLateBound); j < (length - 2); j++) {
            for (int k = 0; k < (inRangeDiatonics.size() - 1); k++) {
                oneColumnGetsToNoClimax(columns.get(j).get(k), j);
            }
        }
        switch(mode) {
            case PHRYGIAN, AEOLIAN, LOCRIAN: if(isInRange(tonic - 3)) {
                for (int l = 0; l < columns.get(length - 3).size(); l++) {
                    if (columns.get(length - 3).get(l).pitch == (tonic - 3)) {
                        Node laNode = columns.get(length - 3).get(l);
                        for (int m = 0; m < columns.get(length - 2).size(); m++) {
                            if(columns.get(length - 2).get(m).pitch == (tonic - 1)) {
                                laNode.addEdge(columns.get(length - 2).get(m));
                            }
                        }
                        oneColumnGetsToSpecificNote(columns.get(length - 4).size(), 4, (tonic - 3), laNode);
                    }
                }
            }
            case DORIAN, MIXOLYDIAN: if(isInRange(tonic - 1)) {
                for (int q = 0; q < columns.get(length - 2).size(); q++) {
                    if(columns.get(length - 2).get(q).pitch == (tonic - 1)) {
                        Node tiNode = columns.get(length - 2).get(q);
                        oneColumnGetsToSpecificNote(columns.get(length - 3).size(), 3, (tonic - 1), tiNode);
                    }
                }
            }
            default: break;
        }
        for (int l = 0; l < columns.get(length - 2).size(); l++) {
            if ((columns.get(length - 2).get(l).pitch == (tonic + mode.steps.get(0))) || (columns.get(length - 2).get(l).pitch == (tonic - 1))) {
                columns.get(length - 2).get(l).addEdge(end);
            }
        }
    }

    private static void oneColumnGetsTo(Node node, int currColumn) {
        for(Integer i : allLegalMoves) {
            if (inRangeDiatonics.contains(node.pitch + i)) {
                node.addEdge(columns.get(currColumn + 1).get(inRangeDiatonics.indexOf(node.pitch + i)));
            }
        }
    }

    private static void oneColumnGetsToNoClimax(Node node, int currColumn) {
        for(Integer i : allLegalMoves) {
            if (((node.pitch + i) != climax) && (inRangeDiatonics.contains(node.pitch + i))) {
                int sub = 0;
                if (inRangeDiatonics.indexOf(node.pitch + i) > inRangeDiatonics.indexOf(climax)) {
                        sub = 1;
                    }
                node.addEdge(columns.get(currColumn + 1).get((inRangeDiatonics.indexOf(node.pitch + i)) - sub));
            }
        }
    }

    private static void oneColumnGetsToSpecificNote(int lim, int sub, int note, Node addEdgeTo) {
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

    private static void output(Node from, Node to) throws IOException {
        LinkedList<LinkedList<Node>> cantusFirmi = new LinkedList<LinkedList<Node>>();
        from.giveRoute(to, new LinkedList<Node>(), cantusFirmi);
        int acc = 1;
        if(writeToFile) {
            FileWriter myWriter = new FileWriter("cantusFirmi.txt");
            for (LinkedList<Node> cantus : cantusFirmi) {
                myWriter.write(writeCantus(cantus, acc));
                acc++;
            }
            myWriter.close();
        } else {
            for(LinkedList<Node> cantus : cantusFirmi) {
                System.out.print(writeCantus(cantus, acc));
                acc++;
            }
        }
        int size = cantusFirmi.size();
        switch(size) {
            case 0: System.out.println("Could not generate any cantus firmi with the given parameters :("); break;
            case 1: System.out.println(size + " cantus firmus generated!"); break;
            default: System.out.println(size + " cantus firmi generated!");
        }
    }

    private static String writeCantus(LinkedList<Node> cantus, int acc) {
        int size = (cantus.size() - 1);
        String str = (acc + ", ");
        for(int i = 0; (i < size); i++) {
            str = str + (cantus.get(i).pitch + " ");
        }
        return(str + (cantus.get(size).pitch + ";\n"));
    }

    private static void clearInput() {
        allLegalMoves.clear();
        diatonicPitchClasses.clear();
        inRangeDiatonics.clear();
        columns.clear();
    }

    private static void clearInputException() {
        allLegalMoves.clear();
        diatonicPitchClasses.clear();
        inRangeDiatonics.clear();
    }

    private static void chooseReset() throws InvalidInputException {
        System.out.print("Enter 1 to start again with new parameters, enter 0 to quit.\nNote if you have write to file on, this will overwrite your previous cantusfirmi.txt.\n");
        int choose = Integer.parseInt(keyboard.next());
        switch(choose){
            case 0: break;
            case 1: clearInput(); input(); break;
            default: throw new InvalidInputException(Integer.toString(choose), " is not 0 or 1");
        }
    }

}