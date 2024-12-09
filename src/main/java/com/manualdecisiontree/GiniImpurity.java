package com.manualdecisiontree;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class GiniImpurity implements Serializable {
    private static final long serialVersionUID = 1L;

    // 计算基尼不纯度，接受 Collection<Double> 而非 RDD
    public static double compute(Collection<Double> labels) {
        Map<Double, Long> labelCounts = labels.stream()
                .collect(Collectors.groupingBy(label -> label, Collectors.counting()));
        double gini = 1.0;
        long total = labels.size();
        for (Long count : labelCounts.values()) {
            double prob = (double) count / total;
            gini -= prob * prob;
        }
        return gini;
    }
}