package com.counterpoint;

import org.junit.Test;

import static org.junit.Assert.*;

public class Tests {
    public Tests() {}

    CantusFirmusNode C4 = new CantusFirmusNode(60);
    CantusFirmusNode D4 = new CantusFirmusNode(62);
    CantusFirmusNode E4 = new CantusFirmusNode(64);
    CantusFirmusNode F4 = new CantusFirmusNode(65);
    CantusFirmusNode G4 = new CantusFirmusNode(67);
    CantusFirmusNode A4 = new CantusFirmusNode(69);
    CantusFirmusNode B4 = new CantusFirmusNode(71);
    CantusFirmusNode C5 = new CantusFirmusNode(72);
    CantusFirmusNode D5 = new CantusFirmusNode(74);
    CantusFirmusNode E5 = new CantusFirmusNode(76);
    CantusFirmusNode F5 = new CantusFirmusNode(77);
    CantusFirmusNode Fs5 = new CantusFirmusNode(78);
    CantusFirmusNode G5 = new CantusFirmusNode(79);

    FirstSpeciesNode C4f = new FirstSpeciesNode(60);
    FirstSpeciesNode D4f = new FirstSpeciesNode(62);
    FirstSpeciesNode E4f = new FirstSpeciesNode(64);
    FirstSpeciesNode F4f = new FirstSpeciesNode(65);
    FirstSpeciesNode G4f = new FirstSpeciesNode(67);
    FirstSpeciesNode A4f = new FirstSpeciesNode(69);
    FirstSpeciesNode B4f = new FirstSpeciesNode(71);
    FirstSpeciesNode C5f = new FirstSpeciesNode(72);
    FirstSpeciesNode D5f = new FirstSpeciesNode(74);
    FirstSpeciesNode E5f = new FirstSpeciesNode(76);
    FirstSpeciesNode F5f = new FirstSpeciesNode(77);
    FirstSpeciesNode Fs5f = new FirstSpeciesNode(78);
    FirstSpeciesNode G5f = new FirstSpeciesNode(79);

    @Test
    public void testStartsLeapTo() {
        //upwards
        assertFalse(C4.startsLeapTo(C4)); //unison
        assertFalse(E4.startsLeapTo(F4)); //minor second
        assertFalse(C4.startsLeapTo(D4)); //major second
        assertTrue(D4.startsLeapTo(F4)); //minor third
        assertTrue(C4.startsLeapTo(E4)); //major third
        assertTrue(C4.startsLeapTo(F5)); //large
        //downwards
        assertFalse(C4.startsLeapTo(C4)); //unison
        assertFalse(F4.startsLeapTo(E4)); //minor second
        assertFalse(D4.startsLeapTo(C4)); //major second
        assertTrue(F4.startsLeapTo(D4)); //minor third
        assertTrue(E4.startsLeapTo(C4)); //major third
        assertTrue(F5.startsLeapTo(C4)); //large
    }

    @Test
    public void teststartsLeapLargerThanMajThird() {
        //upwards
        assertFalse(C4.startsLeapLargerThanMajThird(C4)); //unison
        assertFalse(E4.startsLeapLargerThanMajThird(F4)); //minor second
        assertFalse(C4.startsLeapLargerThanMajThird(D4)); //major second
        assertFalse(D4.startsLeapLargerThanMajThird(F4)); //minor third
        assertFalse(C4.startsLeapLargerThanMajThird(E4)); //major third
        assertTrue(C4.startsLeapLargerThanMajThird(F4)); //perfect fourth
        assertTrue(C4.startsLeapLargerThanMajThird(G4)); //perfect fifth
        assertTrue(C4.startsLeapLargerThanMajThird(F5)); //large
        //downwards
        assertFalse(C4.startsLeapLargerThanMajThird(C4)); //unison
        assertFalse(F4.startsLeapLargerThanMajThird(E4)); //minor second
        assertFalse(D4.startsLeapLargerThanMajThird(C4)); //major second
        assertFalse(F4.startsLeapLargerThanMajThird(D4)); //minor third
        assertFalse(E4.startsLeapLargerThanMajThird(C4)); //major third
        assertTrue(F4.startsLeapLargerThanMajThird(C4)); //perfect fourth
        assertTrue(G4.startsLeapLargerThanMajThird(C4)); //perfect fifth
        assertTrue(F5.startsLeapLargerThanMajThird(C4)); //large
    }

    @Test
    public void testStartsLeapLargerThanFourth() {
        //upwards
        assertFalse(C4.startsLeapLargerThanFourth(C4)); //unison
        assertFalse(E4.startsLeapLargerThanFourth(F4)); //minor second
        assertFalse(C4.startsLeapLargerThanFourth(D4)); //major second
        assertFalse(D4.startsLeapLargerThanFourth(F4)); //minor third
        assertFalse(C4.startsLeapLargerThanFourth(E4)); //major third
        assertFalse(C4.startsLeapLargerThanFourth(F4)); //perfect fourth
        assertTrue(C4.startsLeapLargerThanFourth(G4)); //perfect fifth
        assertTrue(C4.startsLeapLargerThanFourth(F5)); //large
        //downwards
        assertFalse(C4.startsLeapLargerThanFourth(C4)); //unison
        assertFalse(F4.startsLeapLargerThanFourth(E4)); //minor second
        assertFalse(D4.startsLeapLargerThanFourth(C4)); //major second
        assertFalse(F4.startsLeapLargerThanFourth(D4)); //minor third
        assertFalse(E4.startsLeapLargerThanFourth(C4)); //major third
        assertFalse(F4.startsLeapLargerThanFourth(C4)); //perfect fourth
        assertTrue(G4.startsLeapLargerThanFourth(C4)); //perfect fifth
        assertTrue(F5.startsLeapLargerThanFourth(C4)); //large
    }

