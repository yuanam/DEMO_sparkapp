package com.manualdecisiontree;

import java.io.Serializable;

public class LabeledPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private double label;
    private double[] features;

    public LabeledPoint(double label, double[] features) {
        this.label = label;
        this.features = features;
    }

    public double getLabel() {
        return label;
    }

    public double[] getFeatures() {
        return features;
    }
}

