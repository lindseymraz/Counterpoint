package com.counterpoint;

import java.util.LinkedList;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

class Node {
    int pitch;
    LinkedList<Node> getsTo = new LinkedList<Node>();

    static boolean allSixthsPrecedeFollowStepInOppDir;
    static boolean forceAtLeastTwoLeaps;

    static int penultPos;

    Node(int pitch) {
        this.pitch = pitch;
    }

    void addEdge (Node toNode) {
        this.getsTo.add(toNode);
    }

    private boolean startsUpwardMotionTo(Node next) {
        return((next.pitch - this.pitch) > 0);
    }

    private boolean startsDownwardMotionTo(Node next) {
        return((this.pitch - next.pitch) > 0);
    }


    private static boolean sameDirMotionBetween(Node one, Node two, Node three) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) ||
                (one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)));
    }

    private static boolean sameDirMotionBetween(Node one, Node two, Node three, Node four) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) && three.startsUpwardMotionTo(four)) ||
                ((one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)) && three.startsDownwardMotionTo(four));
    }

    private static boolean intervalIsDissonantMelodic(int pitch1, int pitch2) {
        switch(pitch1 - pitch2) {
            case -16, -15, -14, -13, -11, -10, -6, 6, 10, 11, 13, 14, 15, 16: return true;
            default: return false;
        }
    }

    private boolean startsLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    private boolean startsLeapLargerThanMajThird(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    private boolean startsLeapLargerThanFourth(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 5) || (a < -5));
    }

    private static boolean twoSequentialLeapsBetween(Node one, Node two, Node three) {
        return(one.startsLeapTo(two) && two.startsLeapTo(three));
    }

    private static boolean threeSequentialLeapsBetween(Node one, Node two, Node three, Node four) {
        return((one.startsLeapTo(two) && two.startsLeapTo(three)) && three.startsLeapTo(four));
    }

    void giveRoute(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else {
            currPath.add(this);
            for (Node n : this.getsTo) {
                if (passesTests(n, currPath)) {
                    n.giveRoute(to, currPath, list);
                }
            }
            currPath.remove(this);
        }
    }

    private boolean passesTests(Node n, LinkedList<Node> currPath) {
        if(nonLastNodeIsDeadEnd()) { return false; }
        if(alreadyHasClimax(n, currPath)) { return false; }
        if(shouldHaveClimaxAndDoesnt(currPath)) { return false; }
        if(currPath.size() >= 2) {
            Node prevNode = currPath.get((currPath.indexOf(this) - 1));
            int dist = n.pitch - this.pitch;
            if(octAndSixthNotPrecededWithStepInOppositeDir(prevNode, dist)) { return false; }
            if(octAndSixthNotFollowedWithStepInOppositeDir(n, (this.pitch - prevNode.pitch))) { return false; }
            if(fifthOrBiggerDoesntChangeDir(prevNode, dist)) { return false; }
            if(failsSameDirSuccessiveLeapIntervalRequirement(prevNode, n)) { return false; }
            if(currPath.size() >= 3) {
                Node prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
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

    private static boolean alreadyHasClimax(Node n, LinkedList<Node> currPath) {
        return((n.pitch == IO.climax) && hasClimax(currPath));
    }

    private static boolean hasClimax(LinkedList<Node> list) {
        for(Node n : list) {
            if(n.pitch == IO.climax) {
                return true;
            }
        } return false;
    }

    private static boolean shouldHaveClimaxAndDoesnt(LinkedList<Node> currPath) {
        return((currPath.size() >= IO.climaxLateBound) && (!hasClimax(currPath)));
    }

    private static boolean hasMotifs(Node n, LinkedList<Node> currPath) { //checks if there's an identical sequence of three notes in the current list, or if pitches A followed immediately by B are immediately followed by A followed immediately by B (since a Fux thing was ok with A followed by B, then A followed by B reoccurring later)
        if(currPath.size() > 3) {
            LinkedList<Node> potentialPath = new LinkedList<Node>(currPath);
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

    private boolean distToPenultExceedsMajThird(Node n, LinkedList<Node> currPath) {
        return(this.startsLeapLargerThanMajThird(n) && (currPath.size() == penultPos));
    }

    private static boolean notEnoughLeaps(LinkedList<Node> currPath) {
        return(forceAtLeastTwoLeaps && ((currPath.size() == (penultPos)) && (countLeaps(currPath).get(0) < 2)));
    }

    private boolean willExceedAllowedLeaps(Node n, LinkedList<Node> currPath) {
        if(this.startsLeapTo(n)) {
            LinkedList<Integer> leapList = countLeaps(currPath);
            if (leapList.get(0) > 3) { return true; }
            if (this.startsLeapLargerThanFourth(n)) {
                if(leapList.get(1) > 1) { return true; }
            }
        } return false;
    }

    private static LinkedList<Integer> countLeaps(LinkedList<Node> list) {
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

    private boolean fifthOrBiggerDoesntChangeDir(Node prevNode, int dist) {
        return(((dist >= 7) && (prevNode.startsUpwardMotionTo(this))) ||
               ((dist <= -7) && (prevNode.startsDownwardMotionTo(this))));
    }

    private boolean octOrSixthAsSecondSequentialLeap(Node n, int dist) {
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


    private boolean failsSameDirSuccessiveLeapIntervalRequirement(Node prevNode, Node n) {
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

    private boolean outlinesDissonantMelodic(Node n, LinkedList<Node> currPath) {
        if(currPath.size() >= 2) {
            Node prevNode = currPath.get(currPath.indexOf(this) - 1);
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
            Node prevNode = currPath.get(currPath.indexOf(this) - 1);
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

    private boolean octAndSixthNotPrecededWithStepInOppositeDir(Node prevNode, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(prevNode, this, dist));
    }

    private boolean octAndSixthNotFollowedWithStepInOppositeDir(Node n, int dist) {
        return(octAndSixthNotPrecFollowStepInOppositeDirHelper(this, n, dist));
    }

    private static boolean octAndSixthNotPrecFollowStepInOppositeDirHelper(Node one, Node two, int dist) { //for octAndSixthPrecedeFollow functions, get a better name for this.
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

    private static boolean twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        return(twoSequentialLeapsBetween(one, two, three) && (sameDirMotionBetween(two, three, four)));
    }

    private static boolean twoSuccessiveLeapsNotPrecededWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        return((twoSequentialLeapsBetween(two, three, four)) && sameDirMotionBetween(one, two, three));
    }

    private static boolean P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        return(((!one.startsLeapTo(two)) && two.startsLeapLargerThanMajThird(three)) && sameDirMotionBetween(one, two, three, four)); }
}
