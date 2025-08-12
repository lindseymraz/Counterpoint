import java.util.Map;
import static java.util.Map.entry;

public enum Key {
    Bsharp(0), C(0),
    Csharp(1), Dflat(1),
    D(2),
    Dsharp(3), Eflat(3),
    E(4), Fflat(4),
    Esharp(5), F(5),
    Fsharp(6), Gflat(6),
    G(7),
    Gsharp(8), Aflat(8),
    A(9),
    Asharp(10), Bflat(10),
    B(11), Cflat(11);

    final int offset;

    Key(int offset) {
        this.offset = offset;
    }
    static final Map<String, Key> stringNameToKeyMap = Map.ofEntries(
            entry("Cb", Cflat),
            entry("C", C),
            entry("C#", Csharp),
            entry("Db", Dflat),
            entry("D", D),
            entry("D#", Dsharp),
            entry("Eb", Eflat),
            entry("E", E),
            entry("E#", Esharp),
            entry("Fb", Fflat),
            entry("F", F),
            entry("F#", Fsharp),
            entry("Gb", Gflat),
            entry("G", G),
            entry("G#", Gsharp),
            entry("Ab", Aflat),
            entry("A", A),
            entry("A#", Asharp),
            entry("Bb", Bflat),
            entry("B", B),
            entry("B#", Bsharp)
    );

    /**
     *
     * @param aString a string representing a key
     * @return a Key object corresponding to the given string: e.g. given "C#", return Csharp
     */
    static Key getKeyFromString(String aString) {
        return stringNameToKeyMap.get(aString);
    }


}
