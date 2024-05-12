//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.netflix.util;


import java.io.Serializable;

public class Pair<E1, E2> implements Serializable {
    private static final long serialVersionUID = 2L;
    private E1 mFirst;
    private E2 mSecond;

    public Pair(E1 first, E2 second) {
        this.mFirst = first;
        this.mSecond = second;
    }

    public E1 first() {
        return this.mFirst;
    }

    public E2 second() {
        return this.mSecond;
    }

    public void setFirst(E1 first) {
        this.mFirst = first;
    }

    public void setSecond(E2 second) {
        this.mSecond = second;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            Pair other = (Pair)obj;
            return HashCode.equalObjects(this.mFirst, other.mFirst) && HashCode.equalObjects(this.mSecond, other.mSecond);
        } else {
            return false;
        }
    }

    public int hashCode() {
        HashCode h = new HashCode();
        h.addValue(this.mFirst);
        h.addValue(this.mSecond);
        return h.hashCode();
    }
}
