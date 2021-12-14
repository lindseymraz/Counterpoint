import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Math;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

public class Graph {
    Mode mode;
    int key; //0 is C, 11 is B
    int upperBound; //the upper bound is allowed, nothing above it
    int lowerBound; //the lower bound is allowed, nothing below it
    int tonic; //the actual pitch value, stuff like 60, not just pitch class
    int climax; //actual pitch value, not just pitch class
    ArrayList<Integer> diatonicPitchClasses = new ArrayList<Integer>(7); //ranging from 0 to 11
    int length; //8 to 16
    ArrayList<Integer> allLegalMoves = new ArrayList<Integer>(15);
    ArrayList<Integer> inRangeDiatonics = new ArrayList<Integer>();
    ArrayList<ArrayList<Node>> columns;
    Node start;
    Node end;
    Node testnode;
    boolean allowAllSixths;
    boolean allSixthsPrecedeFollowStepInOppDir;
    boolean forceAtLeastTwoLeaps;
    int climaxEarlyBound; //earliest climax can occur
    int climaxLateBound; //latest climax can occur

    void setAllowAllSixths(boolean option) {
        allowAllSixths = option;
    }

    void setAllSixthsPrecedeFollowStepInOppDir(boolean option) {
        allSixthsPrecedeFollowStepInOppDir = option;
    }

    void setForceAtLeastTwoLeaps(boolean option) {
        forceAtLeastTwoLeaps = option;
    }

    Graph(){}

    Graph(Mode mode, int key) {
        this.mode = mode;
        this.key = key;
    }

    void setUpAllLegalMoves() {
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

    void setMode(int input) throws InvalidInputException {
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

    void setKey(int input) throws InvalidInputException {
        switch(input) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11: key = input; break;
            default: throw new InvalidInputException(Integer.toString(input), "is not an integer between 0 and 11");
        }
    }

    //starting with the tonic, add steps (from mode.steps) needed to jump to the next diatonic note.
    //the % 12 ensures the values are 0-11
    void setDiatonicPitchClasses() {
        diatonicPitchClasses.add(key); //adds tonic
        int hop = key;
        for(int i = 0; i < 6; i++) { //not using element-based for because you need to avoid the last
            hop = (hop + mode.steps.get(i)) % 12; //item in mode.steps, which is ti2Do.
            diatonicPitchClasses.add(hop);
        }
    }

    //for debugging and testing
    void printDiatonicPitchClasses() {
        for(Integer i : diatonicPitchClasses) {
            System.out.println(i);
        }
    }

    void boundOK(int input) throws InvalidInputException {
        if(input < 0) {
            throw new InvalidInputException(Integer.toString(input), "is less than 0");
        } if(input > 127) {
            throw new InvalidInputException(Integer.toString(input), "is greater than 127");
        } if(!isDiatonic(input)) {
            throw new InvalidInputException(Integer.toString(input), "is not diatonic");
        }
    }

    boolean isDiatonic(int pitch) {
        return diatonicPitchClasses.contains(pitch%12);
    }

    void setBoundsTonicClimax(int input1, int input2) throws InvalidInputException {
        int distance;
        int upper;
        int lower;
        if(input2 > input1) {
            distance = input2 - input1;
            upper = input2;
            lower = input1;
        } else {
            distance = input1 - input2;
            upper = input1;
            lower = input2;
        }
        switch(mode) {
            case IONIAN, LYDIAN, MIXOLYDIAN: intervalSizeOK(distance, 4, 16, "major"); break;
            case DORIAN, PHRYGIAN, AEOLIAN, LOCRIAN: intervalSizeOK(distance, 3, 15, "minor"); break;
        }
        setTonic(upper, lower);
    }

    void intervalSizeOK(int distance, int smallest, int greatest, String quality) throws InvalidInputException {
        if(distance < smallest) {
            throw new InvalidInputException(Integer.toString(distance), " is distance between bounds, interval must span at least a " + quality + " third");
        }
        if(distance > greatest) {
            throw new InvalidInputException(Integer.toString(distance), " is distance between bounds, interval must not exceed a " + quality + " tenth");
        }
    }

    void setTonic(int upper, int lower) throws InvalidInputException {
        LinkedList<Integer> tonics = findTonics(upper, lower);
        int a = (upper - tonics.get(0));
        int b = (tonics.get(0) - lower);
        int whichTonic = 0;
        if(tonics.size() == 2) {
            int c = (upper - tonics.get(1));
            int d = (tonics.get(1) - lower);
            if (c > a) {
                a = c;
                whichTonic = 1;
            }
            if (d > b) {
                b = d;
                whichTonic = 1;
            }
        }
        if(a > b) {
            if(isClimaxInappropriate(a)) {
                throw new InvalidInputException(Integer.toString(a), " is ti, climax must not be ti");
            } else {
                climax = upper;
            }
        } else if(b > a) {
            if(isClimaxInappropriate(b)) {
                throw new InvalidInputException(Integer.toString(b), " is ti, climax must not be ti");
            } else {
                climax = lower;
            }
        } else {
            throw new InvalidInputException("", "is bottom or top climax?\nIt's equidistant, unclear");
        }
        upperBound = upper;
        lowerBound = lower;
        tonic = tonics.get(whichTonic);
    }

    LinkedList<Integer> findTonics(int upper, int lower) throws InvalidInputException {
        int hop = lower;
        boolean foundATonic = false;
        LinkedList<Integer> tonics = new LinkedList<Integer>();
        int counter = diatonicPitchClasses.indexOf(hop%12);
        while(hop <= upper) {
            if((hop%12) == key) {
                if(foundATonic) {
                    tonics.add(hop);
                    return tonics;
                } else {
                    tonics.add(hop);
                    foundATonic = true;
                }
            } else {
                hop = hop + mode.steps.get(counter);
                if(counter == 6) {
                    counter = 0;
                } else {
                    counter = counter + 1;
                }
            }
        } throw new InvalidInputException("", "did not find a tonic");
    }

    boolean isClimaxInappropriate(int pitch) {
        return(((key + 12) - 1) == (pitch%12));
    }

    void setLength(int input) throws InvalidInputException {
        if(input < 8) {
            throw new InvalidInputException(Integer.toString(input), " is less than 8");
        }
        if(input > 16) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 16");
        } this.length = input;
    }

