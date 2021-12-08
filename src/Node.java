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

    void printLinkedList(LinkedList<Node> a) {
        for(Node n : a) {
            System.out.println(n.pitch);
        }
    }

}
