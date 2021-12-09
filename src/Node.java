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

    void giveRouteH(Node to, LinkedList<Node> currPath, LinkedList<LinkedList<Node>> list) {
        if (this.equals(to)) {
            currPath.add(this);
            list.add(new LinkedList<Node>(currPath));
            currPath.remove(this);
        } else if (this.getsTo.size() > 0 ) {
            currPath.add(this);
            for (Node n : this.getsTo) {
                n.giveRouteH(to, currPath, list);
            }
            currPath.remove(this);
        }
    }

    /*
    after you find a need to backtrack, go back until the first thing with
    unseen getsTos. only now does the node you backtracked from to get to
    the thing with unfinished getsTos should go on a separate "seen" list.
    keep it there until you've got a whole column separating you and the
    node you wanna take off the "seen" list (you're in column 1, there's
    column 2, thing you wanna remove is in 3, that's the earliest you can
    get rid of it)
    once you end up with the start node having all its getsTos on the seen
    list, then you found everything and you're done.
     */

    void printLots(Node to) {
        LinkedList<LinkedList<Node>> cantusFirmi = new LinkedList<LinkedList<Node>>();
        this.giveRouteH(to, new LinkedList<Node>(), cantusFirmi);
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
