package filterEmpty;

import hasOver.HasOver;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */
    public static int[] filterEmpty(String[] arr) {
        //System.out.println(Arrays.toString(arr));
        int[] bits = new int[arr.length];
        mapToBitSet(arr, bits);
        //System.out.println(Arrays.toString(bits));
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);

        return mapToOutput(arr, bits, bitsum);
    }

    public static int[] mapToBitSet(String[] arr, int[] bits) {
        /* TODO: Edit this with your code */
        return POOL.invoke(new mapToBitSetTask(arr, bits, 0, arr.length));
    }

    /* TODO: Add a sequential method and parallel task here */
    public static int[] sequentialMapToBitSet(String[] arr, int[] bits, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            if (arr[i].getBytes().length != 0) bits[i] = 1;
            else bits[i] = 0;
        }
        return bits;
    }

    private static class mapToBitSetTask extends RecursiveTask<int[]> {
        private final String[] arr;
        private int[] bits;
        private final int lo, hi;

        public mapToBitSetTask(String[] arr, int[] bits, int lo, int hi) {
            this.bits = bits;
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }
        @Override
        protected int[] compute() {
            if (hi - lo <= 1) {
                return sequentialMapToBitSet(arr, bits, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                mapToBitSetTask left = new mapToBitSetTask(arr, bits, lo, mid);
                mapToBitSetTask right = new mapToBitSetTask(arr, bits, mid, hi);

                left.fork();
                int[] rightResult = right.compute();
                int[] leftResult = left.join();

                return rightResult;
            }
        }
    }

    /* TODO: Add a sequential method and parallel task here */

    public static int[] mapToOutput(String[] input, int[] bits, int[] bitsum) {
        /* TODO: Edit this with your code */
        if (bitsum.length == 0) {
            return bitsum;
        }
        int[] result = new int[bitsum[bitsum.length - 1]];
        return POOL.invoke(new mapToOutputTask(result, input, bits, bitsum, 0, input.length));
    }

    private static class mapToOutputTask extends RecursiveTask<int[]> {
        private final String[] arr;
        private int[] bits, bitsum, result;
        private final int lo, hi;

        public mapToOutputTask(int[] result, String[] arr, int[] bits, int[] bitsum, int lo, int hi) {
            this.bits = bits;
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.bitsum = bitsum;
            this.result = result;
        }

        @Override
        protected int[] compute() {
            if (hi - lo <= 1) {
                for (int i = lo; i < hi; i++) {
                    if (bits[lo] == 1) {
                        result[bitsum[lo] - 1] = arr[lo].length();
                    }
                }
                return result;
            } else {
                int mid = lo + (hi - lo) / 2;
                mapToOutputTask left = new mapToOutputTask(result, arr, bits, bitsum, lo, mid);
                mapToOutputTask right = new mapToOutputTask(result, arr, bits, bitsum, mid, hi);

                left.fork();
                int[] rightResult = right.compute();
                int[] leftResult = left.join();

                return rightResult;
            }
        }
    }
    /* TODO: Add a sequential method and parallel task here */

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}