package com.nibonn.plants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        PreProcess pp = new PreProcess(args[0], args[1]);
        long start = System.currentTimeMillis();
        pp.process();
        System.out.println("pre-process time: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        List<int[]> data = loadData(args[0] + "_out.txt");
        System.out.println("load data time: " + (System.currentTimeMillis() - start));
        for (int i = 1; i <= 100; ++i) {
            KMeans km = new KMeans(i, data);
            start = System.currentTimeMillis();
            List<Cluster> clusters = km.run();
            long time = System.currentTimeMillis() - start;
            System.out.println(i + "\t" + time + "\t" + clusters.stream().mapToDouble(Cluster::sse).sum());
        }
    }

    static List<int[]> loadData(String filename) throws IOException {
        List<int[]> data = new LinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        Pattern p = Pattern.compile("[,]");
        while ((line = br.readLine()) != null) {
            String[] tokens = p.split(line);
            int[] point = new int[69];
            for (int i = 0; i < point.length; ++i) {
                point[i] = Integer.parseInt(tokens[i + 1]);
            }
            data.add(point);
        }
        br.close();
        return data;
    }
}
