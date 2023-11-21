package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HasOver {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns true if arr has any elements strictly larger than val.
     * For example, if arr is [21, 17, 35, 8, 17, 1], then
     * main.java.hasOver(21, arr) == true and main.java.hasOver(35, arr) == false.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument.
     */
    private static int CUTOFF;
    private static int VAL;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        VAL = val;
        return POOL.invoke(new HasOverTask(arr, 0, arr.length));
    }

    /* TODO: Add a sequential method and parallel task here */
    public static boolean sequentialHasOver(int[] arr, int lo, int hi) {
        boolean test = false;
        int i = lo;
        while (i < hi && !test) {
            test = arr[i] > VAL;
            i++;
        }
        return test;
    }

    private static class HasOverTask extends RecursiveTask<Boolean> {
        private final int[] arr;
        private final int lo, hi;

        public HasOverTask(int[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }
        @Override
        protected Boolean compute() {
            if (hi - lo <= CUTOFF) {
                return sequentialHasOver(arr, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                HasOverTask left = new HasOverTask(arr, lo, mid);
                HasOverTask right = new HasOverTask(arr, mid, hi);

                left.fork();
                boolean rightResult = right.compute();
                boolean leftResult = left.join();

                return rightResult || leftResult;
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
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
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }
}
