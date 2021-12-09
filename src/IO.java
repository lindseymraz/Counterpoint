import java.util.LinkedList;
import java.util.Scanner;

public class IO {
    Scanner keyboard = new Scanner(System.in);
    Graph G = new Graph();
    IO (){}

    public void input() throws InvalidInputException {
        try {
            userInputSequence();
            G.setUpLegalMovesTemplate();
            //G.printLegalMovesTemplate();
            G.setInRangeDiatonics();
            G.setColumns();
            G.makeGetsTo();
            G.climaxPosPicker();
            //G.pickTestNode();
            //System.out.println("pitch is " + G.testnode.pitch);
            //System.out.println("getsTo is ");
            //G.testnode.printLinkedList(G.testnode.getsTo);
            System.out.println("Early bound is " + G.climaxEarlyBound + ", late bound is " + G.climaxLateBound);
            G.start.printLots(G.end, G.climaxEarlyBound, G.climaxLateBound, G.climax, G.length);
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + " " + e.whyBad);
            input();
        }
    }

    public void userInputSequence() throws InvalidInputException {
        System.out.println("Enter mode.\n1 for Ionian\n2 for Dorian\n3 for Phrygian\n4 for Lydian\n5 for Mixolydian\n6 for Aeolian\n7 for Locrian");
        G.setMode(Integer.parseInt(keyboard.next()));
        System.out.println("Enter key.\n0 for C, 11 for B, no higher or lower");
        G.setKey(Integer.parseInt(keyboard.next()));
        G.setDiatonicPitchClasses();
        G.printDiatonicPitchClasses();
        System.out.println("Enter bound 1, no higher than 127, no lower than 0");
        int bound1 = Integer.parseInt(keyboard.next());
        G.boundOK(bound1);
        System.out.println("Enter bound 2, no higher than 127, no lower than 0");
        int bound2 = Integer.parseInt(keyboard.next());
        G.boundOK(bound2);
        G.setBoundsTonicClimax(bound1, bound2);
        System.out.println("Tonic is " + G.tonic + "\nClimax is " + G.climax);
        System.out.println("Enter length. 8 min, 16 max");
        G.setLength(Integer.parseInt(keyboard.next()));
        System.out.println("Length is " + G.length);
    }
}