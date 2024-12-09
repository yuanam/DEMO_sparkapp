package com.manualdecisiontree;

import java.io.Serializable;

public class DecisionTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer featureIndex; // 分割特征的索引，叶节点为null
    private Double threshold;     // 分割阈值，叶节点为null
    private DecisionTreeNode left;  // 左子树
    private DecisionTreeNode right; // 右子树
    private Double label;            // 叶节点的标签，内部节点为null

    // 构造叶节点
    public DecisionTreeNode(Double label) {
        this.label = label;
    }

    // 构造内部节点
    public DecisionTreeNode(Integer featureIndex, Double threshold, DecisionTreeNode left, DecisionTreeNode right) {
        this.featureIndex = featureIndex;
        this.threshold = threshold;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return label != null;
    }

    public Integer getFeatureIndex() {
        return featureIndex;
    }

    public Double getThreshold() {
        return threshold;
    }

    public DecisionTreeNode getLeft() {
        return left;
    }

    public DecisionTreeNode getRight() {
        return right;
    }

    public Double getLabel() {
        return label;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return "Leaf(" + label + ")";
        } else {
            return "Node(featureIndex=" + featureIndex + ", threshold=" + threshold + ")";
        }
    }
}