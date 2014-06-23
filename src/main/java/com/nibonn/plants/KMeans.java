package com.nibonn.plants;

import java.util.LinkedList;
import java.util.List;

public class KMeans {

    private List<Cluster> clusters;
    final private int K;
    public static final double AS_ZERO = 1e-3;

    public KMeans(int k, List<int[]> data) {
        Cluster c = new Cluster(data);
        clusters = new LinkedList<>();
        clusters.add(c);
        K = k;
    }

    /**
     * cluster data set to K clusters
     * @return list of clusters
     */
    public List<Cluster> run() {
        while (clusters.size() < K) {
            split();
        }
        return clusters;
    }

    private void split() {
        Cluster c = clusters.parallelStream().reduce((a, b) -> a.sse() > b.sse() ? a : b).get();
        clusters.remove(c);
        List<int[]> data = c.getData();
        Cluster c1 = new Cluster(new LinkedList<>());
        Cluster c2 = new Cluster(new LinkedList<>());
        // use first two points as initial central points
        c1.addPoint(data.get(0));
        c2.addPoint(data.get(1));
        double[] tmp1 = c1.central();
        double[] tmp2 = c2.central();
        double[] d1 = new double[tmp1.length];
        double[] d2 = new double[tmp2.length];
        do {
            System.arraycopy(tmp1, 0, d1, 0, d1.length);
            System.arraycopy(tmp2, 0, d2, 0, d2.length);
            c1.clear();
            c2.clear();
            data.stream().forEach(d -> {
                if (Cluster.dist(d1, d) > Cluster.dist(d2, d)) {
                    c2.addPoint(d);
                } else {
                    c1.addPoint(d);
                }
            });
            tmp1 = c1.central();
            tmp2 = c2.central();
        } while (Cluster.dist(d1, tmp1) >= AS_ZERO || Cluster.dist(d2, tmp2) >= AS_ZERO);
        clusters.add(c1);
        clusters.add(c2);
    }
}
