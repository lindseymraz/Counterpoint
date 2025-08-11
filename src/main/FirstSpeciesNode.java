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
        //commonly used variables getting assigned for readability and conciseness
        CantusFirmusNode prevCantusNode = IO.composition.cantusFirmus.get(getColumnPosition(currPath));
        CantusFirmusNode potentialCantusNode = IO.composition.cantusFirmus.get(getColumnPositionOfPotentialNodes(currPath));
        FirstSpeciesNode prevNode = currPath.get(getColumnPosition(currPath));

        if(!noCrossing(potentialCantusNode)) { return false; }
        if(getColumnPositionOfPotentialNodes(currPath) > 1) {
            if(!notParallelEighthOrFifth(prevCantusNode, potentialCantusNode, prevNode)) { return false; }
            if(!noOverlap(prevCantusNode, potentialCantusNode, prevNode)) { return false; }
            if(getColumnPositionOfPotentialNodes(currPath) < IO.composition.cantusFirmus.size() - 1) {
                if(!notDirectEighthOrFifth(prevCantusNode, potentialCantusNode, prevNode)) { return false; }
            }
        }
        return true;
    }

    /**
     *
     * @param currPath
     * @return Returns column position where we are looking for potential nodes—so if we added the first node to the path, the column position is in the second column, which is 1 due to 0-indexing.
     */
    int getColumnPositionOfPotentialNodes(LinkedList<FirstSpeciesNode> currPath) {
        return currPath.size();
    }

    /**
     *
     * @param currPath
     * @return Returns column position where the furthest node in the current path currently is. If we have three nodes in the path, the furthest node is in the 3rd column, which is 2 due to 0-indexing.
     */
    int getColumnPosition(LinkedList<FirstSpeciesNode> currPath) {
        return currPath.size() - 1;
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

    /**
     *
     * @param firstCantusNote The cantus firmus note previous to the secondCantusNote.
     * @param secondCantusNote The cantus firmus note that lines up with the first species note currently under consideration.
     * @param prevNote The previous first species note.
     * @return true if two voices' movement is not a parallel octave and not a parallel fifth, false if it is.
     */
    boolean notParallelEighthOrFifth(CantusFirmusNode firstCantusNote, CantusFirmusNode secondCantusNote, FirstSpeciesNode prevNote) {
        int diff = this.pitch - secondCantusNote.pitch;
        switch(diff) {
            case 19, -19, 12, -12, 7, -7: return(!Motion.isParallel(firstCantusNote.pitch, secondCantusNote.pitch, prevNote.pitch, this.pitch));
            default: return true;
        }
    }

    /**
     *
     * @param firstCantusNote The cantus firmus note previous to the secondCantusNote.
     * @param secondCantusNote The cantus firmus note that lines up with the first species note currently under consideration.
     * @param prevNote The previous first species note.
     * @return true if two voices' movement is not a similar octave and not a similar fifth, false if it is.
     */
    boolean notDirectEighthOrFifth(CantusFirmusNode firstCantusNote, CantusFirmusNode secondCantusNote, FirstSpeciesNode prevNote) {
        int diff = this.pitch - secondCantusNote.pitch;
        switch(diff) {
            case 19, -19, 12, -12, 7, -7: return(!Motion.isSimilar(firstCantusNote.pitch, secondCantusNote.pitch, prevNote.pitch, this.pitch));
            default: return true;
        }
    }

    /**
     *
     * @param cantusNote The cantus firmus note that lines up with the first species note currently under consideration.
     * @return true if no voice crossing, false otherwise
     */
    boolean noCrossing(CantusFirmusNode cantusNote) {
        if(IO.firstSpeciesAbove) {
            return(this.pitch >= cantusNote.pitch);
        } else { //first species goes below
            return(this.pitch <= cantusNote.pitch);
        }
    }

    /**
     *
     * @param firstCantusNote The cantus firmus note previous to the secondCantusNote.
     * @param secondCantusNote The cantus firmus note that lines up with the first species note currently under consideration.
     * @param prevNote The previous first species note.
     * @return True if voices do not overlap, false if they do. (Overlap: when lower voice goes higher than upper voice's last note, or when higher voice goes lower than lower voice's last note)
     */
    boolean noOverlap(CantusFirmusNode firstCantusNote, CantusFirmusNode secondCantusNote, FirstSpeciesNode prevNote) {
        if(IO.firstSpeciesAbove) {
            return(secondCantusNote.pitch <= prevNote.pitch && this.pitch >= firstCantusNote.pitch);
        } else { //first species goes below
            return(this.pitch <= firstCantusNote.pitch && secondCantusNote.pitch >= prevNote.pitch);
        }
    }



}
