//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.netflix.util;

public class HashCode {
    private static final int SEED = 17;
    private static final long SCALE = 37L;
    private int mVal = 17;

    public HashCode() {
    }

    public int addValue(Object obj) {
        return this.foldIn(obj != null ? obj.hashCode() : 0);
    }

    public int addValue(boolean b) {
        return this.foldIn(b ? 0 : 1);
    }

    public int addValue(byte i) {
        return this.foldIn(i);
    }

    public int addValue(char i) {
        return this.foldIn(i);
    }

    public int addValue(short i) {
        return this.foldIn(i);
    }

    public int addValue(int i) {
        return this.foldIn(i);
    }

    public int addValue(float f) {
        return this.foldIn(Float.floatToIntBits(f));
    }

    public int addValue(double f) {
        return this.foldIn(Double.doubleToLongBits(f));
    }

    public int addValue(Object[] array) {
        int val = this.hashCode();
        Object[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object obj = arr$[i$];
            val = this.addValue(obj);
        }

        return val;
    }

    public int addValue(boolean[] array) {
        int val = this.hashCode();
        boolean[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            boolean b = arr$[i$];
            val = this.addValue(b);
        }

        return val;
    }

    public int addValue(byte[] array) {
        int val = this.hashCode();
        byte[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte i = arr$[i$];
            val = this.addValue(i);
        }

        return val;
    }

    public int addValue(char[] array) {
        int val = this.hashCode();
        char[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            char i = arr$[i$];
            val = this.addValue(i);
        }

        return val;
    }

    public int addValue(short[] array) {
        int val = this.hashCode();
        short[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            short i = arr$[i$];
            val = this.addValue(i);
        }

        return val;
    }

    public int addValue(int[] array) {
        int val = this.hashCode();
        int[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            int i = arr$[i$];
            val = this.addValue(i);
        }

        return val;
    }

    public int addValue(float[] array) {
        int val = this.hashCode();
        float[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            float f = arr$[i$];
            val = this.addValue(f);
        }

        return val;
    }

    public int addValue(double[] array) {
        int val = this.hashCode();
        double[] arr$ = array;
        int len$ = array.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            double f = arr$[i$];
            val = this.addValue(f);
        }

        return val;
    }

    public static boolean equalObjects(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o2 == null ? false : o1.equals(o2);
        }
    }

    private int foldIn(int c) {
        return this.setVal(37L * (long)this.mVal + (long)c);
    }

    private int foldIn(long c) {
        return this.setVal(37L * (long)this.mVal + c);
    }

    private int setVal(long l) {
        this.mVal = (int)(l ^ l >>> 32);
        return this.mVal;
    }

    public int hashCode() {
        return this.mVal;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            HashCode h = (HashCode)obj;
            return h.hashCode() == this.hashCode();
        } else {
            return false;
        }
    }

    public String toString() {
        return "{HashCode " + this.mVal + "}";
    }
}
