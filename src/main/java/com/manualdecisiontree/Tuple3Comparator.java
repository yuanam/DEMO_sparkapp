package com.manualdecisiontree;

import java.util.Comparator;
import java.io.Serializable;

public class Tuple3Comparator implements Comparator<Tuple3<Integer, Double, Double>>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Tuple3<Integer, Double, Double> o1, Tuple3<Integer, Double, Double> o2) {
        return Double.compare(o1._3(), o2._3());
    }
}
