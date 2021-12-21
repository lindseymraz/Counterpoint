package com.counterpoint;

import java.util.LinkedList;

public class SecondSpeciesNode extends Node<SecondSpeciesNode> {
    SecondSpeciesNode(int pitch) {
        super(pitch);
    }

    void giveRoute(SecondSpeciesNode to, LinkedList<SecondSpeciesNode> currPath, LinkedList<LinkedList<SecondSpeciesNode>> list) {

    }

    boolean passesTests(SecondSpeciesNode n, LinkedList<SecondSpeciesNode> currPath) {
        return false;
    }
}
