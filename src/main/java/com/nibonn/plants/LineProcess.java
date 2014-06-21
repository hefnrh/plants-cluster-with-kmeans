package com.nibonn.plants;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * process line concurrently, unordered.
 */
public abstract class LineProcess {

    private PrintWriter pw;
    private BufferedReader br;

    /**
     * @param filename data file
     * @throws FileNotFoundException if file not exists
     */
    public LineProcess(String filename) throws FileNotFoundException {
        br = new BufferedReader(new FileReader(filename));
        pw = new PrintWriter(filename + "_out.txt");
    }

    /**
     * start process
     *
     * @throws IOException
     */
    final public void process() throws IOException {
        final int SIZE = Runtime.getRuntime().availableProcessors();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(SIZE << 1);
        ProcessThread[] threads = new ProcessThread[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            threads[i] = new ProcessThread(queue);
            threads[i].start();
        }
        String line;
        while ((line = br.readLine()) != null) {
            try {
                queue.put(line);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        br.close();
        for (ProcessThread pt : threads) {
            pt.stopRunning();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        pw.flush();
        pw.close();
    }

    /**
     * process every line
     *
     * @param line text to process
     * @return text after process
     */
    protected abstract String processLine(String line);

    private class ProcessThread extends Thread {
        BlockingQueue<String> bq;
        boolean running;

        ProcessThread(BlockingQueue<String> bq) {
            this.bq = bq;
            running = true;
        }

        @Override
        public void run() {
            while (running || !bq.isEmpty()) {
                try {
                    String line = bq.poll(1L, TimeUnit.SECONDS);
                    if (line != null) {
                        line = processLine(line);
                        if (line != null) {
                            pw.println(line);
                        }
                    }
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        void stopRunning() {
            running = false;
        }
    }
}
