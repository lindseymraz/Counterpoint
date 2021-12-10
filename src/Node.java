import java.util.LinkedList;

public class Node {
    int pitch;
    LinkedList<Node> getsTo = new LinkedList<Node>();

    Node(int pitch) {
        this.pitch = pitch;
    }

    Node(int pitch, LinkedList<Node> getsTo) {
        this.pitch = pitch;
        this.getsTo = getsTo;
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

    void giveRoute(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list, int earlyBound, int lateBound, int climax, int length) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (giveRouteHelper(this, currPath, climax, earlyBound, lateBound)){
            currPath.add(this);
            for (Node n : this.getsTo) {
                if (leapHelper(this, n, currPath, length)) {
                    n.giveRoute(to, currPath, list, earlyBound, lateBound, climax, length);
                }
            }
            currPath.remove(this);
        }
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

    boolean leapHelper(Node curr, Node n, LinkedList<Node> currPath, int length) {
        if (curr.startsLeapTo(n)) {
            if(isLeapCountBad(curr, n, currPath)) { return false; }
            if(curr.startsBigLeapTo(n) && (currPath.size() == (length - 2))) { return false; }
            if(currPath.size() >= 2) {
                Node prevNode = currPath.get((currPath.indexOf(curr) - 1));
                int dist = n.pitch - curr.pitch;
                switch(dist) {
                    case 12, 8: { if(prevNode.startsUpwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return false; } break;}
                    case -12: { if(prevNode.startsDownwardMotionTo(curr) || prevNode.startsLeapTo(curr)) { return false; } break;}
                    default: break;
                }
                if (currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                    if (prevPrevNode.startsLeapTo(prevNode) && prevNode.startsLeapTo(curr)) {
                        return false;
                    }
                    int dist3 = curr.pitch - prevNode.pitch;
                    switch(dist3) {
                        case 12, 8, -12: return false;
                        default: break;
                    }
                }
                if (prevNode.startsLeapTo(curr)) {
                    if (!((prevNode.startsUpwardMotionTo(curr) && curr.startsDownwardMotionTo(n)) ||
                            (prevNode.startsDownwardMotionTo(curr) && curr.startsUpwardMotionTo(n)))) {
                        int topInt;
                        int bottomInt;
                        if (prevNode.startsUpwardMotionTo(curr) && curr.startsUpwardMotionTo(n)) {
                            topInt = (n.pitch - curr.pitch);
                            bottomInt = (curr.pitch - prevNode.pitch);
                        } else {
                            topInt = (prevNode.pitch - curr.pitch);
                            bottomInt = (curr.pitch - n.pitch);
                        }
                        switch (topInt) {
                            case 5:
                                if ((((bottomInt != 7) && (bottomInt != 4)) && (bottomInt != 3))) {
                                    return false;
                                }
                                break;
                            case 4:
                                if (bottomInt != 3) {
                                    return false;
                                }
                                break;
                            case 3:
                                if (bottomInt != 4) {
                                    return false;
                                }
                                break;
                            default:
                                return false;
                        }
                    } if(currPath.size() >= 3) {
                        Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                        if(prevPrevNode.startsUpwardMotionTo(prevNode) && prevNode.startsUpwardMotionTo(curr)) {
                            return false;
                        }
                        if(prevPrevNode.startsDownwardMotionTo(prevNode) && prevNode.startsDownwardMotionTo(curr)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            if (currPath.size() >= 2) {
                Node prevNode = currPath.get(currPath.indexOf(curr) - 1);
                int dist = curr.pitch - prevNode.pitch;
                switch (dist) {
                    case 12, 8:
                        if (curr.startsUpwardMotionTo(n)) {
                            return false;
                        }
                        break;
                    case -12:
                        if (curr.startsDownwardMotionTo(n)) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }
                if(currPath.size() >= 3) {
                    Node prevPrevNode = currPath.get((currPath.indexOf(curr) - 2));
                    if(prevPrevNode.startsLeapTo(prevNode) && prevNode.startsLeapTo(curr)) {
                        if(prevNode.startsUpwardMotionTo(curr) && curr.startsUpwardMotionTo(n)) {
                            return false;
                        } if(prevNode.startsDownwardMotionTo(curr) && curr.startsDownwardMotionTo(n)) {
                            return false;
                        }
                    } else {
                        if((!prevPrevNode.startsLeapTo(prevNode)) && prevNode.startsBigLeapTo(curr)) {
                            if((prevPrevNode.startsUpwardMotionTo(prevNode) && prevNode.startsUpwardMotionTo(curr)) && curr.startsUpwardMotionTo(n)) {
                                return false;
                            }if((prevPrevNode.startsDownwardMotionTo(prevNode) && prevNode.startsDownwardMotionTo(curr)) && curr.startsDownwardMotionTo(n)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    boolean startsLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    boolean isLeapCountBad(Node curr, Node n, LinkedList<Node> currPath) {
        LinkedList<Integer> leapList = countLeaps(currPath);
        if (leapList.get(0) > 3) { return true; }
        if (curr.startsBigLeapTo(n)) {
            if (leapList.get(1) > 1) { return true; }
        }
        return false;
    }

    LinkedList<Integer> countLeaps(LinkedList<Node> list) {
        LinkedList<Integer> leapList = new LinkedList<Integer>();
        int allLeaps = 0;
        int bigLeaps = 0;
        for(int i = 0; i < (list.size() - 1); i++) {
            if(list.get(i).startsLeapTo(list.get(i + 1))) {
                allLeaps += 1;
                if(list.get(i).startsBigLeapTo(list.get(i + 1))) {
                    bigLeaps += 1;
                }
            }
        }
        leapList.add(allLeaps);
        leapList.add(bigLeaps);
        return leapList;
    }

    boolean startsBigLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    boolean startsUpwardMotionTo(Node next) {
        return((next.pitch - this.pitch) > 0);
    }

    boolean startsDownwardMotionTo(Node next) {
        return((this.pitch - next.pitch) > 0);
    }

    void printLots(Node to, int earlyBound, int laterBound, int climax, int length) {
        LinkedList<LinkedList<Node>> cantusFirmi = new LinkedList<LinkedList<Node>>();
        this.giveRoute(to, new LinkedList<Node>(), cantusFirmi, earlyBound, laterBound, climax, length);
        for(LinkedList<Node> cantus : cantusFirmi) {
            printLinkedList(cantus);
        }
        int size = cantusFirmi.size();
        switch(size) {
            case 0: System.out.println("Could not generate any cantus firmi with the given parameters :("); break;
            case 1: System.out.println(size + " cantus firmus generated!"); break;
            default: System.out.println(size + " cantus firmi generated!");
        }
    }

    void printLinkedList(LinkedList<Node> a) {
        int size = (a.size() - 1);
        for(int i = 0; (i < size); i++) {
            System.out.print(a.get(i).pitch + ", ");
        }
        System.out.print(a.get(size).pitch + "\n");
    }

}
