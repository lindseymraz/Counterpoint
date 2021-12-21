package com.counterpoint;

import java.util.LinkedList;

public class ThirdSpeciesNode extends Node<ThirdSpeciesNode> {
    ThirdSpeciesNode(int pitch) {
        super(pitch);
    }

    void giveRoute(ThirdSpeciesNode to, LinkedList<ThirdSpeciesNode> currPath, LinkedList<LinkedList<ThirdSpeciesNode>> list) {

    }

    boolean passesTests(ThirdSpeciesNode n, LinkedList<ThirdSpeciesNode> currPath) {
        return false;
    }
}
