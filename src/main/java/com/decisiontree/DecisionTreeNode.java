package com.decisiontree;

//public class DecisionTreeNode {
//    int featureIndex;  // 特征索引
//    double threshold;  // 划分阈值
//    DecisionTreeNode left;  // 左子树
//    DecisionTreeNode right; // 右子树
//    Double label;  // 叶节点的标签
//
//    // 内部节点构造函数
//    public DecisionTreeNode(int featureIndex, double threshold, DecisionTreeNode left, DecisionTreeNode right) {
//        this.featureIndex = featureIndex;
//        this.threshold = threshold;
//        this.left = left;
//        this.right = right;
//        this.label = null;
//    }
//
//    // 叶节点构造函数
//    public DecisionTreeNode(Double label) {
//        this.label = label;
//    }
//}
import java.io.Serializable;

public class DecisionTreeNode implements Serializable {
    // 给类加上 serialVersionUID，保证版本兼容
    private static final long serialVersionUID = 1L;

    int featureIndex;  // 特征索引
    double threshold;  // 划分阈值
    DecisionTreeNode left;  // 左子树
    DecisionTreeNode right; // 右子树
    Double label;  // 叶节点的标签

    // 内部节点构造函数
    public DecisionTreeNode(int featureIndex, double threshold, DecisionTreeNode left, DecisionTreeNode right) {
        this.featureIndex = featureIndex;
        this.threshold = threshold;
        this.left = left;
        this.right = right;
        this.label = null;
    }

    // 叶节点构造函数
    public DecisionTreeNode(Double label) {
        this.label = label;
    }
}
