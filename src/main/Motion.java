public enum Motion {
    SIMILAR,
    PARALLEL, //subcase of similar?
    CONTRARY,
    OBLIQUE;

    /**
     * REWRITE MORE CLEAR
     * Despite "first pitch" in some argument names, this need not be the actual first pitch
     * of the entire melodic line, simply the pitch that begins the motion.
     * @param firstPitchV1 pitch that starts the motion you want to check in the first voice
     * @param nextPitchV1 pitch following that which starts the motion in the first voice
     * @param firstPitchV2 pitch that starts the motion you want to check in the second voice
     * @param nextPitchV2 pitch following that which starts the motion in the second voice
     * @return the type of motion between two melodic lines.
     */
    public static Motion isMotion(int firstPitchV1, int nextPitchV1, int firstPitchV2, int nextPitchV2) {
        int V1Interval = (nextPitchV1 - firstPitchV1); //positive if ascending, negative if descending
        int V2Interval = (nextPitchV2 - firstPitchV2); //positive if ascending, negative if descending
        if(V1Interval == 0 || V2Interval == 0) {
            return OBLIQUE;
        } else if(V1Interval == V2Interval) {
            return PARALLEL;
        } else if (V1Interval * V2Interval < 0) { //if one positive, one negative, must return a negative number
            return CONTRARY;
        } else {
            return SIMILAR;
        }
    }

    public static boolean isOblique(int firstPitchV1, int nextPitchV1, int firstPitchV2, int nextPitchV2) {
        return(isMotion(firstPitchV1, nextPitchV1, firstPitchV2, nextPitchV2) == OBLIQUE);
    }

    public static boolean isParallel(int firstPitchV1, int nextPitchV1, int firstPitchV2, int nextPitchV2) {
        return(isMotion(firstPitchV1, nextPitchV1, firstPitchV2, nextPitchV2) == PARALLEL);
    }

    public static boolean isContrary(int firstPitchV1, int nextPitchV1, int firstPitchV2, int nextPitchV2) {
        return(isMotion(firstPitchV1, nextPitchV1, firstPitchV2, nextPitchV2) == CONTRARY);
    }

    public static boolean isSimilar(int firstPitchV1, int nextPitchV1, int firstPitchV2, int nextPitchV2) {
        return(isMotion(firstPitchV1, nextPitchV1, firstPitchV2, nextPitchV2) == SIMILAR);
    }

}
