import java.util.ArrayList;

public enum Mode {
    IONIAN(2, 2, 1, 2, 2, 2, 1),
    DORIAN(2, 1, 2, 2, 2, 1, 2),
    PHRYGIAN(1, 2, 2, 2, 1, 2, 2),
    LYDIAN(2, 2, 2, 1, 2, 2, 1),
    MIXOLYDIAN(2, 2, 1, 2, 2, 1, 2),
    AEOLIAN(2, 1, 2, 2, 1, 2, 2),
    LOCRIAN(1, 2, 2, 1, 2, 2, 2);

    ArrayList<Integer> steps = new ArrayList<Integer>();

    private Mode(int do2Re, int re2Mi, int mi2Fa, int fa2So, int so2La, int la2Ti, int ti2Do) {
        steps.add(do2Re);
        steps.add(re2Mi);
        steps.add(mi2Fa);
        steps.add(fa2So);
        steps.add(so2La);
        steps.add(la2Ti);
        steps.add(ti2Do);
    }

}