    @Test
    public void testStartsUpwardMotionTo() {
        //upwards
        assertFalse(C4.startsUpwardMotionTo(C4)); //unison
        assertTrue(E4.startsUpwardMotionTo(F4)); //minor second
        assertTrue(C4.startsUpwardMotionTo(D4)); //major second
        assertTrue(C4.startsUpwardMotionTo(F5)); //large
        //downwards
        assertFalse(C4.startsUpwardMotionTo(C4)); //unison
        assertFalse(F4.startsUpwardMotionTo(E4)); //minor second
        assertFalse(D4.startsUpwardMotionTo(C4)); //major second
        assertFalse(F5.startsUpwardMotionTo(C4)); //large
    }

    @Test
    public void testStartsDownwardMotionTo() {
        //upwards
        assertFalse(C4.startsDownwardMotionTo(C4)); //unison
        assertFalse(E4.startsDownwardMotionTo(F4)); //minor second
        assertFalse(C4.startsDownwardMotionTo(D4)); //major second
        assertFalse(C4.startsDownwardMotionTo(F5)); //large
        //downwards
        assertFalse(C4.startsDownwardMotionTo(C4)); //unison
        assertTrue(F4.startsDownwardMotionTo(E4)); //minor second
        assertTrue(D4.startsDownwardMotionTo(C4)); //major second
        assertTrue(F5.startsDownwardMotionTo(C4)); //large
    }

    @Test
    public void testintervalIsDissonantMelodic() {
        //upwards
        assertFalse(Node.intervalIsDissonantMelodic(60, 60));
        assertFalse(Node.intervalIsDissonantMelodic(60, 61));
        assertFalse(Node.intervalIsDissonantMelodic(60, 62));
        assertFalse(Node.intervalIsDissonantMelodic(60, 63));
        assertFalse(Node.intervalIsDissonantMelodic(60, 64));
        assertFalse(Node.intervalIsDissonantMelodic(60, 65));
        assertTrue(Node.intervalIsDissonantMelodic(60, 66));
        assertFalse(Node.intervalIsDissonantMelodic(60, 67));
        assertFalse(Node.intervalIsDissonantMelodic(60, 68));
        assertFalse(Node.intervalIsDissonantMelodic(60, 69));
        assertTrue(Node.intervalIsDissonantMelodic(60, 70));
        assertTrue(Node.intervalIsDissonantMelodic(60, 71));
        assertFalse(Node.intervalIsDissonantMelodic(60, 72));
        assertTrue(Node.intervalIsDissonantMelodic(60, 73));
        assertTrue(Node.intervalIsDissonantMelodic(60, 74));
        assertTrue(Node.intervalIsDissonantMelodic(60, 75));
        assertTrue(Node.intervalIsDissonantMelodic(60, 76));
        //downwards
        assertFalse(Node.intervalIsDissonantMelodic(76, 76));
        assertFalse(Node.intervalIsDissonantMelodic(76, 75));
        assertFalse(Node.intervalIsDissonantMelodic(76, 74));
        assertFalse(Node.intervalIsDissonantMelodic(76, 73));
        assertFalse(Node.intervalIsDissonantMelodic(76, 72));
        assertFalse(Node.intervalIsDissonantMelodic(76, 71));
        assertTrue(Node.intervalIsDissonantMelodic(76, 70));
        assertFalse(Node.intervalIsDissonantMelodic(76, 69));
        assertFalse(Node.intervalIsDissonantMelodic(76, 68));
        assertFalse(Node.intervalIsDissonantMelodic(76, 67));
        assertTrue(Node.intervalIsDissonantMelodic(76, 66));
        assertTrue(Node.intervalIsDissonantMelodic(76, 65));
        assertFalse(Node.intervalIsDissonantMelodic(76, 64));
        assertTrue(Node.intervalIsDissonantMelodic(76, 63));
        assertTrue(Node.intervalIsDissonantMelodic(76, 62));
        assertTrue(Node.intervalIsDissonantMelodic(76, 61));
        assertTrue(Node.intervalIsDissonantMelodic(76, 60));
    }