    void setInRangeDiatonics() {
        for(int i = lowerBound; i <= upperBound; i++) {
            if (isDiatonic(i)) {
                inRangeDiatonics.add(i);
            }
        }
    }

    void setColumns() {
        columns = new ArrayList<ArrayList<Node>>(length);
        for(int i = 0; i < length; i++) {
            ArrayList<Node> a = new ArrayList<Node>(inRangeDiatonics.size());
            for(int j = 0; j < inRangeDiatonics.size(); j++) {
                a.add(new Node(inRangeDiatonics.get(j)));
            }
            columns.add(a);
        }
        switch(mode) {
            case PHRYGIAN, AEOLIAN, LOCRIAN: if (isInRange(tonic - 3)) { columns.get(length - 3).add(new Node(tonic - 3)); }
            case DORIAN, MIXOLYDIAN: if (isInRange(tonic - 1)) { columns.get(length - 2).add(new Node(tonic - 1)); }
            default: break;
        }
    }

    void makeGetsTo() {
        start = columns.get(0).get(inRangeDiatonics.indexOf(tonic));
        makeGetsToHelper(start, 0);
        for (int j = 1; j < (length - 2); j++) {
            for (int k = 0; k < inRangeDiatonics.size(); k++) {
                makeGetsToHelper(columns.get(j).get(k), j);
            }
        }
        switch(mode) {
            case PHRYGIAN, AEOLIAN, LOCRIAN: if(isInRange(tonic - 3)) {
                int la = (tonic - 3);
                for (int l = 0; l < columns.get(length - 3).size(); l++) {
                    if (columns.get(length - 3).get(l).pitch == la) {
                        Node laNode = columns.get(length - 3).get(l);
                        int mLim = columns.get(length - 2).size();
                        for (int m = 0; m < mLim; m++) {
                            if(columns.get(length - 2).get(m).pitch == (tonic - 1)) {
                                laNode.addEdge(columns.get(length - 2).get(m));
                            }
                        }
                        int nLim = columns.get(length - 4).size();
                        makeGetsToHelper2(nLim, 4, la, laNode);
                    }
                }
            }
            case DORIAN, MIXOLYDIAN: if(isInRange(tonic - 1)) {
                int tii = tonic - 1;
                int pLim = columns.get(length - 3).size();
                int qLim = columns.get(length - 2).size();
                for (int q = 0; q < qLim; q++) {
                    if(columns.get(length - 2).get(q).pitch == tii) {
                        Node tiNode = columns.get(length - 2).get(q);
                        makeGetsToHelper2(pLim, 3, tii, tiNode);
                    }
                }
            }
            default: break;
        }
        for(int o = 0; o < columns.get(length - 1).size(); o++) {
            if(columns.get(length - 1).get(o).pitch == tonic) {
                end = columns.get(length - 1).get(o);
            }
        }
        int re = (tonic + mode.steps.get(0));
        int ti = (tonic - 1);
        int lLim = columns.get(length - 2).size();
        for (int l = 0; l < lLim; l++) {
            if ((columns.get(length - 2).get(l).pitch == re) || (columns.get(length - 2).get(l).pitch == ti)) {
                columns.get(length - 2).get(l).addEdge(end);
            }
        }
    }

