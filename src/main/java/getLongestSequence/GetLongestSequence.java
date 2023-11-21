package getLongestSequence;

import hasOver.HasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */
    private static int CUTOFF;
    private static int VAL;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        VAL = val;
        return POOL.invoke(new GetLongestTask(arr, 0, arr.length)).longestRange;
    }


    /* TODO: Add a sequential method and parallel task here */
    public static SequenceRange sequentialGetLongest(int[] arr, int lo, int hi) {
        SequenceRange returner = new SequenceRange(0, 0, 0);
        int num = 0;
        for (int i = lo; i < hi; i++) {
            if (VAL == arr[i]) {
                num++;
            } else {
                if (arr[lo] == VAL && lo + num == i) {
                    returner.matchingOnLeft = num;
                }
                if (num > returner.longestRange) {
                    returner.longestRange = num;
                }
                num = 0;
            }
        }
        if (arr[lo] == VAL && lo + num == hi) {
            returner.matchingOnLeft = num;
        }
        if (arr[hi - 1] == VAL) {
            returner.matchingOnRight = num;
        }
        if (num > returner.longestRange) {
            returner.longestRange = num;
        }
        return returner;
    }

    private static class GetLongestTask extends RecursiveTask<SequenceRange> {
        private final int[] arr;
        private final int lo, hi;

        public GetLongestTask(int[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }
        @Override
        protected SequenceRange compute() {
            if (hi - lo <= CUTOFF) {
                return sequentialGetLongest(arr, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                GetLongestTask left = new GetLongestTask(arr, lo, mid);
                GetLongestTask right = new GetLongestTask(arr, mid, hi);

                left.fork();
                SequenceRange rightResult = right.compute();
                SequenceRange leftResult = left.join();
                SequenceRange returner = new SequenceRange(0, 0, 0);
                if (leftResult.longestRange == mid - lo && rightResult.longestRange == hi - mid) {
                    returner.matchingOnLeft = leftResult.matchingOnLeft + rightResult.matchingOnLeft;
                    returner.matchingOnRight = leftResult.matchingOnLeft + rightResult.matchingOnLeft;
                    returner.longestRange = leftResult.matchingOnLeft + rightResult.matchingOnLeft;
                } else if (leftResult.longestRange == mid - lo) {
                    returner.matchingOnLeft = leftResult.matchingOnLeft + rightResult.matchingOnLeft;
                    returner.matchingOnRight = rightResult.matchingOnRight;
                    returner.longestRange = Math.max(returner.matchingOnLeft, rightResult.longestRange);
                } else if (rightResult.longestRange == hi - mid) {
                    returner.matchingOnLeft = leftResult.matchingOnLeft;
                    returner.matchingOnRight = leftResult.matchingOnRight + rightResult.matchingOnLeft;
                    returner.longestRange = Math.max(returner.matchingOnRight, leftResult.longestRange);
                } else {
                    returner.matchingOnLeft = leftResult.matchingOnLeft;
                    returner.matchingOnRight = rightResult.matchingOnRight;
                    int test = Math.max(leftResult.longestRange, rightResult.longestRange);
                    returner.longestRange = Math.max(test, leftResult.matchingOnRight + rightResult.matchingOnLeft);
                }
                return returner;
            }
        }
    }
    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}