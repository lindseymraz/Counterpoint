import java.util.LinkedList;

public class Node {
    int pitch;
    LinkedList<Node> getsTo = new LinkedList<Node>();

    Node(int pitch) {
        this.pitch = pitch;
    }

    public void addEdge (Node toNode) {
        this.getsTo.add(toNode);
    }

}