    void makeGetsToHelper(Node node, int currColumn) {
        for(Integer i : allLegalMoves) {
            if (inRangeDiatonics.contains(node.pitch + i)) {
                node.addEdge(columns.get(currColumn + 1).get(inRangeDiatonics.indexOf(node.pitch + i)));
            }
        }
    }

    void makeGetsToHelper2(int lim, int sub, int note, Node addEdgeTo) {
        for(int j = 0; j < lim; j++) {
            for(Integer i : allLegalMoves) {
                if((columns.get(length - sub).get(j).pitch + i) == note) {
                    columns.get(length - sub).get(j).addEdge(addEdgeTo);
                }
            }
        }
    }

    void climaxPosPicker() {
        climaxEarlyBound = (int)((Math.floor(length*.33)) + 1);
        climaxLateBound = (int)(Math.round(length*.66));
    }

    void pickTestNode() {
        testnode = columns.get(1).get(4);
    }

    boolean isInRange(int pitch) {
        return((pitch >= lowerBound) && (pitch <= upperBound));
    }

    //for debugging and testing!
    void printAllLegalMoves() {
        for(Integer i : allLegalMoves) {
            System.out.println(allLegalMoves.get(i));
        }
    }

    void printLots(Node from, Node to, int earlyBound, int laterBound, int climax, int penultPos, boolean allSixthsPrecedeFollowStepInOppDir, boolean forceAtLeastTwoLeaps) throws IOException {
        LinkedList<LinkedList<Node>> cantusFirmi = new LinkedList<LinkedList<Node>>();
        from.giveRoute(to, new LinkedList<Node>(), cantusFirmi, earlyBound, laterBound, climax, penultPos, allSixthsPrecedeFollowStepInOppDir, forceAtLeastTwoLeaps);
        int acc = 1;
        FileWriter myWriter = new FileWriter("filename.txt");
        for(LinkedList<Node> cantus : cantusFirmi) {
            myWriter.write(from.writeFile(cantus, acc));
            acc++;
        }
        myWriter.close();
        int size = cantusFirmi.size();
        switch(size) {
            case 0: System.out.println("Could not generate any cantus firmi with the given parameters :("); break;
            case 1: System.out.println(size + " cantus firmus generated!"); break;
            default: System.out.println(size + " cantus firmi generated!");
        }
    }
}

