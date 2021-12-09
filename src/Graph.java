import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Math;

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
    LinkedList<Integer> legalMovesTemplate = new LinkedList<Integer>();
    LinkedList<Integer> tempLegalMoves = new LinkedList<Integer>();
    ArrayList<Integer> inRangeDiatonics = new ArrayList<Integer>();
    ArrayList<ArrayList<Node>> columns;
    Node start;
    Node end;
    Node testnode;
    int climaxEarlyBound; //earliest climax can occur
    int climaxLateBound; //latest climax can occur

    void climaxPosPicker() {
        climaxEarlyBound = (int)((Math.round(length*.66)) - (Math.round(length*.33)) + 1);
        climaxLateBound = ((climaxEarlyBound * 2) - 1);
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
    }

    void makeGetsTo() {
        start = columns.get(0).get(inRangeDiatonics.indexOf(tonic));
        for(Integer i : allLegalMoves) {
            if (inRangeDiatonics.contains(tonic + i)) {
                start.addEdge(columns.get(1).get(inRangeDiatonics.indexOf(tonic + i)));
            }
        }
        for(int j = 1; j < (length - 1); j++) {
            for(int k = 0; k < inRangeDiatonics.size(); k++) {
                makeGetsToHelper(columns.get(j).get(k), j);
            }
        }
        end = columns.get(length - 1).get(inRangeDiatonics.indexOf(tonic));
    }

    void pickTestNode() {
        testnode = columns.get(1).get(4);
    }

    void makeGetsToHelper(Node node, int currColumn) {
        for(Integer i : allLegalMoves) {
            if (inRangeDiatonics.contains(node.pitch + i)) {
                node.addEdge(columns.get(currColumn + 1).get(inRangeDiatonics.indexOf(node.pitch + i)));
            }
        }
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
    }

    void setUpLegalMovesTemplate() {
        legalMovesTemplate.addAll(allLegalMoves);
    }

    void removeAllLeaps() {
        legalMovesTemplate.removeFirstOccurrence(-12);
        legalMovesTemplate.removeFirstOccurrence(-7);
        legalMovesTemplate.removeFirstOccurrence(-5);
        legalMovesTemplate.removeFirstOccurrence(-4);
        legalMovesTemplate.removeFirstOccurrence(-3);
        legalMovesTemplate.removeFirstOccurrence(3);
        legalMovesTemplate.removeFirstOccurrence(4);
        legalMovesTemplate.removeFirstOccurrence(5);
        legalMovesTemplate.removeFirstOccurrence(7);
        legalMovesTemplate.removeFirstOccurrence(8);
        legalMovesTemplate.removeFirstOccurrence(12);
    }

    void removeAllP4AndUpLeaps() {
        legalMovesTemplate.removeFirstOccurrence(-12);
        legalMovesTemplate.removeFirstOccurrence(-7);
        legalMovesTemplate.removeFirstOccurrence(-5);
        legalMovesTemplate.removeFirstOccurrence(5);
        legalMovesTemplate.removeFirstOccurrence(7);
        legalMovesTemplate.removeFirstOccurrence(8);
        legalMovesTemplate.removeFirstOccurrence(12);
    }

    void setUpTempLegalMoves(int pitch) {
        tempLegalMoves.clear();
        for(Integer i : legalMovesTemplate) {
            int tempPitch = (pitch + i);
            if(isInRange(tempPitch)) {
                if(isDiatonic(tempPitch)) {
                    tempLegalMoves.add(i);
                }
            }
        }
    }

    //for debugging and testing!
    void printLegalMovesTemplate() {
        for(Integer i : legalMovesTemplate) {
            System.out.println(i);
        }
    }

    //for debugging and testing!
    void printAllLegalMoves() {
        for(Integer i : allLegalMoves) {
            System.out.println(allLegalMoves.get(i));
        }
    }

    Graph() {
        setUpAllLegalMoves();
    }

    Graph(Mode mode, int key) {
        setUpAllLegalMoves();
        this.mode = mode;
        this.key = key;
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
            default: throw new InvalidInputException(Integer.toString(input), "is not an integer between 1 and 7");
        }
    }

    void setKey(int input) throws InvalidInputException {
        switch(input) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11: key = input; break;
            default: throw new InvalidInputException(Integer.toString(input), "is not an integer between 0 and 11");
        }
    }

    void setDiatonicPitchClasses() { //may want to clean with a more element-based for loop
        //style item, look at JavaDoc for ArrayList
        diatonicPitchClasses.add(key);
        int hop = key;
        for(int i = 0; i < 6; i++) {
            hop = (hop + mode.steps.get(i)) % 12;
            diatonicPitchClasses.add(hop);
        }
    }

    //for debugging and testing!
    void printDiatonicPitchClasses() { //may want to clean with a more element-based for loop
        //style item, look at JavaDoc for ArrayList
        for(int i = 0; i < 7; i++) {
            System.out.println(diatonicPitchClasses.get(i));
        }
    }

    boolean isDiatonic(int pitch) {
        return diatonicPitchClasses.contains(pitch%12);
    }

    void boundHelper(int input) throws InvalidInputException {
        if(input < 0) {
            throw new InvalidInputException(Integer.toString(input), "is less than 0");
        } else if(input > 127) {
            throw new InvalidInputException(Integer.toString(input), "is greater than 127");
        } else if(!isDiatonic(input)) {
            throw new InvalidInputException(Integer.toString(input), "is not diatonic");
        }
    }

    void setBounds(int input1, int input2) throws InvalidInputException {
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
            case IONIAN, LYDIAN, MIXOLYDIAN: intervalSizeOK(true, distance); break;
            case DORIAN, PHRYGIAN, AEOLIAN, LOCRIAN: intervalSizeOK(false, distance); break;
        }
        setTonic(upper, lower);
    }

    void intervalSizeOK(boolean isMajThirdTenth, int distance) throws InvalidInputException {
        if(isMajThirdTenth) {
            if(distance < 4) {
                throw new InvalidInputException(Integer.toString(distance), "is distance between bounds,\ninterval must span at least a major third");
            } else if(distance > 16) {
                throw new InvalidInputException(Integer.toString(distance), "is distance between bounds,\ninterval must not exceed a major tenth");
            }
        } else {
            if(distance < 3) {
                throw new InvalidInputException(Integer.toString(distance), "is distance between bounds,\ninterval must span at least a minor third");
            } else if(distance > 15) {
                throw new InvalidInputException(Integer.toString(distance), "is distance between bounds,\ninterval must not exceed a minor tenth");
            }
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
                throw new InvalidInputException(Integer.toString(a), "is ti, climax must not be ti");
            } else {
                climax = upper;
            }
        } else if(b > a) {
            if(isClimaxInappropriate(b)) {
                throw new InvalidInputException(Integer.toString(b), "is ti, climax must not be ti");
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
                if(counter == 6) {
                    hop = hop + mode.steps.get(counter);
                    counter = 0;
                } else {
                    hop = hop + mode.steps.get(counter);
                    counter = counter + 1;
                }
            }
        } throw new InvalidInputException("", "did not find a tonic");
    }

    boolean isClimaxInappropriate(int pitch) {
        return((diatonicPitchClasses.get(6)) == (pitch%12));
    }

    boolean isInRange(int pitch) {
        return((pitch >= lowerBound) && (pitch <= upperBound));
    }

    void setLength(int input) throws InvalidInputException {
        if(input < 8) {
            throw new InvalidInputException(Integer.toString(input), " is less than 8");
        } else if(input > 16) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 16");
        } else this.length = input;
    }
}

