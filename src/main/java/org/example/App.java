package org.example;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws InterruptedException {
        int[] arrayToSort = generateRandomArray(10000);

        ThreadSafeFileWriter myFileWriter = ThreadSafeFileWriter.getInstance();

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            int[] arr = Arrays.copyOf(arrayToSort, arrayToSort.length);
            SortingAlgorithms.bubbleSort(arr);
            long endTime = System.currentTimeMillis();
            myFileWriter.appendOutputWithText("Bubble sort time: " + (endTime - startTime) + "\n" + Arrays.toString(arr));
        });

        executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            int[] arr = Arrays.copyOf(arrayToSort, arrayToSort.length);
            SortingAlgorithms.shellSort(arr);
            long endTime = System.currentTimeMillis();
            myFileWriter.appendOutputWithText("Shell sort time: " + (endTime - startTime) + "\n" + Arrays.toString(arr));
        });

        executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            int[] arr = Arrays.copyOf(arrayToSort, arrayToSort.length);
            SortingAlgorithms.quickSort(arr, 0, arrayToSort.length - 1);
            long endTime = System.currentTimeMillis();
            myFileWriter.appendOutputWithText("Quick sort time: " + (endTime - startTime) + "\n" + Arrays.toString(arr));
        });

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        ThreadSafeFileWriter.waitAndShutdown();
    }

    private static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000000);
        }
        return array;
    }
}
