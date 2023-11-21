package getLeftMostIndex;

import hasOver.HasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */
    private static int CUTOFF;
    private static char[] VAL;
    private static int SIZE;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        CUTOFF = sequentialCutoff;
        VAL = needle;
        SIZE = needle.length;
        return POOL.invoke(new GetLeftTask(haystack, 0, haystack.length));
    }


    /* TODO: Add a sequential method and parallel task here */
    public static int sequentialGetLeft(char[] arr, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            int test = SIZE - 1;
            int j = i;
            while (i - SIZE >= -1 && test >= 0 && arr[j] == VAL[test]) {test--; j--;}
            if (test == -1) {
              return j + 1;
            }
        }
        return -1;
    }

    private static class GetLeftTask extends RecursiveTask<Integer> {
        private final char[] arr;
        private final int lo, hi;

        public GetLeftTask(char[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }
        @Override
        protected Integer compute() {
            if (hi - lo <= CUTOFF) {
                return sequentialGetLeft(arr, lo, hi);
            } else {
                int mid = lo + (hi - lo) / 2;
                GetLeftTask left = new GetLeftTask(arr, lo, mid);
                GetLeftTask right = new GetLeftTask(arr, mid, hi);

                left.fork();
                int rightResult = right.compute();
                int leftResult = left.join();
                if (leftResult != -1) {
                    return leftResult;
                }
                return rightResult;
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
