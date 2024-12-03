package com.decisiontree;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.function.Function;

public class DecisionTreeExample {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("ManualDecisionTree")
                .master("local[*]")
                .getOrCreate();

        // 加载数据
        JavaRDD<LabeledPoint> rdd = loadData(spark);

        // 训练决策树
        DecisionTree decisionTree = new DecisionTree();
        DecisionTreeNode tree = decisionTree.buildTree(rdd, 5, 0);

        // 评估模型
        double accuracy = evaluateModel(tree, rdd);
        System.out.println("Model Accuracy: " + accuracy);

        spark.stop();
    }

    public static JavaRDD<LabeledPoint> loadData(SparkSession spark) {
        // 加载CSV文件
        JavaRDD<String> data = spark.read().textFile("src/main/resources/data.csv").javaRDD();

        return data.map(new Function<String, LabeledPoint>() {
            public LabeledPoint call(String line) throws Exception {
                String[] parts = line.split(",");
                double label = Double.parseDouble(parts[-1]);
                double[] features = new double[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    features[i - 1] = Double.parseDouble(parts[i]);
                }
                return new LabeledPoint(label, features);
            }
        });
    }

    // 评估模型的准确度
    public static double evaluateModel(final DecisionTreeNode tree, JavaRDD<LabeledPoint> rdd) {
        long correctPredictions = rdd.filter(new Function<LabeledPoint, Boolean>() {
            public Boolean call(LabeledPoint point) throws Exception {
                return predict(tree, point.features) == point.label;
            }
        }).count();

        long total = rdd.count();
        return (double) correctPredictions / total;
    }

    // 预测方法
    public static double predict(DecisionTreeNode tree, double[] features) {
        if (tree.label != null) {
            return tree.label;
        }
        if (features[tree.featureIndex] <= tree.threshold) {
            return predict(tree.left, features);
        } else {
            return predict(tree.right, features);
        }
    }
}

