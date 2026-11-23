 import java.util.Arrays;
import java.util.Random;

public class Practice9 {

    private static final long SORT_TIMEOUT_MS = 5000;

    public static int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(20001) - 10000;
        }
        return arr;
    }

    // Спосіб 1: Сортування масиву простим алгоритмом (сортування вибором за спаданням) і вибір 6-го елемента
    public static Integer find6thBySorting(int[] arr, long startTime) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            if (i % 1000 == 0 && (System.currentTimeMillis() - startTime) > SORT_TIMEOUT_MS) {
                return null;
            }
            int maxIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] > arr[maxIdx]) {
                    maxIdx = j;
                }
            }
            int temp = arr[maxIdx];
            arr[maxIdx] = arr[i];
            arr[i] = temp;
        }
        return arr[5];
    }

    // Спосіб 2: Зберігання 6 найбільших елементів у відсортованому масиві (за спаданням)
    public static int find6thByTopArray(int[] arr) {
        int[] top6 = new int[6];
        Arrays.fill(top6, Integer.MIN_VALUE);

        for (int num : arr) {
            if (num > top6[5]) {
                int j = 5;
                while (j > 0 && top6[j - 1] < num) {
                    top6[j] = top6[j - 1];
                    j--;
                }
                top6[j] = num;
            }
        }
        return top6[5];
    }

    // Спосіб 3: 6-разовий пошук максимуму із заміною знайденого максимуму на Integer.MIN_VALUE
    public static int find6thByRepeatedMax(int[] arr) {
        int sixthMax = Integer.MIN_VALUE;

        for (int k = 0; k < 6; k++) {
            int maxVal = Integer.MIN_VALUE;
            int maxIdx = -1;

            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > maxVal) {
                    maxVal = arr[i];
                    maxIdx = i;
                }
            }

            sixthMax = maxVal;
            if (maxIdx != -1) {
                arr[maxIdx] = Integer.MIN_VALUE;
            }
        }
        return sixthMax;
    }

    private static String formatTimes(long[] times, boolean timedOut) {
        if (timedOut) {
            return String.format(">%d(timeout)", SORT_TIMEOUT_MS);
        }
        long avg = (times[0] + times[1] + times[2]) / 3;
        return String.format("%d(%d %d %d)", avg, times[0], times[1], times[2]);
    }

    public static void main(String[] args) {
        int[] sizes = {10_000, 100_000, 1_000_000, 10_000_000, 100_000_000};
        int runs = 3;

        System.out.println("Виконується тестування (45 перевірок)...\n");
        System.out.printf("%-18s | %-22s | %-22s | %-25s | %-12s%n",
                "Розмір даних",
                "Спосіб 1 (Сортування)",
                "Спосіб 2 (Масив)",
                "Спосіб 3 (Макс. ел.)",
                "6-й елемент");
        System.out.println("-".repeat(108));

        for (int size : sizes) {
            long[] timesMethod1 = new long[runs];
            long[] timesMethod2 = new long[runs];
            long[] timesMethod3 = new long[runs];
            boolean method1TimedOut = false;
            int lastFoundElement = 0;

            for (int r = 0; r < runs; r++) {
                int[] baseArray = generateRandomArray(size);

                //  Перевірка Способу 1
                if (!method1TimedOut) {
                    int[] copy1 = Arrays.copyOf(baseArray, baseArray.length);
                    long start1 = System.currentTimeMillis();
                    Integer res1 = find6thBySorting(copy1, start1);
                    long end1 = System.currentTimeMillis();
                    if (res1 == null) {
                        method1TimedOut = true;
                    } else {
                        timesMethod1[r] = end1 - start1;
                    }
                }

                //  Перевірка Способу 2
                long start2 = System.currentTimeMillis();
                int res2 = find6thByTopArray(baseArray);
                long end2 = System.currentTimeMillis();
                timesMethod2[r] = end2 - start2;

                //  Перевірка Способу 3
                int[] copy3 = Arrays.copyOf(baseArray, baseArray.length);
                long start3 = System.currentTimeMillis();
                int res3 = find6thByRepeatedMax(copy3);
                long end3 = System.currentTimeMillis();
                timesMethod3[r] = end3 - start3;

                lastFoundElement = res2;
            }

            System.out.printf("%-18s | %-22s | %-22s | %-25s | %-12d%n",
                    String.format("%,d", size).replace(',', ' '),
                    formatTimes(timesMethod1, method1TimedOut),
                    formatTimes(timesMethod2, false),
                    formatTimes(timesMethod3, false),
                    lastFoundElement);
        }
    }
}
