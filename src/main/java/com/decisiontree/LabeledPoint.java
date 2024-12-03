package com.decisiontree;

//public class LabeledPoint {
//    public double label;
//    public double[] features;
//
//    public LabeledPoint(double label, double[] features) {
//        this.label = label;
//        this.features = features;
//    }
//}
import java.io.Serializable;

public class LabeledPoint implements Serializable {
    public double label;
    public double[] features;

    public LabeledPoint(double label, double[] features) {
        this.label = label;
        this.features = features;
    }
}