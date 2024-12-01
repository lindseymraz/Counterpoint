import java.util.LinkedList;

public class FirstSpeciesNode extends Node<FirstSpeciesNode> {

    FirstSpeciesNode(int pitch) {
        super(pitch);
    }

    void giveRoute(FirstSpeciesNode to, LinkedList<FirstSpeciesNode> currPath, LinkedList<LinkedList<FirstSpeciesNode>> list) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<FirstSpeciesNode>(currPath));
            currPath.remove(this);
        } else {
            currPath.add(this);
            for (FirstSpeciesNode n : this.getsTo) {
                if (passesTests(n, currPath)) {
                    n.giveRoute(to, currPath, list);
                }
            }
            currPath.remove(this);
        }
    }

    boolean passesTests(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) {
        return true;
    }



    boolean makesConsonance(CantusFirmusNode cantusNote) {
        int diff = (this.pitch - cantusNote.pitch);
        switch(diff) {
            case -19, -16, -15, -12, -9, -8, -7, -4, -3, 0, 3, 4, 7, 8, 9, 12, 15, 16, 19: return true;
            default: return false;
        }
    }

    boolean makesPerfectConsonance(CantusFirmusNode cantusNote) {
        int diff = (this.pitch - cantusNote.pitch);
        switch(diff) {
            case -19, -12, -7, 0, 7, 12, 19: return true;
            default: return false;
        }
    }

}
