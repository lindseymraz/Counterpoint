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

    boolean leapHelper(Node curr, Node n, LinkedList<Node> currPath) {
        if(curr.startsLeapTo(n)) {
            LinkedList<Integer> leapList = countLeaps(currPath);
            if(leapList.get(0) < 4) {
                if (curr.startsBigLeapTo(n)) {
                    if (leapList.get(1) < 2) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    void giveRoute(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list, int earlyBound, int lateBound, int climax) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (giveRouteHelper(this, currPath, climax, earlyBound, lateBound)){
            currPath.add(this);
            for (Node n : this.getsTo) {
                if (leapHelper(this, n, currPath)) {
                    n.giveRoute(to, currPath, list, earlyBound, lateBound, climax);
                }
            }
            currPath.remove(this);
        }
    }

    boolean startsBigLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    boolean startsLeapUpTo(Node next) {
        return((next.pitch - this.pitch) >= 3);
    }

    boolean startsLeapDownTo(Node next) {
        return((this.pitch - next.pitch) >= 3);
    }

    boolean startsLeapTo(Node next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    void printLots(Node to, int earlyBound, int laterBound, int climax) {
        LinkedList<LinkedList<Node>> cantusFirmi = new LinkedList<LinkedList<Node>>();
        this.giveRoute(to, new LinkedList<Node>(), cantusFirmi, earlyBound, laterBound, climax);
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
        for(Node n : a) {
            System.out.print(n.pitch + ", ");
        }
        System.out.print("done\n");
    }

}
