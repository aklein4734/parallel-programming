package getLongestSequence;

/**
 * A major part of the challenge here is to figure out what to do with this class.
 * We heavily recommended not to edit this (but you can).
 */
public class SequenceRange {
    public int matchingOnLeft, matchingOnRight;
    public int longestRange, sequenceLength;

    public SequenceRange(int left, int right, int longest, int length) {
        this.matchingOnLeft = left;
        this.matchingOnRight = right;
        this.longestRange = longest;
    }
}
