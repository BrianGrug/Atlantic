package com.doctordark.util;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;
import javax.annotation.*;

public class ConcurrentValueOrderedMap<K, V extends Comparable<V>> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    private final Set<InternalEntry<K, V>> ordering;
    private final ConcurrentMap<K, InternalEntry<K, V>> lookup;

    public ConcurrentValueOrderedMap() {
        this.ordering = new ConcurrentSkipListSet<InternalEntry<K, V>>();
        this.lookup = new ConcurrentHashMap<K, InternalEntry<K, V>>();
    }

    @Override
    public V get(final Object key) {
        final InternalEntry<K, V> old = this.lookup.get(key);
        return (V) ((old != null) ? old.getValue() : null);
    }

    @Override
    public V put(final K key, final V val) {
        final InternalEntry<K, V> entry = new InternalEntry<K, V>(key, val);
        final InternalEntry<K, V> old = this.lookup.put(key, entry);
        if (old == null) {
            this.ordering.add(entry);
            return null;
        }
        this.ordering.remove(old);
        this.ordering.add(entry);
        return old.getValue();
    }

    @Override
    public V remove(final Object key) {
        final InternalEntry<K, V> old = this.lookup.remove(key);
        if (old != null) {
            this.ordering.remove(old);
            return old.getValue();
        }
        return null;
    }

    @Override
    public void clear() {
        this.ordering.clear();
        this.lookup.clear();
    }

    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Entry<K, V>>) this.ordering);
    }

    private static class InternalEntry<K, V extends Comparable<V>> implements Comparable<InternalEntry<K, V>>, Entry<K, V> {
        private final K key;
        private final V value;

        InternalEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(final V value) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(@Nonnull final InternalEntry<K, V> o) {
            return o.value.compareTo(this.value);
        }
    }
}
