import java.util.LinkedList;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

class Node {
    int pitch;
    LinkedList<Node> getsTo = new LinkedList<Node>();

    static boolean allSixthsPrecedeFollowStepInOppDir;
    static boolean forceAtLeastTwoLeaps;

    static int climax; //actual pitch value, not just pitch class
    static int climaxEarlyBound; //earliest climax can occur
    static int climaxLateBound; //latest climax can occur
    static int penultPos;

    Node(int pitch) {
        this.pitch = pitch;
    }

    void addEdge (Node toNode) {
        this.getsTo.add(toNode);
    }

    private boolean hasClimax(LinkedList<Node> list) {
        for(Node n : list) {
            if(n.pitch == climax) {
                return true;
            }
        } return false;
    }

    void giveRoute(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (giveRouteHelper(this, currPath)) {
            currPath.add(this);
            for (Node n : this.getsTo) {
                if (((leapHelper(n, currPath)) && doesntOutlineDissonantMelodic(n, currPath)) && noMotifs(currPath, n)) {
                    n.giveRoute(to, currPath, list);
                }
            }
            currPath.remove(this);
        }
    }

    private boolean noMotifs(LinkedList<Node> currPath, Node n) { //checks if there's an identical sequence of three notes in the current list, or if pitches A followed immediately by B are immediately followed by A followed immediately by B (since a Fux thing was ok with A followed by B, then A followed by B reoccurring later)
        if(currPath.size() > 3) {
            LinkedList<Node> potPath = new LinkedList<Node>(currPath);
            potPath.add(n);
            for(int i = 0; i < (potPath.size() - 3); i++) {
                LinkedList<Integer> motif = new LinkedList<Integer>();
                motif.add(potPath.get(i).pitch);
                motif.add(potPath.get(i + 1).pitch);
                if((motif.get(0) == potPath.get(i + 2).pitch) && (motif.get(1) == potPath.get(i + 3).pitch)) {
                    return false;
                }
            }
            if(currPath.size() > 5) {
                for(int i = 0; i < (potPath.size() - 2); i++) {
                    LinkedList<Integer> motif = new LinkedList<Integer>();
                    motif.add(potPath.get(i).pitch);
                    motif.add(potPath.get(i + 1).pitch);
                    motif.add(potPath.get(i + 2).pitch);
                    for(int j = (i + 3); j < potPath.size() - 2; j++) {
                        if(((motif.get(0) == potPath.get(j).pitch) && (motif.get(1) == potPath.get(j + 1).pitch)) && (motif.get(2) == potPath.get(j + 2).pitch)) {
                            return false;
                        }
                    }
                }
            }
        } return true;
    }

    private boolean giveRouteHelper(Node n, LinkedList<Node> currPath) {
        if(!(n.getsTo.size() > 0)) {return false;}
        if(n.pitch == climax) {
            if((currPath.size() + 1) < climaxEarlyBound) {
                return false;
            } else if((currPath.size()) >= climaxLateBound) {
                return false;
            } else if(hasClimax(currPath)) {
                return false;
            }
        }
        if(currPath.size() >= climaxLateBound) {
            if(!hasClimax(currPath)) {
                return false;
            }
        }
        return true;
    }

