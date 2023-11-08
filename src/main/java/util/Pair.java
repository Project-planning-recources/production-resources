package util;

import javafx.beans.NamedArg;

import java.util.Objects;

public class Pair<K,V> {

    private K key;

    public K getKey() { return key; }

    public void setKey(K key) {
        this.key = key;
    }

    private V value;
    public V getValue() { return value; }

    public void setValue(V value) {
        this.value = value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
