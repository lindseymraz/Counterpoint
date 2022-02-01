package com.counterpoint;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

class IO {

    private static LinkedList<LinkedList<CantusFirmusNode>> cantusFirmi;
    private static ArrayList<ArrayList<CantusFirmusNode>> columns;
    private static CantusFirmusNode start;
    private static CantusFirmusNode end;
    static LinkedList<CantusFirmusNode> cantusFirmus;

    private static Scanner keyboard = new Scanner(System.in);
    IO(){}

    IO(Mode mode, int key){
        this.mode = mode;
        this.key = key;
    }

    private static boolean allowAllSixths;
    private static boolean writeToFile;
    private static boolean debugMessagesOn;

    static Mode mode;
    private static int key; //0 is C, 11 is B
    private static int length; //8 to 16
    private static int upperBound; //the upper bound is allowed, nothing above it
    private static int lowerBound; //the lower bound is allowed, nothing below it
    static int tonic; //the actual pitch value, stuff like 60, not just pitch class
    static int climax; //actual pitch value, not just pitch class
    static int climaxEarlyBound; //earliest climax can occur. ex: if 3, climax can be 3rd note at earliest (this means 2nd column in array though)
    static int climaxLateBound; //latest climax can occur. ex: if 6, climax can be 6th note at latest (this means 5th column in array though)

    private static ArrayList<Integer> allLegalMoves = new ArrayList<Integer>();
    private static ArrayList<Integer> diatonicPitchClasses = new ArrayList<Integer>(7); //ranging from 0 to 11
    private static ArrayList<Integer> inRangeDiatonics = new ArrayList<Integer>();

    private static void config() {
        allowAllSixths = false;
        CantusFirmusNode.allSixthsPrecedeFollowStepInOppDir = false;
        CantusFirmusNode.forceAtLeastTwoLeaps = true;
        writeToFile = true;
        debugMessagesOn = true;
        CantusFirmusNode.naturalSeventhAvoidsRaisedSeventh = false;
    }

    static void cantusFirmusInput() throws InvalidInputException {
        try {
            config();
            setUpAllLegalMoves();
            setMode();
            setKey();
            setDiatonicPitchClasses();
            System.out.println("Enter bound 1, no higher than 127, no lower than 0");
            int bound1 = Integer.parseInt(keyboard.next());
            boundOK(bound1);
            System.out.println("Enter bound 2, no higher than 127, no lower than 0");
            int bound2 = Integer.parseInt(keyboard.next());
            boundOK(bound2);
            setBoundsTonicClimax(bound1, bound2);
            setLength();
            climaxPosPicker();
            setPenultPos();
            setInRangeDiatonics();
            setColumns();
            makeGetsTo();
            int size = output(start, end);
            chooseReset(size);
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
        System.out.println("Enter key.\n0 for C, 11 for B, no higher or lower");
        String input = (keyboard.next());
        switch(Integer.parseInt(input)) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11: key = Integer.parseInt(input); break;
            default: throw new InvalidInputException((input), " is not an integer between 0 and 11");
        }
    }

    //starting with the tonic, add steps (from mode.steps) needed to jump to the next diatonic note.
    //the % 12 ensures the values are 0-11
    static void setDiatonicPitchClasses() {
        diatonicPitchClasses.add(key); //adds tonic
        int hop = key;
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

    private static void setLength() throws InvalidInputException {
        System.out.println("Enter length. 8 min, 16 max");
        int input = Integer.parseInt(keyboard.next());
        if(input < 8) {
            throw new InvalidInputException(Integer.toString(input), " is less than 8");
        }
        if(input > 16) {
            throw new InvalidInputException(Integer.toString(input), " is greater than 16");
        } length = input;
        if(debugMessagesOn) {
            System.out.println("Length is " + length);
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

    private static int output(CantusFirmusNode from, CantusFirmusNode to) throws IOException {
        cantusFirmi = new LinkedList<LinkedList<CantusFirmusNode>>();
        from.giveRoute(to, new LinkedList<CantusFirmusNode>(), cantusFirmi);
        int acc = 1;
        if(writeToFile) {
            FileWriter myWriter = new FileWriter("cantusFirmi.txt");
            for (LinkedList<CantusFirmusNode> cantus : cantusFirmi) {
                myWriter.write(writeCantus(cantus, acc));
                acc++;
            }
            myWriter.close();
        } else {
            for(LinkedList<CantusFirmusNode> cantus : cantusFirmi) {
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
        return size;
    }

    private static String writeCantus(LinkedList<CantusFirmusNode> cantus, int acc) {
        int size = (cantus.size() - 1);
        String str = (acc + ", ");
        for(int i = 0; (i < size); i++) {
            str = str + (cantus.get(i).pitch + " ");
        }
        return(str + (cantus.get(size).pitch + ";\n"));
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

    private static void chooseReset(int size) throws InvalidInputException {
        System.out.print("Enter 0 to quit, 1 to start again with new parameters");
        if(size > 0) {
            System.out.println(", or 2 to proceed to first species.");
        } else {
            System.out.println(".");
        }
        System.out.println("Note if you have write to file on, this will overwrite your previous cantusfirmi.txt.");
        int choose = Integer.parseInt(keyboard.next());
        if(size > 0) {
            switch(choose){
                case 0: break;
                case 1: clearCantusFirmusInput(); cantusFirmusInput(); break;
                case 2: selectACantusFirmus(size); break;
                default: throw new InvalidInputException(Integer.toString(choose), " is not 0, 1, or 2");
            }
        } else {
            switch(choose){
                case 0: break;
                case 1: clearCantusFirmusInput(); cantusFirmusInput(); break;
                default: throw new InvalidInputException(Integer.toString(choose), " is not 0 or 1");
            }
        }
    }

    static void setCF(LinkedList<CantusFirmusNode> cantus) {
        cantusFirmus = cantus;
    }

    private static void selectACantusFirmus(int size) throws InvalidInputException {
        try {
            LinkedList<CantusFirmusNode> selectedCF = cantusFirmi.get(0);
            int input = 1;
            if(size > 1) {
                System.out.println("Select a generated cantus firmus. Options are from 1 to " + size + ".");
                input = Integer.parseInt(keyboard.next());
                if(input < 1) {
                    throw new InvalidInputException(Integer.toString(input), " is below 1");
                } if(input > size) {
                    throw new InvalidInputException(Integer.toString(input), " is above " + size);
                } else {
                    selectedCF = cantusFirmi.get(input - 1);
                }
            } System.out.println("The cantus firmus that will be used is " + input + ", whose notes are:");
            String str = "";
            for(int i = 0; (i < selectedCF.size()); i++) {
                str = str + (selectedCF.get(i).pitch + " ");
            }
            System.out.println(str);
            if(size > 1) {
                System.out.println("Is this acceptable? Enter 1 for yes, 0 to select a different cantus firmus.");
                int acceptable = Integer.parseInt(keyboard.next());
                switch(acceptable) {
                    case 0: selectACantusFirmus(size); break;
                    case 1: setCF(selectedCF); break;
                    default: throw new InvalidInputException(Integer.toString(acceptable), " is not 1 or 0");
                }
            } firstSpeciesInput();
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            selectACantusFirmus(size);
        }
    }

    private static void selectCounterpointAboveOrBelow() throws InvalidInputException {
        System.out.println("Enter 0 to put the counterpoint above the cantus firmus; 1 to put the counterpoint below the cantus firmus.");
        int input = Integer.parseInt(keyboard.next());
        switch(input) {
            case 0: break; //above
            case 1: break; //below
            default: throw new InvalidInputException(Integer.toString(input), " is not 1 or 0");
        }
    }

}