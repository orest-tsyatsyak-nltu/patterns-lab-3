package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadSafeFileWriter {

    private static ThreadSafeFileWriter threadSafeFileWriter;

    private ThreadPoolExecutor executorService;

    private static volatile String outputFilename = "output.txt";

    private ThreadSafeFileWriter(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    public void appendOutputWithText(String text) {
        if(executorService == null) {
            throw new IllegalStateException("Was already shut down, to restart use ThreadSafeFileWriter.restart()");
        }
        executorService.submit(() -> writeToFile(outputFilename, text));
    }

    private static void writeToFile(String fileName, String text) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ThreadSafeFileWriter getInstance() {
        if (threadSafeFileWriter == null) {
            threadSafeFileWriter = new ThreadSafeFileWriter(new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>()));
        }
        return threadSafeFileWriter;
    }

    public static synchronized void setOutputFilename(String outputFilename) {
        ThreadSafeFileWriter.outputFilename = outputFilename;
    }

    public static synchronized void waitAndShutdown() {
        threadSafeFileWriter.executorService.shutdown();
        try {
            threadSafeFileWriter.executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadSafeFileWriter.executorService = null;
    }

    public static synchronized void restart() {
        threadSafeFileWriter.executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }



}
