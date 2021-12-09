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

    public boolean hasRoute(Node to, LinkedList<Node> visited) {
        if (this.equals(to)) {
            return true;
        } else if (visited.contains(this)) {
            return false;
        } else {
            visited.add(this);
            for (Node n : this.getsTo) {
                if (n.hasRoute(to, visited)) {
                    return true;
                }
            }
            return false;
        }
    }

    LinkedList<Node> giveRoute(Node to, LinkedList<Node> visited) {
        if (visited.contains(this)) {
            return new LinkedList<Node>(); //empty
        } else if (this.equals(to)) {
            LinkedList<Node> newRoute = new LinkedList<Node>();
            newRoute.addFirst(this);
            return newRoute;
        } else {
            visited.add(this);
            for(Node n : this.getsTo) {
                LinkedList<Node> nRoute = n.giveRoute(to,visited);
                if(nRoute.size() > 0) {
                    nRoute.addFirst(this);
                    return nRoute;
                }
            }
            return new LinkedList<Node>(); //empty
        }
    }

    boolean hasClimax(LinkedList<Node> list, int climax) {
        for(Node n : list) {
            if(n.pitch == climax) {
                return true;
            }
        } return false;
    }

    boolean giveRouteHHelper(Node n, LinkedList<Node> currPath, int climax, int earlyBound, int lateBound) {
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
    void giveRouteH(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list, int leaps, int bigLeaps, int earlyBound, int lateBound, int climax) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (giveRouteHHelper(this, currPath, climax, earlyBound, lateBound)){
            currPath.add(this);
            for (Node n : this.getsTo) {
                n.giveRouteH(to, currPath, list, leaps, bigLeaps, earlyBound, lateBound, climax);
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
        this.giveRouteH(to, new LinkedList<Node>(), cantusFirmi, 0, 0, earlyBound, laterBound, climax);
        for(LinkedList<Node> cantus : cantusFirmi) {
            printLinkedList(cantus);
        }
    }

    void printLinkedList(LinkedList<Node> a) {
        for(Node n : a) {
            System.out.print(n.pitch + ", ");
        }
        System.out.print("done\n");
    }

}
