package com.counterpoint;

import java.util.LinkedList;

public class FirstSpeciesNode extends Node<FirstSpeciesNode> {

    //column setup can probably mimic cantus firmus column setup, keep the same logic but stick something
    //that changes start and end combos at the beginning and end?

    FirstSpeciesNode(int pitch) {
        super(pitch);
    }

    void giveRoute(FirstSpeciesNode to, LinkedList<FirstSpeciesNode> currPath, LinkedList<LinkedList<FirstSpeciesNode>> list) {
    }

    boolean passesTests(FirstSpeciesNode n, LinkedList<FirstSpeciesNode> currPath) {
        return false;
    }


}