    private boolean leapHelper(Node n, LinkedList<Node> currPath) {
        if (this.startsLeapTo(n)) {
            if(exceedsAllowedLeaps(n, currPath)) { return false; }
            if(this.startsBigLeapTo(n) && (currPath.size() == penultPos)) { return false; }
            if(currPath.size() >= 2) {
                Node prevNode = currPath.get((currPath.indexOf(this) - 1));
                int dist = n.pitch - this.pitch;
                if(octAndSixthNotPrecededWithStepInOppositeDir(prevNode, dist)) { return false; }
                if(fifthOrBiggerDoesntChangeDir(prevNode, dist)) { return false; }
                if (currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
                    if (twoSequentialLeapsBetween(prevPrevNode, prevNode, this)) { return false; }
                    if (octOrSixthAsSecondSequentialLeap((this.pitch - prevNode.pitch))) { return false; }
                }
                if (prevNode.startsLeapTo(this)) {
                    if(failsSameDirSuccessiveLeapIntervalRequirement(prevNode, n)) { return false; }
                    if(currPath.size() >= 3) {
                        Node prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
                        if(sameDirMotionBetween(prevPrevNode, prevNode, this)) {
                            return false; //if you're here, you have a leap and are considering the second, and
                            //that first leap doesn't begin at the cantus firmus' start, so you'll need to make sure
                            //the first leap isn't preceded by motion in the same direction as the leap
                        }
                    }
                }
            }
        } else {
            if (currPath.size() >= 2) {
                Node prevNode = currPath.get(currPath.indexOf(this) - 1);
                if(octAndSixthNotFollowedWithStepInOppositeDir(n, (this.pitch - prevNode.pitch))) { return false; }
                if(currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(this) - 2));
                    if(twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                    if(P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(prevPrevNode, prevNode, this, n)) { return false; }
                    if(forceAtLeastTwoLeaps && notEnoughLeaps(currPath)) { return false; }
                    }
                }
            }
        return true;
    }

    private boolean notEnoughLeaps(LinkedList<Node> currPath) {
        return((currPath.size() == (penultPos - 1)) && (countLeaps(currPath).get(0) < 2));
    }

    private boolean startsLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    private boolean startsLeapLargerThanFourth(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 5) || (a < -5));
    }

    private boolean exceedsAllowedLeaps(Node n, LinkedList<Node> currPath) {
        LinkedList<Integer> leapList = countLeaps(currPath);
        if (leapList.get(0) > 3) { return true; }
        if (this.startsLeapLargerThanFourth(n)) {
            if(leapList.get(1) > 1) { return true; }
        }
        return false;
    }

    private LinkedList<Integer> countLeaps(LinkedList<Node> list) {
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

    private boolean startsBigLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    private boolean octAndSixthNotPrecededWithStepInOppositeDir(Node prevNode, int dist) {
        if(allSixthsPrecedeFollowStepInOppDir) {
            switch(dist) {
                case 12, 9, 8: { if(prevNode.startsUpwardMotionTo(this) || prevNode.startsLeapTo(this)) { return true; } break;}
                case -12, -9, -8: { if(prevNode.startsDownwardMotionTo(this) || prevNode.startsLeapTo(this)) { return true; } break;}
                default: break;
            }
        } else {
            switch(dist) {
                case 12, 8: { if(prevNode.startsUpwardMotionTo(this) || prevNode.startsLeapTo(this)) { return true; } break;}
                case -12: { if(prevNode.startsDownwardMotionTo(this) || prevNode.startsLeapTo(this)) { return true; } break;}
                default: break;
            }
        }
        return false;
    }

    private boolean startsUpwardMotionTo(Node next) {
        return((next.pitch - this.pitch) > 0);
    }

    private boolean startsDownwardMotionTo(Node next) {
        return((this.pitch - next.pitch) > 0);
    }

    private boolean fifthOrBiggerDoesntChangeDir(Node prevNode, int dist) {
        if(dist >= 7) {
            if(prevNode.startsUpwardMotionTo(this)) { return true; }
        }
        if(dist <= -7) {
            if(prevNode.startsDownwardMotionTo(this)) { return true; }
        } return false;
    }

    private boolean twoSequentialLeapsBetween(Node one, Node two, Node three) {
        return(one.startsLeapTo(two) && two.startsLeapTo(three));
    }

    private boolean octOrSixthAsSecondSequentialLeap(int dist) {
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
    }

    private boolean failsSameDirSuccessiveLeapIntervalRequirement(Node prevNode, Node n) {
        if(sameDirMotionBetween(prevNode, this, n)) {
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

    private static boolean sameDirMotionBetween(Node one, Node two, Node three) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) ||
               (one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)));
    }

    private static boolean sameDirMotionBetween(Node one, Node two, Node three, Node four) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) && three.startsUpwardMotionTo(four)) ||
              ((one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)) && three.startsDownwardMotionTo(four));
    }

    private boolean doesntOutlineDissonantMelodic(Node n, LinkedList<Node> currPath) {
        if(currPath.size() >= 2) {
            Node prevNode = currPath.get(currPath.indexOf(this) - 1);
            if(!(sameDirMotionBetween(prevNode, this, n))) {
                int dir = -1;
                if (this.startsDownwardMotionTo(n)) {
                    dir = 1;
                }
                for (int i = (currPath.size() - 1); i > -1; i--) {
                    if((i == 0) || ((dir * (currPath.get(i).pitch - currPath.get(i - 1).pitch)) < 0)) {
                        return intervalIsntDissonantMelodic(this.pitch, currPath.get(i).pitch);
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
                        return intervalIsntDissonantMelodic(n.pitch, currPath.get(i).pitch);
                    }
                }
            }
        }
        return true;
    }

    private boolean intervalIsntDissonantMelodic(int pitch1, int pitch2) {
        switch(pitch1 - pitch2) {
            case -16, -15, -14, -13, -11, -10, -6, 6, 10, 11, 13, 14, 15, 16: return false;
            default: return true;
        }
    }

    private boolean octAndSixthNotFollowedWithStepInOppositeDir(Node n, int dist) {
        if(allSixthsPrecedeFollowStepInOppDir) {
            switch (dist) {
                case 12, 9, 8: return(this.startsUpwardMotionTo(n));
                case -12, -9, -8: return(this.startsDownwardMotionTo(n));
                default: return false;
            }
        } else {
            switch (dist) {
                case 12, 8: return(this.startsUpwardMotionTo(n));
                case -12: return(this.startsDownwardMotionTo(n));
                default: return false;
            }
        }
    }

    private boolean twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        return(twoSequentialLeapsBetween(one, two, three) && (sameDirMotionBetween(two, three, four)));
    }

    private boolean P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        if((!one.startsLeapTo(two)) && two.startsBigLeapTo(three)) {
            return(sameDirMotionBetween(one, two, three, four));
        } return false;
    }
}
