package getLongestSequence;

/**
 * A major part of the challenge here is to figure out what to do with this class.
 * We heavily recommended not to edit this (but you can).
 */
public class SequenceRange {
    public int matchingOnLeft, matchingOnRight;
    public int longestRange;

    public SequenceRange(int left, int right, int longest) {
        this.matchingOnLeft = left;
        this.matchingOnRight = right;
        this.longestRange = longest;
    }
}
