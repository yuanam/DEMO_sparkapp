package com.mldecisiontree;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;

public class DecisionTreeML {
    public static void main(String[] args) {
        // 初始化Spark
        SparkConf conf = new SparkConf().setAppName("DecisionTreeExample")
                .setMaster("local")
                ;
        JavaSparkContext sc = new JavaSparkContext(conf);
        SparkSession spark = SparkSession.builder().appName("DecisionTreeExample").getOrCreate();

        // 加载数据
        Dataset<Row> rawData = spark.read()
                .option("header", "false") // 假设数据没有header
                .option("inferSchema", "true")
                .csv(args[0]);

        // 假设最后一列是label
        int numFeatures = rawData.columns().length - 1;
        String labelColumn = "_c" + numFeatures;

        // 为标签列创建索引 (如果标签是字符串)
        StringIndexer labelIndexer = new StringIndexer()
                .setInputCol(labelColumn)
                .setOutputCol("indexedLabel");
        Dataset<Row> indexedData = labelIndexer.fit(rawData).transform(rawData);

        // 将所有特征组合成一个向量
        String[] featureCols = new String[numFeatures];
        for (int i = 0; i < numFeatures; i++) {
            featureCols[i] = "_c" + i;
        }
        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(featureCols)
                .setOutputCol("features");
        Dataset<Row> finalData = vectorAssembler.transform(indexedData);

        // 划分训练集和测试集
        Dataset<Row>[] splits = finalData.randomSplit(new double[]{0.7, 0.3}, 116);
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testData = splits[1];

        // 创建决策树分类器
        DecisionTreeClassifier dt = new DecisionTreeClassifier()
                .setLabelCol("indexedLabel")
                .setFeaturesCol("features");

        // 训练模型
        DecisionTreeClassificationModel model = dt.fit(trainingData);

        // 对测试数据进行预测
        Dataset<Row> predictions = model.transform(testData);

        // 评估模型
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("indexedLabel")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");

        double accuracy = evaluator.evaluate(predictions);
        System.out.println("模型准确率: " + accuracy);

//        // 获取模型的调试字符串
//        String modelDebugString = model.toDebugString();
//
//        // 将调试信息保存到文件中
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {
//            writer.write("Decision Tree Model Structure:\n");
//            writer.write(modelDebugString);
//        } catch (IOException e) {
//            System.err.println("Error writing model debug string to file: " + e.getMessage());
//        }

        // 关闭Spark
        sc.close();
    }
}