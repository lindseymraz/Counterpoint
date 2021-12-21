package com.counterpoint;

import java.util.LinkedList;

public class FourthSpeciesNode extends Node<FourthSpeciesNode> {
    FourthSpeciesNode(int pitch) {
        super(pitch);
    }

    void giveRoute(FourthSpeciesNode to, LinkedList<FourthSpeciesNode> currPath, LinkedList<LinkedList<FourthSpeciesNode>> list) {

    }

    boolean passesTests(FourthSpeciesNode n, LinkedList<FourthSpeciesNode> currPath) {
        return false;
    }
}
