package com.counterpoint;

import java.util.LinkedList;

abstract public class Node<TDATA extends Node<TDATA>> {
    int pitch;
    LinkedList<TDATA> getsTo = new LinkedList<TDATA>();

    static int penultPos;

    Node(int pitch) {
        this.pitch = pitch;
    }

    void addEdge (TDATA toNode) {
        this.getsTo.add(toNode);
    }

    protected boolean startsUpwardMotionTo(TDATA next) {
        return((next.pitch - this.pitch) > 0);
    }

    protected boolean startsDownwardMotionTo(TDATA next) {
        return((this.pitch - next.pitch) > 0);
    }

    protected boolean sameDirMotionBetween(TDATA one, TDATA two, TDATA three) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) ||
                (one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)));
    }

    protected boolean sameDirMotionBetween(TDATA one, TDATA two, TDATA three, TDATA four) {
        return((one.startsUpwardMotionTo(two) && two.startsUpwardMotionTo(three)) && three.startsUpwardMotionTo(four)) ||
                ((one.startsDownwardMotionTo(two) && two.startsDownwardMotionTo(three)) && three.startsDownwardMotionTo(four));
    }

    protected static boolean intervalIsDissonantMelodic(int pitch1, int pitch2) {
        switch(pitch1 - pitch2) {
            case -16, -15, -14, -13, -11, -10, -6, 6, 10, 11, 13, 14, 15, 16: return true;
            default: return false;
        }
    }

    protected boolean startsLeapTo(TDATA next) {
        int a = (this.pitch - next.pitch);
        return ((a > 2) || (a < -2));
    }

    protected boolean startsLeapLargerThanMajThird(TDATA next) {
        int a = (this.pitch - next.pitch);
        return ((a >= 5) || (a <= -5));
    }

    protected boolean startsLeapLargerThanFourth(TDATA next) {
        int a = (this.pitch - next.pitch);
        return ((a > 5) || (a < -5));
    }

    protected boolean twoSequentialLeapsBetween(TDATA one, TDATA two, TDATA three) {
        return(one.startsLeapTo(two) && two.startsLeapTo(three));
    }

    protected boolean threeSequentialLeapsBetween(TDATA one, TDATA two, TDATA three, TDATA four) {
        return((one.startsLeapTo(two) && two.startsLeapTo(three)) && three.startsLeapTo(four));
    }

    abstract void giveRoute(TDATA to, LinkedList<TDATA> currPath, LinkedList<LinkedList<TDATA>> list);

    abstract boolean passesTests(TDATA n, LinkedList<TDATA> currPath);

}
