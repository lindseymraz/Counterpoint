import org.junit.Test;
import static org.junit.Assert.*;

public class Examples {
    public Examples() {}

    @Test
    public void testDiatonicity(){
        Graph G = new Graph(Mode.AEOLIAN, 9);
        G.setDiatonicPitchClasses();
        assertTrue(G.isDiatonic(0));
        assertFalse(G.isDiatonic(1));
        assertTrue(G.isDiatonic(2));
        assertFalse(G.isDiatonic(3));
        assertTrue(G.isDiatonic(4));
        assertTrue(G.isDiatonic(5));
        assertFalse(G.isDiatonic(6));
        assertTrue(G.isDiatonic(7));
        assertFalse(G.isDiatonic(8));
        assertTrue(G.isDiatonic(9));
        assertFalse(G.isDiatonic(10));
        assertTrue(G.isDiatonic(11));
        assertTrue(G.isDiatonic(12));
        assertTrue(G.isDiatonic(60));
        assertFalse(G.isDiatonic(61));
        assertTrue(G.isDiatonic(62));
        assertFalse(G.isDiatonic(63));
        assertTrue(G.isDiatonic(64));
        assertTrue(G.isDiatonic(65));
        assertFalse(G.isDiatonic(66));
        assertTrue(G.isDiatonic(67));
        assertFalse(G.isDiatonic(68));
        assertTrue(G.isDiatonic(69));
        assertFalse(G.isDiatonic(70));
        assertTrue(G.isDiatonic(71));
        assertTrue(G.isDiatonic(72));
        assertTrue(G.isDiatonic(120));
        assertFalse(G.isDiatonic(121));
        assertTrue(G.isDiatonic(122));
        assertFalse(G.isDiatonic(123));
        assertTrue(G.isDiatonic(124));
        assertTrue(G.isDiatonic(125));
        assertFalse(G.isDiatonic(126));
        assertTrue(G.isDiatonic(127));
    }

}
