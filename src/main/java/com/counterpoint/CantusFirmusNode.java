package com.counterpoint;

import java.util.LinkedList;

public class CantusFirmusNode extends Node<CantusFirmusNode> {

    static boolean allSixthsPrecedeFollowStepInOppDir;
    static boolean forceAtLeastTwoLeaps;

    CantusFirmusNode(int pitch) {
        super(pitch);
    }

    void giveRoute(CantusFirmusNode to, LinkedList<CantusFirmusNode> currPath, LinkedList<LinkedList<CantusFirmusNode>> list) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<CantusFirmusNode>(currPath));
            currPath.remove(this);
        } else {
            currPath.add(this);
            for (CantusFirmusNode n : this.getsTo) {
                if (passesTests(n, currPath)) {
                    n.giveRoute(to, currPath, list);
                }
            }
            currPath.remove(this);
        }
    }

    boolean passesTests(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) {
        if(nonLastNodeIsDeadEnd()) { return false; }
        if(alreadyHasClimax(n, currPath)) { return false; }
        if(shouldHaveClimaxAndDoesnt(currPath)) { return false; }
        if(currPath.size() >= 2) {
            CantusFirmusNode prevNode = currPath.get((currPath.indexOf(this) - 1));
            int dist = n.pitch - this.pitch;
            if(octAndSixthNotPrecededWithStepInOppositeDir(prevNode, dist)) { return false; }
            if(octAndSixthNotFollowedWithStepInOppositeDir(n, (this.pitch - prevNode.pitch))) { return false; }
            if(fifthOrBiggerDoesntChangeDir(prevNode, dist)) { return false; }
            if(failsSameDirSuccessiveLeapIntervalRequirement(prevNode, n)) { return false; }
            if(currPath.size() >= 3) {
                CantusFirmusNode prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
                if(willExceedAllowedLeaps(n, currPath)) { return false; }
                if(threeSequentialLeapsBetween(prevPrevNode, prevNode, this, n)) { return false; }
                if(octOrSixthAsSecondSequentialLeap(n, (this.pitch - prevNode.pitch))) { return false; }
                if(hasMotifs(n, currPath)) { return false; }
                if(outlinesDissonantMelodic(n, currPath)) { return false; }
                if(twoSuccessiveLeapsNotPrecededWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                if(distToPenultExceedsMajThird(n, currPath)) { return false; }
                if(notEnoughLeaps(currPath)) { return false; }
            }
        } return true;
    }

    private boolean nonLastNodeIsDeadEnd() {
        return(this.getsTo.size() < 1);
    }

    private static boolean alreadyHasClimax(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) {
        return((n.pitch == IO.climax) && hasClimax(currPath));
    }

    private static boolean hasClimax(LinkedList<CantusFirmusNode> list) {
        for(CantusFirmusNode n : list) {
            if(n.pitch == IO.climax) {
                return true;
            }
        } return false;
    }

    private static boolean shouldHaveClimaxAndDoesnt(LinkedList<CantusFirmusNode> currPath) {
        return((currPath.size() >= IO.climaxLateBound) && (!hasClimax(currPath)));
    }

    private static boolean hasMotifs(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) { //checks if there's an identical sequence of three notes in the current list, or if pitches A followed immediately by B are immediately followed by A followed immediately by B (since a Fux thing was ok with A followed by B, then A followed by B reoccurring later)
        if(currPath.size() > 3) {
            LinkedList<CantusFirmusNode> potentialPath = new LinkedList<CantusFirmusNode>(currPath);
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

    private boolean distToPenultExceedsMajThird(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) {
        return(this.startsLeapLargerThanMajThird(n) && (currPath.size() == penultPos));
    }

    private static boolean notEnoughLeaps(LinkedList<CantusFirmusNode> currPath) {
        return(forceAtLeastTwoLeaps && ((currPath.size() == (penultPos)) && (countLeaps(currPath).get(0) < 2)));
    }

    private boolean willExceedAllowedLeaps(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) {
        if(this.startsLeapTo(n)) {
            LinkedList<Integer> leapList = countLeaps(currPath);
            if (leapList.get(0) > 3) { return true; }
            if (this.startsLeapLargerThanFourth(n)) {
                if(leapList.get(1) > 1) { return true; }
            }
        } return false;
    }

    private static LinkedList<Integer> countLeaps(LinkedList<CantusFirmusNode> list) {
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

    private boolean fifthOrBiggerDoesntChangeDir(CantusFirmusNode prevNode, int dist) {
        return(((dist >= 7) && (prevNode.startsUpwardMotionTo(this))) ||
                ((dist <= -7) && (prevNode.startsDownwardMotionTo(this))));
    }

    private boolean octOrSixthAsSecondSequentialLeap(CantusFirmusNode n, int dist) {
        if(this.startsLeapTo(n)) {
            if(allSixthsPrecedeFollowStepInOppDir) {
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


    private boolean failsSameDirSuccessiveLeapIntervalRequirement(CantusFirmusNode prevNode, CantusFirmusNode n) {
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

    private boolean outlinesDissonantMelodic(CantusFirmusNode n, LinkedList<CantusFirmusNode> currPath) {
        if(currPath.size() >= 2) {
            CantusFirmusNode prevNode = currPath.get(currPath.indexOf(this) - 1);
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
            CantusFirmusNode prevNode = currPath.get(currPath.indexOf(this) - 1);
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

    private boolean octAndSixthNotPrecededWithStepInOppositeDir(CantusFirmusNode prevNode, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(prevNode, this, dist));
    }

    private boolean octAndSixthNotFollowedWithStepInOppositeDir(CantusFirmusNode n, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(this, n, dist));
    }

    private static boolean octAndSixthNotPrecFollowStepInOppositeDirHelper(CantusFirmusNode one, CantusFirmusNode two, int dist) { //for octAndSixthPrecedeFollow functions, get a better name for this.
        if(allSixthsPrecedeFollowStepInOppDir) { //consider merging with fifth or bigger, aside from the prevNode.startsLeapTo
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

    private boolean twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(CantusFirmusNode one, CantusFirmusNode two, CantusFirmusNode three, CantusFirmusNode four) {
        return(twoSequentialLeapsBetween(one, two, three) && (sameDirMotionBetween(two, three, four)));
    }

    private boolean twoSuccessiveLeapsNotPrecededWithStepInOppositeDir(CantusFirmusNode one, CantusFirmusNode two, CantusFirmusNode three, CantusFirmusNode four) {
        return((twoSequentialLeapsBetween(two, three, four)) && sameDirMotionBetween(one, two, three));
    }

    private boolean P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(CantusFirmusNode one, CantusFirmusNode two, CantusFirmusNode three, CantusFirmusNode four) {
        return(((!one.startsLeapTo(two)) && two.startsLeapLargerThanMajThird(three)) && sameDirMotionBetween(one, two, three, four)); }
}
