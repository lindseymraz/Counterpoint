import java.util.Scanner;

public class IO {
    Scanner keyboard = new Scanner(System.in);
    Graph G = new Graph();
    IO (){}

    public void input() throws InvalidInputException {
        try {
            System.out.println("Enter mode.\n1 for Ionian\n2 for Dorian\n3 for Phrygian\n4 for Lydian\n5 for Mixolydian\n6 for Aeolian\n7 for Locrian");
            G.setMode(Integer.parseInt(keyboard.next()));
            System.out.println("Enter key.\n0 for C, 11 for B, no higher or lower");
            G.setKey(Integer.parseInt(keyboard.next()));
            G.setDiatonicPitchClasses();
            System.out.println("Enter bound 1, no higher than 127, no lower than 0");
            int bound1 = Integer.parseInt(keyboard.next());
            G.boundHelper(bound1);
            System.out.println("Enter bound 2, no higher than 127, no lower than 0");
            int bound2 = Integer.parseInt(keyboard.next());
            G.boundHelper(bound2);
            G.setBounds(bound1, bound2);
            System.out.println("Tonic is " + G.tonic + "\nClimax is " + G.climax);
            System.out.println("Enter length. 8 min, 16 max");
            G.setLength(Integer.parseInt(keyboard.next()));
        } catch (InvalidInputException e) {
            System.out.println(e.badInput + " " + e.whyBad);
            input();
        }
    }
}