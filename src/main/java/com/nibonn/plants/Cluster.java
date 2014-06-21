package com.nibonn.plants;

import java.util.List;

public class Cluster {
    private List<int[]> data;
    private double[] central;
    private boolean sseModified;
    private boolean centralModified;
    private double sse;

    public Cluster(List<int[]> data) {
        this.data = data;
        sseModified = centralModified = true;
    }

    public double sse() {
        if (sseModified) {
            central();
            sse = data.parallelStream().mapToDouble(a -> {
                double d = 0;
                for (int i = 0; i < a.length; ++i) {
                    double tmp = a[i] - central[i];
                    d += tmp * tmp;
                }
                return d;
            }).sum();
            sseModified = false;
        }
        return sse;
    }

    public double[] central() {
        if (centralModified) {
            if (central == null) {
                central = new double[data.get(0).length];
            }
            int[] tmp = data.parallelStream().reduce((a, b) -> {
               int[] r = new int[a.length];
               for (int i = 0; i < r.length; ++i) {
                   r[i] = a[i] + b[i];
               }
               return r;
            }).get();
            for (int i = 0, j = data.size(); i < tmp.length; ++i) {
                central[i] = tmp[i] / (double) j;
            }
            centralModified = false;
        }
        return central;
    }

    public List<int[]> getData() {
        return data;
    }

    public double dist(int[] point) {
        if (centralModified) {
            central();
        }
        double d = 0;
        for (int i = 0; i < central.length; ++i) {
            double tmp = central[i] - point[i];
            d += tmp * tmp;
        }
        return d;
    }

    public static double dist(double[] d1, double[] d2) {
        double d = 0;
        for (int i = 0; i < d1.length; ++i) {
            double tmp = d1[i] - d2[i];
            d += tmp * tmp;
        }
        return d;
    }

    public static double dist(double[] d1, int[] d2) {
        double d = 0;
        for (int i = 0; i < d1.length; ++i) {
            double tmp = d1[i] - d2[i];
            d += tmp * tmp;
        }
        return d;
    }

    public void removePoint(int[] p) {
        sseModified = centralModified = true;
        data.remove(p);
    }

    public void clear() {
        data.clear();
    }

    public void addPoint(int[] p) {
        sseModified = centralModified = true;
        data.add(p);
    }
}
