package com.counterpoint;

public enum Interval {

    unison(0),
    minorSecond(1),
    majorSecond(2),
    minorThird(3),
    majorThird(4),
    perfectFourth(5),
    tritone(6),
    perfectFifth(7),
    minorSixth(8),
    majorSixth(9),
    minorSeventh(10),
    majorSeventh(11),
    octave(12);
    final int distance;
    Interval(int distance) {
        this.distance = distance;
    }

    //may want to add function that reduces, say, major 13th down to major 6th if needed
    public boolean isDescendingInterval(int firstPitch, int nextPitch) {
        return (this.distance == (firstPitch-nextPitch));
    }

    public boolean isAscendingInterval(int firstPitch, int nextPitch) {
        return (this.distance == (nextPitch-firstPitch));
    }


}
