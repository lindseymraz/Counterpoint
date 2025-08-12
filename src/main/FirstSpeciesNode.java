import java.util.Collections;
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
        int size = currPath.size();
        //commonly used variables getting assigned for readability and conciseness
        CantusFirmusNode prevCantusNode = IO.composition.cantusFirmus.get(size - 1);
        CantusFirmusNode potentialCantusNode = IO.composition.cantusFirmus.get(size);
        FirstSpeciesNode prevNode = currPath.get(size - 1);

        if(!noCrossing(potentialCantusNode)) { return false; }
        if(!reasonableRange(currPath)) { return false; }
        if(size >= 1) {
            if(!notParallelEighthOrFifth(prevCantusNode, potentialCantusNode, prevNode)) { return false; }
            if(!noOverlap(prevCantusNode, potentialCantusNode, prevNode)) { return false; }
            if(size >= 4) {
                if(!underFourSuccessiveParallelImperfectConsonances(size, currPath)) { return false; }
                if (size < IO.composition.cantusFirmus.size() - 1) {
                    if (!notDirectEighthOrFifth(prevCantusNode, potentialCantusNode, prevNode)) {return false;}
                }
            }
        }
        if(IO.copyCantusFirmusRulesAsGeneralMelodic) {
            int dist = n.pitch - this.pitch;
            if(octAndSixthNotPrecededWithStepInOppositeDir(prevNode, dist)) { return false; }
            if(octAndSixthNotFollowedWithStepInOppositeDir(n, (this.pitch - prevNode.pitch))) { return false; }
            if(fifthOrBiggerDoesntChangeDir(prevNode, dist)) { return false; }
            if(failsSameDirSuccessiveLeapIntervalRequirement(prevNode, n)) { return false; }
            if(size >= 3) {
                FirstSpeciesNode prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
                if(willExceedAllowedLeaps(n, currPath)) { return false; }
                if(threeSequentialLeapsBetween(prevPrevNode, prevNode, this, n)) { return false; }
                if(octOrSixthAsSecondSequentialLeap(n, (this.pitch - prevNode.pitch))) { return false; }
                if(hasMotifs(n, currPath)) { return false; }
                if(outlinesDissonantMelodic(n, currPath)) { return false; }
                if(twoSuccessiveLeapsNotPrecededWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(distToPenultExceedsMajThird(n, currPath)) { return false; }
                if(size == (penultPos + 1)) {
                    if(diatonicSeventhsAreInFourNotesBeforeLeadingRaisedSeventh(currPath)) { return false; }
                    if(notEnoughLeaps(currPath)) { return false; }
                }
            }
        }
        return true;
    }
    boolean makesConsonance(CantusFirmusNode cantusNote) {
        int diff = (this.pitch - cantusNote.pitch);
        switch(diff) {
            case -15, -12, -9, -8, -7, -4, -3, 0, 3, 4, 7, 8, 9, 12, 15: return true;
            default: return false;
        }
    }

    boolean makesPerfectConsonance(CantusFirmusNode cantusNote) {
        int diff = (this.pitch - cantusNote.pitch);
        switch(diff) {
            case -12, -7, 0, 7, 12: return true;
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
            case 12, -12, 7, -7: return(!Motion.isParallel(firstCantusNote.pitch, secondCantusNote.pitch, prevNote.pitch, this.pitch));
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
            case 12, -12, 7, -7: return(!Motion.isSimilar(firstCantusNote.pitch, secondCantusNote.pitch, prevNote.pitch, this.pitch));
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

    public boolean reasonableRange(LinkedList<FirstSpeciesNode> currPath) {
        int lowestPitch = findLowestPitch(currPath);
        if(lowestPitch > this.pitch) {
            lowestPitch = this.pitch;
        }
        return((this.pitch - lowestPitch) < (Interval.octave.distance + Interval.majorThird.distance));
    }

    public int findLowestPitch(LinkedList<FirstSpeciesNode> currPath) {
        return currPath.stream().mapToInt(node -> node.pitch).min().orElse(127);
    }


    /**
     * @param currPath current path
     * @return true if the current node being considered would NOT result in four parallel thirds, four parallel sixths, or four parallel tenths in a row
     */
    boolean underFourSuccessiveParallelImperfectConsonances(int i, LinkedList<FirstSpeciesNode> currPath) {
        LinkedList<CantusFirmusNode> CF = IO.composition.cantusFirmus;
        int diff = this.pitch - CF.get(i).pitch;
        switch (diff) {
            case 3, 4, 8, 9, 15, 16, -3, -4, -8, -9, -15, -16:
                return (Motion.isParallel(CF.get(i - 4).pitch, CF.get(i - 3).pitch, currPath.get(i - 4).pitch, currPath.get(i - 3).pitch) && Motion.isParallel(CF.get(i - 3).pitch, CF.get(i - 2).pitch, currPath.get(i - 3).pitch, currPath.get(i - 2).pitch) && Motion.isParallel(CF.get(i - 2).pitch, CF.get(i - 1).pitch, currPath.get(i - 2).pitch, currPath.get(i - 1).pitch) && Motion.isParallel(CF.get(i - 1).pitch, CF.get(i).pitch, currPath.get(i - 1).pitch, this.pitch));
            default:
                return true;
        }
    }
    //copypasta
    private static boolean hasMotifs(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) { //checks if there's an identical sequence of three notes in the current list, or if pitches A followed immediately by B are immediately followed by A followed immediately by B (since a Fux thing was ok with A followed by B, then A followed by B reoccurring later)
        if(currPath.size() > 3) {
            LinkedList<FirstSpeciesNode> potentialPath = new LinkedList<FirstSpeciesNode>(currPath);
            potentialPath.add(n);
            for(int i = 0; i < (potentialPath.size() - 3); i++) {
                LinkedList<Integer> motif = new LinkedList<Integer>();
                motif.add(potentialPath.get(i).pitch);
                motif.add(potentialPath.get(i + 1).pitch);
                if((motif.get(0) == potentialPath.get(i + 2).pitch) && (motif.get(1) == potentialPath.get(i + 3).pitch)) {
                    return true;
                }
            }
            if(currPath.size() > 5) {
                for(int i = 0; i < (potentialPath.size() - 2); i++) {
                    LinkedList<Integer> motif = new LinkedList<Integer>();
                    motif.add(potentialPath.get(i).pitch);
                    motif.add(potentialPath.get(i + 1).pitch);
                    motif.add(potentialPath.get(i + 2).pitch);
                    for(int j = (i + 3); j < potentialPath.size() - 2; j++) {
                        if(((motif.get(0) == potentialPath.get(j).pitch) && (motif.get(1) == potentialPath.get(j + 1).pitch)) && (motif.get(2) == potentialPath.get(j + 2).pitch)) {
                            return true;
                        }
                    }
                }
            }
        } return false;
    }

    private boolean distToPenultExceedsMajThird(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) {
        return(this.startsLeapLargerThanMajThird(n) && (currPath.size() == penultPos));
    }

    private static boolean notEnoughLeaps(LinkedList<FirstSpeciesNode> currPath) {
        return(CantusFirmusNode.forceAtLeastTwoLeaps && (countLeaps(currPath).get(0) < 2));
    }

    private boolean willExceedAllowedLeaps(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) {
        if(this.startsLeapTo(n)) {
            LinkedList<Integer> leapList = countLeaps(currPath);
            if (leapList.get(0) > 3) { return true; }
            if (this.startsLeapLargerThanFourth(n)) {
                if(leapList.get(1) > 1) { return true; }
            }
        } return false;
    }

    private static LinkedList<Integer> countLeaps(LinkedList<FirstSpeciesNode> list) {
        LinkedList<Integer> leapList = new LinkedList<Integer>();
        int allLeaps = 0;
        int leapsOverFourth = 0;
        for(int i = 0; i < (list.size() - 1); i++) {
            if(list.get(i).startsLeapTo(list.get(i + 1))) {
                allLeaps += 1;
                if(list.get(i).startsLeapLargerThanFourth(list.get(i + 1))) {
                    leapsOverFourth += 1;
                }
            }
        }
        leapList.add(allLeaps);
        leapList.add(leapsOverFourth);
        return leapList;
    }

    private boolean fifthOrBiggerDoesntChangeDir(FirstSpeciesNode prevNode, int dist) {
        return(((dist >= 7) && (prevNode.startsUpwardMotionTo(this))) ||
                ((dist <= -7) && (prevNode.startsDownwardMotionTo(this))));
    }

    private boolean octOrSixthAsSecondSequentialLeap(FirstSpeciesNode n, int dist) {
        if(this.startsLeapTo(n)) {
            if(CantusFirmusNode.allSixthsPrecedeFollowStepInOppDir) {
                switch(dist) {
                    case 12, 9, 8, -8, -9, -12: return true;
                    default: return false;
                }
            } else {
                switch (dist) {
                    case 12, 8, -12: return true;
                    default: return false;
                }
            }
        } return false;
    }


    private boolean failsSameDirSuccessiveLeapIntervalRequirement(FirstSpeciesNode prevNode, FirstSpeciesNode n) {
        if((twoSequentialLeapsBetween(prevNode, this, n)) && (sameDirMotionBetween(prevNode, this, n))){
            int topInt;
            int bottomInt;
            if (prevNode.startsUpwardMotionTo(this)) {
                topInt = (n.pitch - this.pitch);
                bottomInt = (this.pitch - prevNode.pitch);
            } else {
                topInt = (prevNode.pitch - this.pitch);
                bottomInt = (this.pitch - n.pitch);
            }
            switch (topInt) {
                case 5: return((((bottomInt != 7) && (bottomInt != 4)) && (bottomInt != 3)));
                case 4: return(bottomInt != 3);
                case 3: return(bottomInt != 4);
                default: return true;
            }
        } return false;
    }

    private boolean outlinesDissonantMelodic(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) {
        if(currPath.size() >= 2) {
            FirstSpeciesNode prevNode = currPath.get(currPath.indexOf(this) - 1);
            if(!(sameDirMotionBetween(prevNode, this, n))) {
                int dir = -1;
                if (this.startsDownwardMotionTo(n)) {
                    dir = 1;
                }
                for (int i = (currPath.size() - 1); i > -1; i--) {
                    if((i == 0) || ((dir * (currPath.get(i).pitch - currPath.get(i - 1).pitch)) < 0)) {
                        return intervalIsDissonantMelodic(this.pitch, currPath.get(i).pitch);
                    }
                }
            }
        } if(penultPos == currPath.indexOf(this)) {
            FirstSpeciesNode prevNode = currPath.get(currPath.indexOf(this) - 1);
            if(sameDirMotionBetween(prevNode, this, n)) {
                int dir = 1;
                if (this.startsDownwardMotionTo(n)) {
                    dir = -1;
                }
                for (int i = (currPath.size() - 1); i > -1; i--) {
                    if ((i == 0) || ((dir * (currPath.get(i).pitch - currPath.get(i - 1).pitch)) < 0)) {
                        return intervalIsDissonantMelodic(n.pitch, currPath.get(i).pitch);
                    }
                }
            }
        }
        return false;
    }

    private boolean octAndSixthNotPrecededWithStepInOppositeDir(FirstSpeciesNode prevNode, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(prevNode, this, dist));
    }

    private boolean octAndSixthNotFollowedWithStepInOppositeDir(FirstSpeciesNode n, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(this, n, dist));
    }

    private static boolean octAndSixthNotPrecFollowStepInOppositeDirHelper(FirstSpeciesNode one, FirstSpeciesNode two, int dist) { //for octAndSixthPrecedeFollow functions, get a better name for this.
        if(CantusFirmusNode.allSixthsPrecedeFollowStepInOppDir) { //consider merging with fifth or bigger, aside from the prevNode.startsLeapTo
            switch(dist) { //has very similar logic
                case 12, 9, 8: return(one.startsUpwardMotionTo(two) || one.startsLeapTo(two));
                case -12, -9, -8: return(one.startsDownwardMotionTo(two) || one.startsLeapTo(two));
                default: break;
            }
        } else {
            switch(dist) {
                case 12, 8: return(one.startsUpwardMotionTo(two) || one.startsLeapTo(two));
                case -12: return(one.startsDownwardMotionTo(two) || one.startsLeapTo(two));
                default: break;
            }
        } return false;
    }

    private boolean twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(FirstSpeciesNode one, FirstSpeciesNode two, FirstSpeciesNode three, FirstSpeciesNode four) {
        return(twoSequentialLeapsBetween(one, two, three) && (sameDirMotionBetween(two, three, four)));
    }

    private boolean twoSuccessiveLeapsNotPrecededWithStepInOppositeDir(FirstSpeciesNode one, FirstSpeciesNode two, FirstSpeciesNode three, FirstSpeciesNode four) {
        return((twoSequentialLeapsBetween(two, three, four)) && sameDirMotionBetween(one, two, three));
    }

    private boolean P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(FirstSpeciesNode one, FirstSpeciesNode two, FirstSpeciesNode three, FirstSpeciesNode four) {
        return(((!one.startsLeapTo(two)) && two.startsLeapLargerThanMajThird(three)) && sameDirMotionBetween(one, two, three, four)); }

    private boolean diatonicSeventhsAreInFourNotesBeforeLeadingRaisedSeventh(LinkedList<FirstSpeciesNode> currPath) {
        if(CantusFirmusNode.naturalSeventhAvoidsRaisedSeventh) {
            switch(IO.mode) {
                case DORIAN, MIXOLYDIAN, PHRYGIAN, AEOLIAN, LOCRIAN: //the raised seventh is not diatonic so go ahead and check
                    if(this.pitch == (IO.tonic - 1)) { //we do have the raised seventh as penultimate tone
                        for(int i = 4; i < 0; i--) {
                            if(((currPath.get(penultPos - i).pitch) % 12) == ((IO.tonic - IO.mode.steps.get(6)) % 12)) {
                                return true;
                            }
                        }
                    } return false;
                case IONIAN, LYDIAN: return false; //the seventh is already "raised" and that's diatonic, so it's not an issue
                default: break;
            }
        } return false;
    }


}
