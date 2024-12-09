package com.manualdecisiontree;

import java.io.Serializable;

public class Tuple3<T1, T2, T3> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T1 _1;
    private T2 _2;
    private T3 _3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
    }

    public T1 _1() { return _1; }
    public T2 _2() { return _2; }
    public T3 _3() { return _3; }

    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ")";
    }
}
