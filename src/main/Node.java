import java.util.LinkedList;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors


public class Node {
    int pitch;
    LinkedList<Node> getsTo = new LinkedList<Node>();

    Node(int pitch) {
        this.pitch = pitch;
    }

    public void addEdge (Node toNode) {
        this.getsTo.add(toNode);
    }

    boolean hasClimax(LinkedList<Node> list, int climax) {
        for(Node n : list) {
            if(n.pitch == climax) {
                return true;
            }
        } return false;
    }

    void giveRoute(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list, int earlyBound, int lateBound, int climax, int penultPos, boolean allSixthsPrecedeFollowStepInOppDir, boolean forceAtLeastTwoLeaps) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (giveRouteHelper(this, currPath, climax, earlyBound, lateBound)){
            currPath.add(this);
            for (Node n : this.getsTo) {
                if (((leapHelper(this, n, currPath, penultPos, allSixthsPrecedeFollowStepInOppDir, forceAtLeastTwoLeaps)) && doesntOutlineDissonantMelodic(this, n, currPath, penultPos)) && noMotifs(currPath, n)) {
                    n.giveRoute(to, currPath, list, earlyBound, lateBound, climax, penultPos, allSixthsPrecedeFollowStepInOppDir, forceAtLeastTwoLeaps);
                }
            }
            currPath.remove(this);
        }
    }

    boolean noMotifs(LinkedList<Node> currPath, Node n) { //checks if there's an identical sequence of three notes in the current list, or if pitches A followed immediately by B are immediately followed by A followed immediately by B (since a Fux thing was ok with A followed by B, then A followed by B reoccurring later)
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

    boolean giveRouteHelper(Node n, LinkedList<Node> currPath, int climax, int earlyBound, int lateBound) {
        if(!(n.getsTo.size() > 0)) {return false;}
        if(n.pitch == climax) {
            if((currPath.size() + 1) < earlyBound) {
                return false;
            } else if((currPath.size()) >= lateBound) {
                return false;
            } else if(hasClimax(currPath, climax)) {
                return false;
            }
        }
        if(currPath.size() >= lateBound) {
            if(!hasClimax(currPath, climax)) {
                return false;
            }
        }
        return true;
    }

    boolean leapHelper(Node curr, Node n, LinkedList<Node> currPath, int penultPos, boolean allSixthsPrecedeFollowStepInOppDir, boolean forceAtLeastTwoLeaps) {
        if (curr.startsLeapTo(n)) {
            if(exceedsAllowedLeaps(curr, n, currPath)) { return false; }
            if(curr.startsBigLeapTo(n) && (currPath.size() == penultPos)) { return false; }
            if(currPath.size() >= 2) {
                Node prevNode = currPath.get((currPath.indexOf(curr) - 1));
                int dist = n.pitch - curr.pitch;
                if(octAndSixthNotPrecededWithStepInOppositeDir(prevNode, curr, dist, allSixthsPrecedeFollowStepInOppDir)) { return false; }
                if(fifthOrBiggerDoesntChangeDir(prevNode, curr, dist)) { return false; }
                if (currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                    if (twoSequentialLeapsBetween(prevPrevNode, prevNode, curr)) { return false; }
                    if (octOrSixthAsSecondSequentialLeap((curr.pitch - prevNode.pitch), allSixthsPrecedeFollowStepInOppDir)) { return false; }
                }
                if (prevNode.startsLeapTo(curr)) {
                    if(failsSameDirSuccessiveLeapIntervalRequirement(prevNode, curr, n)) { return false; }
                    if(currPath.size() >= 3) {
                        Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                        if(sameDirMotionBetween(prevPrevNode, prevNode, curr)) {
                            return false; //if you're here, you have a leap and are considering the second, and
                            //that first leap doesn't begin at the cantus firmus' start, so you'll need to make sure
                            //the first leap isn't preceded by motion in the same direction as the leap
                        }
                    }
                }
            }
        } else {
            if (currPath.size() >= 2) {
                Node prevNode = currPath.get(currPath.indexOf(curr) - 1);
                if(octAndSixthNotFollowedWithStepInOppositeDir(curr, n, (curr.pitch - prevNode.pitch), allSixthsPrecedeFollowStepInOppDir)) { return false; }
                if(currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                    if(twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(prevPrevNode, prevNode, curr, n)) { return false; }
                    if(P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(prevPrevNode, prevNode, curr, n)) { return false; }
                    if(forceAtLeastTwoLeaps && notEnoughLeaps(currPath, penultPos)) { return false; }
                    }
                }
            }
        return true;
    }

    boolean notEnoughLeaps(LinkedList<Node> currPath, int penultPos) {
        return((currPath.size() == (penultPos - 1)) && (countLeaps(currPath).get(0) < 2));
    }

    boolean startsLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    boolean startsLeapLargerThanFourth(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 5) || (a < -5));
    }

    boolean exceedsAllowedLeaps(Node curr, Node n, LinkedList<Node> currPath) {
        LinkedList<Integer> leapList = countLeaps(currPath);
        if (leapList.get(0) > 3) { return true; }
        if (curr.startsLeapLargerThanFourth(n)) {
            if (leapList.get(1) > 1) { return true; }
        }
        return false;
    }

    LinkedList<Integer> countLeaps(LinkedList<Node> list) {
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

    boolean startsBigLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    boolean octAndSixthNotPrecededWithStepInOppositeDir(Node prevNode, Node curr, int dist, boolean allSixthsPrecedeFollowStepInOppDir) {
        if(allSixthsPrecedeFollowStepInOppDir) {
            switch(dist) {
                case 12, 9, 8: { if(prevNode.startsUpwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return true; } break;}
                case -12, -9, -8: { if(prevNode.startsDownwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return true; } break;}
                default: break;
            }
        } else {
            switch(dist) {
                case 12, 8: { if(prevNode.startsUpwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return true; } break;}
                case -12: { if(prevNode.startsDownwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return true; } break;}
                default: break;
            }
        }
        return false;
    }

    boolean startsUpwardMotionTo(Node next) {
        return((next.pitch - this.pitch) > 0);
    }

    boolean startsDownwardMotionTo(Node next) {
        return((this.pitch - next.pitch) > 0);
    }

    boolean fifthOrBiggerDoesntChangeDir(Node prevNode, Node curr, int dist) {
        if(dist >= 7) {
            if(prevNode.startsUpwardMotionTo(curr)) { return true; }
        }
        if(dist <= -7) {
            if(prevNode.startsDownwardMotionTo(curr)) { return true; }
        } return false;
    }

    boolean twoSequentialLeapsBetween(Node one, Node two, Node three) {
        return(one.startsLeapTo(two) && two.startsLeapTo(three));
    }

    boolean octOrSixthAsSecondSequentialLeap(int dist, boolean allSixthsPrecedeFollowStepInOppDir) {
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

    boolean failsSameDirSuccessiveLeapIntervalRequirement(Node prevNode, Node curr, Node n) {
        if(sameDirMotionBetween(prevNode, curr, n)) {
            int topInt;
            int bottomInt;
            if (prevNode.startsUpwardMotionTo(curr)) {
                topInt = (n.pitch - curr.pitch);
                bottomInt = (curr.pitch - prevNode.pitch);
            } else {
                topInt = (prevNode.pitch - curr.pitch);
                bottomInt = (curr.pitch - n.pitch);
            }
            switch (topInt) {
                case 5: return((((bottomInt != 7) && (bottomInt != 4)) && (bottomInt != 3)));
                case 4: return(bottomInt != 3);
                case 3: return(bottomInt != 4);
                default: return true;
            }
        } return false;
    }

    boolean sameDirMotionBetween(Node prev, Node curr, Node n) {
        return((prev.startsUpwardMotionTo(curr) && curr.startsUpwardMotionTo(n)) ||
               (prev.startsDownwardMotionTo(curr) && curr.startsDownwardMotionTo(n)));
    }

    boolean sameDirMotionBetween(Node prevPrev, Node prev, Node curr, Node n) {
        return((prevPrev.startsUpwardMotionTo(prev) && prev.startsUpwardMotionTo(curr)) && curr.startsUpwardMotionTo(n)) ||
              ((prevPrev.startsDownwardMotionTo(prev) && prev.startsDownwardMotionTo(curr)) && curr.startsDownwardMotionTo(n));
    }

    boolean doesntOutlineDissonantMelodic(Node curr, Node n, LinkedList<Node> currPath, int penultPos) {
        if(currPath.size() >= 2) {
            Node prevNode = currPath.get(currPath.indexOf(curr) - 1);
            if(!(sameDirMotionBetween(prevNode, curr, n))) {
                int dir = -1;
                if (curr.startsDownwardMotionTo(n)) {
                    dir = 1;
                }
                for (int i = (currPath.size() - 1); i > -1; i--) {
                    if((i == 0) || ((dir * (currPath.get(i).pitch - currPath.get(i - 1).pitch)) < 0)) {
                        return intervalIsntDissonantMelodic(curr.pitch, currPath.get(i).pitch);
                    }
                }
            }
        } if(penultPos == currPath.indexOf(curr)) {
            Node prevNode = currPath.get(currPath.indexOf(curr) - 1);
            if(sameDirMotionBetween(prevNode, curr, n)) {
                int dir = 1;
                if (curr.startsDownwardMotionTo(n)) {
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

    boolean intervalIsntDissonantMelodic(int pitch1, int pitch2) {
        switch(pitch1 - pitch2) {
            case -16, -15, -14, -13, -11, -10, -6, 6, 10, 11, 13, 14, 15, 16: return false;
            default: return true;
        }
    }

    boolean octAndSixthNotFollowedWithStepInOppositeDir(Node curr, Node n, int dist, boolean allSixthsPrecedeFollowStepInOppDir) {
        if(allSixthsPrecedeFollowStepInOppDir) {
            switch (dist) {
                case 12, 9, 8: return(curr.startsUpwardMotionTo(n));
                case -12, -9, -8: return(curr.startsDownwardMotionTo(n));
                default: return false;
            }
        } else {
            switch (dist) {
                case 12, 8: return(curr.startsUpwardMotionTo(n));
                case -12: return(curr.startsDownwardMotionTo(n));
                default: return false;
            }
        }
    }

    boolean twoSuccessiveLeapsNotFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        return(twoSequentialLeapsBetween(one, two, three) && (sameDirMotionBetween(two, three, four)));
    }

    boolean P4AndUpLeapsNotPrecededOrFollowedWithStepInOppositeDir(Node one, Node two, Node three, Node four) {
        if((!one.startsLeapTo(two)) && two.startsBigLeapTo(three)) {
            return(sameDirMotionBetween(one, two, three, four));
        } return false;
    }

    void printLinkedList(LinkedList<Node> a) {
        int size = (a.size() - 1);
        for(int i = 0; (i < size); i++) {
            System.out.print(a.get(i).pitch + ", ");
        }
        System.out.print(a.get(size).pitch + "\n");
    }

    void printMaxStyle(LinkedList<Node> a, int acc) {
        int size = (a.size() - 1);
        System.out.print(acc + ", ");
        for(int i = 0; (i < size); i++) {
            System.out.print(a.get(i).pitch + " ");
        }
        System.out.print(a.get(size).pitch + ";\n");
    }

    String writeFile(LinkedList<Node> a, int acc) {
        int size = (a.size() - 1);
        String str = (acc + ", ");
        for(int i = 0; (i < size); i++) {
            str = str + (a.get(i).pitch + " ");
        }
        return(str + (a.get(size).pitch + ";\n"));
    }

}
