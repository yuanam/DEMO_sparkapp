package com.manualdecisiontree;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;

public class DistributedDecisionTree {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("ManualDecisionTree")
                .master("local[*]")
                .getOrCreate();
        JavaRDD<LabeledPoint> rdd = loadData(spark, args);
        JavaRDD<LabeledPoint>[] splits = rdd.randomSplit(new double[]{0.7, 0.3}, 116);
        JavaRDD<LabeledPoint> trainingData = splits[0]; // 训练集
        JavaRDD<LabeledPoint> testData = splits[1];     // 测试集
        // 构建决策树
        System.out.println("开始构建决策树...");
        DecisionTreeNode tree = DecisionTreeBuilder.buildTree(trainingData, 5, 0);
        System.out.println("决策树构建完成：");
        System.out.println(tree);

        // 模型评估
        System.out.println("开始评估模型...");
        double accuracy = evaluateModel(tree, testData);
        System.out.println("模型准确率: " + accuracy);
    }

    // 加载数据的方法，假设数据文件为CSV，第一列为标签，后续列为特征
    public static JavaRDD<LabeledPoint> loadData(SparkSession spark, String[] args) {
        // 加载CSV文件
        JavaRDD<String> data = spark.read().textFile(args[0]).javaRDD();

        return data.map(new Function<String, LabeledPoint>() {
            public LabeledPoint call(String line) throws Exception {
                String[] parts = line.split(",");
                double label = Double.parseDouble(parts[parts.length - 1]);
                double[] features = new double[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    features[i - 1] = Double.parseDouble(parts[i]);
                }
                return new LabeledPoint(label, features);
            }
        });
    }

    // 模型评估方法
    public static double evaluateModel(final DecisionTreeNode tree, JavaRDD<LabeledPoint> rdd) {
        long correctPredictions = rdd.filter(new Function<LabeledPoint, Boolean>() {
            @Override
            public Boolean call(LabeledPoint lp) throws Exception {
                return predict(tree, lp.getFeatures()) == lp.getLabel();
            }
        }).count();

        long total = rdd.count();
        return (double) correctPredictions / total;
    }

    // 预测方法
    public static double predict(DecisionTreeNode node, double[] features) {
        if (node.isLeaf()) {
            return node.getLabel();
        }
        if (features[node.getFeatureIndex()] <= node.getThreshold()) {
            return predict(node.getLeft(), features);
        } else {
            return predict(node.getRight(), features);
        }
    } }