    @Test
    public void testSameDirMotionBetweenThree() {
        //pure upwards
        assertFalse(C4.sameDirMotionBetween(C4, C4, C4)); //unison unison
        assertFalse(C4.sameDirMotionBetween(C4, C4, D4)); //unison step
        assertFalse(C4.sameDirMotionBetween(C4, D4, D4)); //step unison
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4)); //unison leap
        assertFalse(C4.sameDirMotionBetween(C4, G4, G4)); //leap unison
        assertTrue(C4.sameDirMotionBetween(C4, D4, E4)); //step step
        assertTrue(C4.sameDirMotionBetween(C4, D4, F5)); //step leap
        assertTrue(C4.sameDirMotionBetween(C4, C5, D5)); //leap step
        assertTrue(C4.sameDirMotionBetween(C4, G4, C5)); //leap leap
        //up down
        assertFalse(C4.sameDirMotionBetween(C4, D4, C4)); //step step
        assertFalse(C4.sameDirMotionBetween(G4, A4, C4)); //step leap
        assertFalse(C4.sameDirMotionBetween(C4, G4, F4)); //leap step
        assertFalse(C4.sameDirMotionBetween(C4, A4, D4)); //leap leap
        //down up
        assertFalse(C4.sameDirMotionBetween(C5, B4, C5)); //step step
        assertFalse(C4.sameDirMotionBetween(C5, B4, E5)); //step leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, A4)); //leap step
        assertFalse(C4.sameDirMotionBetween(C5, G4, E5)); //leap leap
        //pure downwards
        assertFalse(C4.sameDirMotionBetween(C5, C5, C5)); //unison unison
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4)); //unison step
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4)); //step unison
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4)); //unison leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4)); //leap unison
        assertTrue(C4.sameDirMotionBetween(C5, B4, A4)); //step step
        assertTrue(C4.sameDirMotionBetween(C5, B4, F4)); //step leap
        assertTrue(C4.sameDirMotionBetween(C5, G4, F4)); //leap step
        assertTrue(C4.sameDirMotionBetween(C5, G4, C4)); //leap leap
    }

    @Test
    public void testSameDirMotionBetweenFour() {
        //pure upwards
        assertFalse(C4.sameDirMotionBetween(C4, C4, C4, C4)); //unison unison unison
        assertFalse(C4.sameDirMotionBetween(C4, C4, C4, D4)); //unison unison step
        assertFalse(C4.sameDirMotionBetween(C4, C4, C4, G4)); //unison unison leap
        assertFalse(C4.sameDirMotionBetween(C4, C4, D4, D4)); //unison step unison
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4, G4)); //unison leap unison
        assertFalse(C4.sameDirMotionBetween(C4, D4, D4, D4)); //step unison unison
        assertFalse(C4.sameDirMotionBetween(C4, G4, G4, G4)); //leap unison unison
        assertFalse(C4.sameDirMotionBetween(C4, C4, D4, E4)); //unison step step
        assertFalse(C4.sameDirMotionBetween(C4, C4, D4, G4)); //unison step leap
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4, A4)); //unison leap step
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4, C5)); //unison leap leap
        assertFalse(C4.sameDirMotionBetween(C4, D4, D4, E4)); //step unison step
        assertFalse(C4.sameDirMotionBetween(C4, D4, D4, C5)); //step unison leap
        assertFalse(C4.sameDirMotionBetween(C4, C5, C5, D5)); //leap unison step
        assertFalse(C4.sameDirMotionBetween(C4, C5, C5, E5)); //leap unison leap
        assertFalse(C4.sameDirMotionBetween(C4, D4, E4, E4)); //step step unison
        assertFalse(C4.sameDirMotionBetween(C4, D4, C5, C5)); //step leap unison
        assertFalse(C4.sameDirMotionBetween(C4, G4, A4, A4)); //leap step unison
        assertFalse(C4.sameDirMotionBetween(C4, G4, C5, C5)); //leap leap unison
        assertTrue(C4.sameDirMotionBetween(C4, D4, E4, F4)); //step step step
        assertTrue(C4.sameDirMotionBetween(C4, D4, E4, C5)); //step step leap
        assertTrue(C4.sameDirMotionBetween(C4, D4, C5, D5)); //step leap step
        assertTrue(C4.sameDirMotionBetween(C4, C5, D5, E5)); //leap step step
        assertTrue(C4.sameDirMotionBetween(C4, D4, A4, E5)); //step leap leap
        assertTrue(C4.sameDirMotionBetween(C4, A4, B4, E5)); //leap step leap
        assertTrue(C4.sameDirMotionBetween(C4, G4, C5, D5)); //leap leap step
        assertTrue(C4.sameDirMotionBetween(C4, E4, G4, D5)); //leap leap leap
        //up down
        assertFalse(C4.sameDirMotionBetween(C4, C4, D4, C4)); //unison step step
        assertFalse(C4.sameDirMotionBetween(G4, G4, A4, E4)); //unison step leap
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4, F4)); //unison leap step
        assertFalse(C4.sameDirMotionBetween(C4, C4, G4, E4)); //unison leap leap
        assertFalse(C4.sameDirMotionBetween(C4, D4, D4, C4)); //step unison step
        assertFalse(C4.sameDirMotionBetween(C5, D5, D5, E4)); //step unison leap
        assertFalse(C4.sameDirMotionBetween(C5, E5, E5, D5)); //leap unison step
        assertFalse(C4.sameDirMotionBetween(G4, C5, C5, C4)); //leap unison leap
        assertFalse(C4.sameDirMotionBetween(C5, D5, C5, C5)); //step step unison
        assertFalse(C4.sameDirMotionBetween(C5, D5, E4, E4)); //step leap unison
        assertFalse(C4.sameDirMotionBetween(C4, G4, F4, F4)); //leap step unison
        assertFalse(C4.sameDirMotionBetween(C4, B4, E4, E4)); //leap leap unison
        //up up down
        assertFalse(C4.sameDirMotionBetween(C4, D4, E4, D4)); //step step step
        assertFalse(C4.sameDirMotionBetween(C4, D4, E4, C4)); //step step leap
        assertFalse(C4.sameDirMotionBetween(C4, D4, C5, B4)); //step leap step
        assertFalse(C4.sameDirMotionBetween(C4, C5, D5, C5)); //leap step step
        assertFalse(C4.sameDirMotionBetween(C4, D4, A4, C4)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(C4, A4, B4, C4)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C4, G4, C5, B4)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(C4, E4, G4, C4)); //leap leap leap
        //up down up
        assertFalse(C4.sameDirMotionBetween(C4, D4, C4, D4)); //step step step
        assertFalse(C4.sameDirMotionBetween(C4, D4, C4, C5)); //step step leap
        assertFalse(C4.sameDirMotionBetween(E4, F4, C4, D4)); //step leap step
        assertFalse(C4.sameDirMotionBetween(C4, C5, B4, C5)); //leap step step
        assertFalse(C4.sameDirMotionBetween(E4, F4, C4, C5)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(C4, C5, B4, E5)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C4, G4, C4, D4)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(C4, E4, C4, D5)); //leap leap leap
        //up down down
        assertFalse(C4.sameDirMotionBetween(E4, F4, E4, D4)); //step step step
        assertFalse(C4.sameDirMotionBetween(E4, F4, E4, C4)); //step step leap
        assertFalse(C4.sameDirMotionBetween(C5, D5, G4, F4)); //step leap step
        assertFalse(C4.sameDirMotionBetween(C4, C5, B4, A4)); //leap step step
        assertFalse(C4.sameDirMotionBetween(C5, D5, G4, C4)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(C4, C5, B4, C4)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C5, E5, C5, B4)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(C5, E5, G4, C4)); //leap leap leap
        //down up up
        assertFalse(C4.sameDirMotionBetween(D4, C4, D4, E4)); //step step step
        assertFalse(C4.sameDirMotionBetween(D4, C4, D4, C5)); //step step leap
        assertFalse(C4.sameDirMotionBetween(D4, C4, C5, D5)); //step leap step
        assertFalse(C4.sameDirMotionBetween(C5, C4, D4, E4)); //leap step step
        assertFalse(C4.sameDirMotionBetween(D4, C4, A4, E5)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(C5, A4, B4, E5)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, C5, D5)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(C5, E4, G4, D5)); //leap leap leap
        //down up down
        assertFalse(C4.sameDirMotionBetween(E4, D4, E4, D4)); //step step step
        assertFalse(C4.sameDirMotionBetween(E4, D4, E4, C4)); //step step leap
        assertFalse(C4.sameDirMotionBetween(E4, D4, C5, B4)); //step leap step
        assertFalse(C4.sameDirMotionBetween(C5, E4, F4, E4)); //leap step step
        assertFalse(C4.sameDirMotionBetween(E4, D4, A4, C4)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(C5, A4, B4, C4)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, C5, B4)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(C5, E4, G4, C4)); //leap leap leap
        //down down up
        assertFalse(C4.sameDirMotionBetween(E4, D4, C4, D4)); //step step step
        assertFalse(C4.sameDirMotionBetween(E4, D4, C4, C5)); //step step leap
        assertFalse(C4.sameDirMotionBetween(A4, G4, C4, D4)); //step leap step
        assertFalse(C4.sameDirMotionBetween(A4, D4, C4, D4)); //leap step step
        assertFalse(C4.sameDirMotionBetween(A4, G4, C4, E5)); //step leap leap
        assertFalse(C4.sameDirMotionBetween(A4, F4, E4, E5)); //leap step leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, C4, D4)); //leap leap step
        assertFalse(C4.sameDirMotionBetween(E5, C5, G4, D5)); //leap leap leap
        //down up
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4, C5)); //unison step step
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4, E5)); //unison step leap
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4, A4)); //unison leap step
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4, C5)); //unison leap leap
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4, C5)); //step unison step
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4, E5)); //step unison leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4, A4)); //leap unison step
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4, C5)); //leap unison leap
        assertFalse(C4.sameDirMotionBetween(C5, B4, C5, C5)); //step step unison
        assertFalse(C4.sameDirMotionBetween(C5, B4, E5, E5)); //step leap unison
        assertFalse(C4.sameDirMotionBetween(C5, G4, A4, A4)); //leap step unison
        assertFalse(C4.sameDirMotionBetween(C5, G4, E5, E5)); //leap leap unison
        //pure downwards
        assertFalse(C4.sameDirMotionBetween(C4, C4, C4, C4)); //unison unison unison
        assertFalse(C4.sameDirMotionBetween(C5, C5, C5, B4)); //unison unison step
        assertFalse(C4.sameDirMotionBetween(C5, C5, C5, G4)); //unison unison leap
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4, B4)); //unison step unison
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4, G4)); //unison leap unison
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4, B4)); //step unison unison
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4, G4)); //leap unison unison
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4, A4)); //unison step step
        assertFalse(C4.sameDirMotionBetween(C5, C5, B4, G4)); //unison step leap
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4, F4)); //unison leap step
        assertFalse(C4.sameDirMotionBetween(C5, C5, G4, C4)); //unison leap leap
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4, A4)); //step unison step
        assertFalse(C4.sameDirMotionBetween(C5, B4, B4, C4)); //step unison leap
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4, F4)); //leap unison step
        assertFalse(C4.sameDirMotionBetween(C5, G4, G4, C4)); //leap unison leap
        assertFalse(C4.sameDirMotionBetween(C5, B4, A4, A4)); //step step unison
        assertFalse(C4.sameDirMotionBetween(C5, B4, C4, C4)); //step leap unison
        assertFalse(C4.sameDirMotionBetween(C5, G4, F4, F4)); //leap step unison
        assertFalse(C4.sameDirMotionBetween(C5, G4, C4, C4)); //leap leap unison
        assertTrue(C4.sameDirMotionBetween(C5, B4, A4, G4)); //step step step
        assertTrue(C4.sameDirMotionBetween(C5, B4, A4, C4)); //step step leap
        assertTrue(C4.sameDirMotionBetween(C5, B4, D4, C4)); //step leap step
        assertTrue(C4.sameDirMotionBetween(C5, A4, G4, F4)); //leap step step
        assertTrue(C4.sameDirMotionBetween(C5, B4, E4, C4)); //step leap leap
        assertTrue(C4.sameDirMotionBetween(C5, A4, G4, C4)); //leap step leap
        assertTrue(C4.sameDirMotionBetween(C5, G4, E4, D4)); //leap leap step
        assertTrue(C4.sameDirMotionBetween(E5, C5, G4, C4)); //leap leap leap
    }

    @Test
    public void testSequentialLeapsBetween() {
        //not sequential leaps
        //pure upwards
        assertFalse(C4.twoSequentialLeapsBetween(C4, C4, C4)); //unison unison
        assertFalse(C4.twoSequentialLeapsBetween(C4, C4, D4)); //unison step
        assertFalse(C4.twoSequentialLeapsBetween(C4, D4, D4)); //step unison
        assertFalse(C4.twoSequentialLeapsBetween(C4, C4, E4)); //unison third
        assertFalse(C4.twoSequentialLeapsBetween(C4, C4, C5)); //unison big
        assertFalse(C4.twoSequentialLeapsBetween(C4, E4, E4)); //third unison
        assertFalse(C4.twoSequentialLeapsBetween(C4, G4, G4)); //big unison
        assertFalse(C4.twoSequentialLeapsBetween(C4, D4, E4)); //step step
        assertFalse(C4.twoSequentialLeapsBetween(C4, D4, F4)); //step third
        assertFalse(C4.twoSequentialLeapsBetween(C4, D4, F5)); //step big
        assertFalse(C4.twoSequentialLeapsBetween(C4, E4, F4)); //third step
        assertFalse(C4.twoSequentialLeapsBetween(C4, C5, D5)); //big step
        //up down
        assertFalse(C4.twoSequentialLeapsBetween(C4, D4, C4)); //step step
        assertFalse(C4.twoSequentialLeapsBetween(G4, A4, F4)); //step third
        assertFalse(C4.twoSequentialLeapsBetween(G4, A4, C4)); //step big
        assertFalse(C4.twoSequentialLeapsBetween(C4, E4, D4)); //third step
        assertFalse(C4.twoSequentialLeapsBetween(C4, G4, F4)); //big step
        // down up
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, C5)); //step step
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, D5)); //step third
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, E5)); //step big
        assertFalse(C4.twoSequentialLeapsBetween(C5, A4, B4)); //third step
        assertFalse(C4.twoSequentialLeapsBetween(C5, F4, G4)); //big step
        //pure downwards
        assertFalse(C4.twoSequentialLeapsBetween(C5, C5, C5)); //unison unison
        assertFalse(C4.twoSequentialLeapsBetween(C5, C5, B4)); //unison step
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, B4)); //step unison
        assertFalse(C4.twoSequentialLeapsBetween(C5, C5, A4)); //unison third
        assertFalse(C4.twoSequentialLeapsBetween(C5, C5, C4)); //unison big
        assertFalse(C4.twoSequentialLeapsBetween(C5, A4, A4)); //third unison
        assertFalse(C4.twoSequentialLeapsBetween(C5, C4, C4)); //big unison
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, A4)); //step step
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, G4)); //step third
        assertFalse(C4.twoSequentialLeapsBetween(C5, B4, C4)); //step big
        assertFalse(C4.twoSequentialLeapsBetween(C5, A4, G4)); //third step
        assertFalse(C4.twoSequentialLeapsBetween(C5, E4, D4)); //big step

        //sequential leaps
        //pure upwards
        assertTrue(C4.twoSequentialLeapsBetween(C4, E4, G4)); //third third
        assertTrue(C4.twoSequentialLeapsBetween(C4, E4, C5)); //third big
        assertTrue(C4.twoSequentialLeapsBetween(C4, C5, E5)); //big third
        assertTrue(C4.twoSequentialLeapsBetween(C4, G4, C5)); //big big
        //up down
        assertTrue(C4.twoSequentialLeapsBetween(C4, E4, C4)); //third third
        assertTrue(C4.twoSequentialLeapsBetween(F4, A4, C4)); //third big
        assertTrue(C4.twoSequentialLeapsBetween(C4, C5, A4)); //big third
        assertTrue(C4.twoSequentialLeapsBetween(C4, E5, C4)); //big big
        //down up
        assertTrue(C4.twoSequentialLeapsBetween(E4, C4, E4)); //third third
        assertTrue(C4.twoSequentialLeapsBetween(G4, E4, C5)); //third big
        assertTrue(C4.twoSequentialLeapsBetween(C5, C4, E4)); //big third
        assertTrue(C4.twoSequentialLeapsBetween(E5, C4, C5)); //big big
        //pure downwards
        assertTrue(C4.twoSequentialLeapsBetween(G4, E4, C4)); //third third
        assertTrue(C4.twoSequentialLeapsBetween(G4, E4, C5)); //third big
        assertTrue(C4.twoSequentialLeapsBetween(E5, A4, F4)); //big third
        assertTrue(C4.twoSequentialLeapsBetween(E5, A4, C4)); //big big
    }

    /* @Test
    public void testDiatonicity() { //the reason for the 3 different keys: C says yes to all white key pitch
        Graph C = new Graph(Mode.IONIAN, 0); //classes, B says no to all white keys BUT B and E,
        Graph D = new Graph(Mode.DORIAN, 2); //Db says no to B and E (though it says yes to F and C)
        Graph E = new Graph(Mode.PHRYGIAN, 4); //so between them all the white key pitch classes get said
        Graph F = new Graph(Mode.LYDIAN, 5); //yes to, AND no to; similar logic for black key pitch classes
        Graph G = new Graph(Mode.MIXOLYDIAN, 7); //C says no to all, B and Db say yes to all
        Graph A = new Graph(Mode.AEOLIAN, 9);
        Graph B = new Graph(Mode.LOCRIAN, 11);
        testDiatonicityHelperC(C);
        testDiatonicityHelperC(D);
        testDiatonicityHelperC(E);
        testDiatonicityHelperC(F);
        testDiatonicityHelperC(G);
        testDiatonicityHelperC(A);
        testDiatonicityHelperC(B);
        Graph Db = new Graph(Mode.IONIAN, 1);
        Graph Eb = new Graph(Mode.DORIAN, 3);
        Graph Gbb = new Graph(Mode.PHRYGIAN, 5);
        Graph Gb = new Graph(Mode.LYDIAN, 6);
        Graph Ab = new Graph(Mode.MIXOLYDIAN, 8);
        Graph Bb = new Graph(Mode.AEOLIAN, 10);
        Graph Dbb = new Graph(Mode.LOCRIAN, 0);
        testDiatonicityHelperDFlat(Db);
        testDiatonicityHelperDFlat(Eb);
        testDiatonicityHelperDFlat(Gbb);
        testDiatonicityHelperDFlat(Gb);
        testDiatonicityHelperDFlat(Ab);
        testDiatonicityHelperDFlat(Bb);
        testDiatonicityHelperDFlat(Dbb);
        Graph Ashsh = new Graph(Mode.IONIAN, 11);
        Graph Csh = new Graph(Mode.DORIAN, 1);
        Graph Dsh = new Graph(Mode.PHRYGIAN, 3);
        Graph Dshsh = new Graph(Mode.LYDIAN, 4);
        Graph Fsh = new Graph(Mode.MIXOLYDIAN, 6);
        Graph Gsh = new Graph(Mode.AEOLIAN, 8);
        Graph Ash = new Graph(Mode.LOCRIAN, 10);
        testDiatonicityHelperB(Ashsh);
        testDiatonicityHelperB(Csh);
        testDiatonicityHelperB(Dsh);
        testDiatonicityHelperB(Dshsh);
        testDiatonicityHelperB(Fsh);
        testDiatonicityHelperB(Gsh);
        testDiatonicityHelperB(Ash);
    }

    public void testDiatonicityHelperB(Graph Graph) {
        Graph.setDiatonicPitchClasses();
        assertFalse(Graph.isDiatonic(0));
        assertTrue(Graph.isDiatonic(1));
        assertFalse(Graph.isDiatonic(2));
        assertTrue(Graph.isDiatonic(3));
        assertTrue(Graph.isDiatonic(4));
        assertFalse(Graph.isDiatonic(5));
        assertTrue(Graph.isDiatonic(6));
        assertFalse(Graph.isDiatonic(7));
        assertTrue(Graph.isDiatonic(8));
        assertFalse(Graph.isDiatonic(9));
        assertTrue(Graph.isDiatonic(10));
        assertTrue(Graph.isDiatonic(11));
        assertFalse(Graph.isDiatonic(12));
        assertFalse(Graph.isDiatonic(60));
        assertTrue(Graph.isDiatonic(61));
        assertFalse(Graph.isDiatonic(62));
        assertTrue(Graph.isDiatonic(63));
        assertTrue(Graph.isDiatonic(64));
        assertFalse(Graph.isDiatonic(65));
        assertTrue(Graph.isDiatonic(66));
        assertFalse(Graph.isDiatonic(67));
        assertTrue(Graph.isDiatonic(68));
        assertFalse(Graph.isDiatonic(69));
        assertTrue(Graph.isDiatonic(70));
        assertTrue(Graph.isDiatonic(71));
        assertFalse(Graph.isDiatonic(72));
        assertTrue(Graph.isDiatonic(116));
        assertFalse(Graph.isDiatonic(117));
        assertTrue(Graph.isDiatonic(118));
        assertTrue(Graph.isDiatonic(119));
        assertFalse(Graph.isDiatonic(120));
        assertTrue(Graph.isDiatonic(121));
        assertFalse(Graph.isDiatonic(122));
        assertTrue(Graph.isDiatonic(123));
        assertTrue(Graph.isDiatonic(124));
        assertFalse(Graph.isDiatonic(125));
        assertTrue(Graph.isDiatonic(126));
        assertFalse(Graph.isDiatonic(127));
    }

    public void testDiatonicityHelperC(Graph Graph) {
        Graph.setDiatonicPitchClasses();
        assertTrue(Graph.isDiatonic(0));
        assertFalse(Graph.isDiatonic(1));
        assertTrue(Graph.isDiatonic(2));
        assertFalse(Graph.isDiatonic(3));
        assertTrue(Graph.isDiatonic(4));
        assertTrue(Graph.isDiatonic(5));
        assertFalse(Graph.isDiatonic(6));
        assertTrue(Graph.isDiatonic(7));
        assertFalse(Graph.isDiatonic(8));
        assertTrue(Graph.isDiatonic(9));
        assertFalse(Graph.isDiatonic(10));
        assertTrue(Graph.isDiatonic(11));
        assertTrue(Graph.isDiatonic(12));
        assertTrue(Graph.isDiatonic(60));
        assertFalse(Graph.isDiatonic(61));
        assertTrue(Graph.isDiatonic(62));
        assertFalse(Graph.isDiatonic(63));
        assertTrue(Graph.isDiatonic(64));
        assertTrue(Graph.isDiatonic(65));
        assertFalse(Graph.isDiatonic(66));
        assertTrue(Graph.isDiatonic(67));
        assertFalse(Graph.isDiatonic(68));
        assertTrue(Graph.isDiatonic(69));
        assertFalse(Graph.isDiatonic(70));
        assertTrue(Graph.isDiatonic(71));
        assertTrue(Graph.isDiatonic(72));
        assertFalse(Graph.isDiatonic(116));
        assertTrue(Graph.isDiatonic(117));
        assertFalse(Graph.isDiatonic(118));
        assertTrue(Graph.isDiatonic(119));
        assertTrue(Graph.isDiatonic(120));
        assertFalse(Graph.isDiatonic(121));
        assertTrue(Graph.isDiatonic(122));
        assertFalse(Graph.isDiatonic(123));
        assertTrue(Graph.isDiatonic(124));
        assertTrue(Graph.isDiatonic(125));
        assertFalse(Graph.isDiatonic(126));
        assertTrue(Graph.isDiatonic(127));
    }

    public void testDiatonicityHelperDFlat(Graph Graph) {
        Graph.setDiatonicPitchClasses();
        assertTrue(Graph.isDiatonic(0));
        assertTrue(Graph.isDiatonic(1));
        assertFalse(Graph.isDiatonic(2));
        assertTrue(Graph.isDiatonic(3));
        assertFalse(Graph.isDiatonic(4));
        assertTrue(Graph.isDiatonic(5));
        assertTrue(Graph.isDiatonic(6));
        assertFalse(Graph.isDiatonic(7));
        assertTrue(Graph.isDiatonic(8));
        assertFalse(Graph.isDiatonic(9));
        assertTrue(Graph.isDiatonic(10));
        assertFalse(Graph.isDiatonic(11));
        assertTrue(Graph.isDiatonic(12));
        assertTrue(Graph.isDiatonic(60));
        assertTrue(Graph.isDiatonic(61));
        assertFalse(Graph.isDiatonic(62));
        assertTrue(Graph.isDiatonic(63));
        assertFalse(Graph.isDiatonic(64));
        assertTrue(Graph.isDiatonic(65));
        assertTrue(Graph.isDiatonic(66));
        assertFalse(Graph.isDiatonic(67));
        assertTrue(Graph.isDiatonic(68));
        assertFalse(Graph.isDiatonic(69));
        assertTrue(Graph.isDiatonic(70));
        assertFalse(Graph.isDiatonic(71));
        assertTrue(Graph.isDiatonic(72));
        assertTrue(Graph.isDiatonic(116));
        assertFalse(Graph.isDiatonic(117));
        assertTrue(Graph.isDiatonic(118));
        assertFalse(Graph.isDiatonic(119));
        assertTrue(Graph.isDiatonic(120));
        assertTrue(Graph.isDiatonic(121));
        assertFalse(Graph.isDiatonic(122));
        assertTrue(Graph.isDiatonic(123));
        assertFalse(Graph.isDiatonic(124));
        assertTrue(Graph.isDiatonic(125));
        assertTrue(Graph.isDiatonic(126));
        assertFalse(Graph.isDiatonic(127));
    } */

    @Test
    public void testMakesConsonance() {
        assertTrue(C4f.makesConsonance(C4)); //unison

        //up
        assertFalse(E4f.makesConsonance(F4)); //m2
        assertFalse(C4f.makesConsonance(D4)); //M2
        assertTrue(E4f.makesConsonance(G4)); //m3
        assertTrue(C4f.makesConsonance(E4)); //M3
        assertFalse(C4f.makesConsonance(F4)); //P4
        assertFalse(F4f.makesConsonance(B4)); //TT
        assertTrue(C4f.makesConsonance(G4)); //P5
        assertTrue(E4f.makesConsonance(C5)); //m6
        assertTrue(C4f.makesConsonance(A4)); //M6
        assertFalse(D4f.makesConsonance(C5)); //m7
        assertFalse(C4f.makesConsonance(B4)); //M7
        assertTrue(C4f.makesConsonance(C5)); //P8
        assertFalse(E4f.makesConsonance(F5)); //m9
        assertFalse(C4f.makesConsonance(D5)); //M9
        assertTrue(D4f.makesConsonance(F5)); //m10
        assertTrue(C4f.makesConsonance(E5)); //M10
        assertFalse(C4f.makesConsonance(F5)); //P11
        assertFalse(C4f.makesConsonance(Fs5)); //TT
        assertTrue(C4f.makesConsonance(G5)); //P12

        //down
        assertFalse(F4f.makesConsonance(E4)); //m2
        assertFalse(D4f.makesConsonance(C4)); //M2
        assertTrue(G4f.makesConsonance(E4)); //m3
        assertTrue(E4f.makesConsonance(C4)); //M3
        assertFalse(F4f.makesConsonance(C4)); //P4
        assertFalse(B4f.makesConsonance(F4)); //TT
        assertTrue(G4f.makesConsonance(C4)); //P5
        assertTrue(C5f.makesConsonance(E4)); //m6
        assertTrue(A4f.makesConsonance(C4)); //M6
        assertFalse(C5f.makesConsonance(D4)); //m7
        assertFalse(B4f.makesConsonance(C4)); //M7
        assertTrue(C5f.makesConsonance(C4)); //P8
        assertFalse(F5f.makesConsonance(E4)); //m9
        assertFalse(D5f.makesConsonance(C4)); //M9
        assertTrue(F5f.makesConsonance(D4)); //m10
        assertTrue(E5f.makesConsonance(C4)); //M10
        assertFalse(F5f.makesConsonance(C4)); //P11
        assertFalse(Fs5f.makesConsonance(C4)); //TT
        assertTrue(G5f.makesConsonance(C4)); //P12
    }

    @Test
    public void testMakesPerfectConsonance() {
        assertTrue(C4f.makesPerfectConsonance(C4)); //unison

        //up
        assertFalse(E4f.makesPerfectConsonance(F4)); //m2
        assertFalse(C4f.makesPerfectConsonance(D4)); //M2
        assertFalse(E4f.makesPerfectConsonance(G4)); //m3
        assertFalse(C4f.makesPerfectConsonance(E4)); //M3
        assertFalse(C4f.makesPerfectConsonance(F4)); //P4
        assertFalse(F4f.makesPerfectConsonance(B4)); //TT
        assertTrue(C4f.makesPerfectConsonance(G4)); //P5
        assertFalse(E4f.makesPerfectConsonance(C5)); //m6
        assertFalse(C4f.makesPerfectConsonance(A4)); //M6
        assertFalse(D4f.makesPerfectConsonance(C5)); //m7
        assertFalse(C4f.makesPerfectConsonance(B4)); //M7
        assertTrue(C4f.makesPerfectConsonance(C5)); //P8
        assertFalse(E4f.makesPerfectConsonance(F5)); //m9
        assertFalse(C4f.makesPerfectConsonance(D5)); //M9
        assertFalse(D4f.makesPerfectConsonance(F5)); //m10
        assertFalse(C4f.makesPerfectConsonance(E5)); //M10
        assertFalse(C4f.makesConsonance(F5)); //P11
        assertFalse(C4f.makesConsonance(Fs5)); //TT
        assertTrue(C4f.makesConsonance(G5)); //P12

        //down
        assertFalse(F4f.makesPerfectConsonance(E4)); //m2
        assertFalse(D4f.makesPerfectConsonance(C4)); //M2
        assertFalse(G4f.makesPerfectConsonance(E4)); //m3
        assertFalse(E4f.makesPerfectConsonance(C4)); //M3
        assertFalse(F4f.makesPerfectConsonance(C4)); //P4
        assertFalse(B4f.makesPerfectConsonance(F4)); //TT
        assertTrue(G4f.makesPerfectConsonance(C4)); //P5
        assertFalse(C5f.makesPerfectConsonance(E4)); //m6
        assertFalse(A4f.makesPerfectConsonance(C4)); //M6
        assertFalse(C5f.makesPerfectConsonance(D4)); //m7
        assertFalse(B4f.makesPerfectConsonance(C4)); //M7
        assertTrue(C5f.makesPerfectConsonance(C4)); //P8
        assertFalse(F5f.makesPerfectConsonance(E4)); //m9
        assertFalse(D5f.makesPerfectConsonance(C4)); //M9
        assertFalse(F5f.makesPerfectConsonance(D4)); //m10
        assertFalse(E5f.makesPerfectConsonance(C4)); //M10
        assertFalse(F5f.makesConsonance(C4)); //P11
        assertFalse(Fs5f.makesConsonance(C4)); //TT
        assertTrue(G5f.makesConsonance(C4)); //P12
    }
}
