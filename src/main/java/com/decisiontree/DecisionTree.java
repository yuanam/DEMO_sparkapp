package com.decisiontree;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import scala.collection.JavaConverters;
import scala.reflect.ClassTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DecisionTree {

    // 计算基尼不纯度
    public static class GiniImpurity {
        public static double compute(List<Double> labels) {
            int[] labelCounts = new int[2];  // 假设二分类
            for (double label : labels) {
                labelCounts[(int) label]++;
            }
            double leftProb = (double) labelCounts[0] / labels.size();
            double rightProb = (double) labelCounts[1] / labels.size();
            return 1 - (leftProb * leftProb + rightProb * rightProb);
        }
    }

    // 递归构建决策树
    public DecisionTreeNode buildTree(JavaRDD<LabeledPoint> rdd, int maxDepth, int depth) {
        List<LabeledPoint> data = rdd.collect();

        // 如果所有数据的标签相同，返回叶节点
        double label = data.get(0).label;
        boolean allSame = true;
        for (LabeledPoint point : data) {
            if (point.label != label) {
                allSame = false;
                break;
            }
        }
        if (allSame) {
            return new DecisionTreeNode(label);
        }

        if (depth >= maxDepth) {
            return new DecisionTreeNode(label);
        }

        // 遍历特征，找到最佳的分割
        double bestGini = Double.MAX_VALUE;
        int bestFeatureIndex = -1;
        double bestThreshold = -1;
        List<LabeledPoint> bestLeft = new ArrayList<>();
        List<LabeledPoint> bestRight = new ArrayList<>();
        for (int featureIndex = 0; featureIndex < data.get(0).features.length; featureIndex++) {
            List<Double> values = new ArrayList<>();
            for (LabeledPoint point : data) {
                values.add(point.features[featureIndex]);
            }
            List<Double> uniqueValues = new ArrayList<>(new HashSet<>(values));

            for (double threshold : uniqueValues) {
                List<LabeledPoint> left = new ArrayList<>();
                List<LabeledPoint> right = new ArrayList<>();
                for (LabeledPoint point : data) {
                    if (point.features[featureIndex] <= threshold) {
                        left.add(point);
                    } else {
                        right.add(point);
                    }
                }

                List<Double> leftLabels = new ArrayList<>();
                for (LabeledPoint point : left) leftLabels.add(point.label);
                List<Double> rightLabels = new ArrayList<>();
                for (LabeledPoint point : right) rightLabels.add(point.label);

                double gini = GiniImpurity.compute(leftLabels) * left.size() / data.size()
                        + GiniImpurity.compute(rightLabels) * right.size() / data.size();

                if (gini < bestGini) {
                    bestGini = gini;
                    bestFeatureIndex = featureIndex;
                    bestThreshold = threshold;
                    bestLeft = left;
                    bestRight = right;
                }
            }
        }

        // 将 Java List 转换为 Scala Seq，再 parallelize
        scala.collection.Seq<LabeledPoint> leftSeq = JavaConverters.asScalaBuffer(bestLeft).toList();
        scala.collection.Seq<LabeledPoint> rightSeq = JavaConverters.asScalaBuffer(bestRight).toList();

        // 获取 LabeledPoint 的 ClassTag
        ClassTag<LabeledPoint> classTag = scala.reflect.ClassTag$.MODULE$.apply(LabeledPoint.class);

        // 使用 parallelize 并传入 ClassTag
        RDD<LabeledPoint> leftRDD = rdd.context().parallelize(leftSeq, 1, classTag);
        RDD<LabeledPoint> rightRDD = rdd.context().parallelize(rightSeq, 1, classTag);

        // 将 RDD 转换为 JavaRDD
        JavaRDD<LabeledPoint> leftJavaRDD = JavaRDD.fromRDD(leftRDD, classTag);
        JavaRDD<LabeledPoint> rightJavaRDD = JavaRDD.fromRDD(rightRDD, classTag);

        // 递归构建左右子树
        DecisionTreeNode leftNode = buildTree(leftJavaRDD, maxDepth, depth + 1);
        DecisionTreeNode rightNode = buildTree(rightJavaRDD, maxDepth, depth + 1);

        return new DecisionTreeNode(bestFeatureIndex, bestThreshold, leftNode, rightNode);
    }

    // 预测函数
    public double predict(DecisionTreeNode node, double[] features) {
        if (node.label != null) {
            return node.label;
        }
        if (features[node.featureIndex] <= node.threshold) {
            return predict(node.left, features);
        } else {
            return predict(node.right, features);
        }
    }
}
