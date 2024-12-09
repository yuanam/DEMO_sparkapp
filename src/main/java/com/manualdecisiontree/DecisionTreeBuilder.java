package com.manualdecisiontree;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

public class DecisionTreeBuilder {

    // 构建决策树的方法
    public static DecisionTreeNode buildTree(JavaRDD<LabeledPoint> rdd, int maxDepth, int depth) {
        // 缓存当前RDD，以避免重复计算
        rdd.cache();

        // 1. 检查是否所有标签相同
        long distinctLabels = rdd.map(lp -> lp.getLabel()).distinct().count();
        if (distinctLabels == 1) {
            double label = rdd.first().getLabel();
            return new DecisionTreeNode(label);
        } else if (distinctLabels == 0) {
            return new DecisionTreeNode(null);
        }

        // 2. 检查是否达到最大深度
        if (depth >= maxDepth) {
            double majorityLabel = rdd.mapToPair(lp -> new Tuple2<>(lp.getLabel(), 1L))
                    .reduceByKey(Long::sum)
                    .mapToPair(Tuple2::swap)
                    .sortByKey(false)
                    .first()._2();
            return new DecisionTreeNode(majorityLabel);
        }

        // 3. 寻找最佳分割点
        Tuple3<Integer, Double, Double> bestSplit = findBestSplit(rdd);
        if (bestSplit == null) {
            double majorityLabel = rdd.mapToPair(lp -> new Tuple2<>(lp.getLabel(), 1L))
                    .reduceByKey(Long::sum)
                    .mapToPair(Tuple2::swap)
                    .sortByKey(false)
                    .first()._2();
            return new DecisionTreeNode(majorityLabel);
        }

        int bestFeature = bestSplit._1();
        double bestThreshold = bestSplit._2();

        // 4. 分割数据并递归构建子树
        JavaRDD<LabeledPoint> leftRDD = rdd.filter(lp -> lp.getFeatures()[bestFeature] <= bestThreshold);
        JavaRDD<LabeledPoint> rightRDD = rdd.filter(lp -> lp.getFeatures()[bestFeature] > bestThreshold);

        DecisionTreeNode leftNode = buildTree(leftRDD, maxDepth, depth + 1);
        DecisionTreeNode rightNode = buildTree(rightRDD, maxDepth, depth + 1);

        return new DecisionTreeNode(bestFeature, bestThreshold, leftNode, rightNode);
    }

    // 寻找最佳分割点的方法
    public static Tuple3<Integer, Double, Double> findBestSplit(JavaRDD<LabeledPoint> rdd) {
        int numFeatures = rdd.first().getFeatures().length;

        // 对每个特征并行计算最佳分割点
        JavaRDD<Tuple3<Integer, Double, Double>> featureSplits = rdd.mapPartitions(iter -> {
            List<Tuple3<Integer, Double, Double>> splits = new ArrayList<>();
            List<LabeledPoint> data = new ArrayList<>();
            iter.forEachRemaining(data::add);

            for (int featureIndex = 0; featureIndex < numFeatures; featureIndex++) {
                // 获取当前特征的所有值并排序
                int finalFeatureIndex2 = featureIndex;
                List<Double> featureValues = data.stream()
                        .map(lp -> lp.getFeatures()[finalFeatureIndex2])
                        .sorted()
                        .distinct()
                        .collect(Collectors.toList());
                double bestGini = Double.MAX_VALUE;
                double bestThreshold = 0.0;
                for (int i = 1; i < featureValues.size(); i++) {
                    double threshold = (featureValues.get(i - 1) + featureValues.get(i)) / 2.0;
                    int finalFeatureIndex = featureIndex;
                    List<Double> leftLabels = data.stream()
                            .filter(lp -> lp.getFeatures()[finalFeatureIndex] <= threshold)
                            .map(LabeledPoint::getLabel)
                            .collect(Collectors.toList());
                    int finalFeatureIndex1 = featureIndex;
                    List<Double> rightLabels = data.stream()
                            .filter(lp -> lp.getFeatures()[finalFeatureIndex1] > threshold)
                            .map(LabeledPoint::getLabel)
                            .collect(Collectors.toList());
                    if (leftLabels.isEmpty() || rightLabels.isEmpty()) continue;

                    // 计算基尼不纯度
                    double gini = GiniImpurity.compute(leftLabels) * leftLabels.size() +
                            GiniImpurity.compute(rightLabels) * rightLabels.size();

                    if (gini < bestGini) {
                        bestGini = gini;
                        bestThreshold = threshold;
                    }
                }
                splits.add(new Tuple3<>(featureIndex, bestThreshold, bestGini));
            }
            return splits.iterator();
        });

        // 使用可序列化的 Comparator 类替代 Lambda 表达式
        Tuple3<Integer, Double, Double> bestSplit = featureSplits.min(new Tuple3Comparator());

        return bestSplit;
    }
}

