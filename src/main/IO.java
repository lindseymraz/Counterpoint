import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class IO {
    Scanner keyboard = new Scanner(System.in);
    IO (){}

    public void input() throws InvalidInputException {
        try {
            Graph G = new Graph();
            userInitSequence(G, 1, 0, 60, 72, 15);
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
            G.printLots(G.start, G.end, G.climaxEarlyBound, G.climaxLateBound, G.climax, (G.length - 2), G.allSixthsPrecedeFollowStepInOppDir, G.forceAtLeastTwoLeaps);
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + e.whyBad);
            input();
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    public void userInitSequence(Graph G) throws InvalidInputException {
        G.setAllowAllSixths(false); //config
        G.setAllSixthsPrecedeFollowStepInOppDir(false); //config
        G.setForceAtLeastTwoLeaps(false); //config
        G.setUpAllLegalMoves();
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

    public void userInitSequence(Graph G, int mode, int key, int bound1, int bound2, int length) throws InvalidInputException {
        G.setAllowAllSixths(false); //config
        G.setAllSixthsPrecedeFollowStepInOppDir(false); //config
        G.setForceAtLeastTwoLeaps(true); //config
        G.setUpAllLegalMoves();
        G.setMode(mode);
        G.setKey(key);
        G.setDiatonicPitchClasses();
        G.printDiatonicPitchClasses();
        G.boundOK(bound1);
        G.boundOK(bound2);
        G.setBoundsTonicClimax(bound1, bound2);
        G.setLength(length);
    }
}