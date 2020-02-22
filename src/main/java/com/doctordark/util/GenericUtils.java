package com.doctordark.util;

import java.util.*;

public final class GenericUtils {
    public static <E> List<E> createList(final Object object, final Class<E> type) {
        final List<E> output = new ArrayList<E>();
        if (object != null && object instanceof List) {
            final List<?> input = (List<?>) object;
            for (final Object value : input) {
                if (value != null) {
                    if (value.getClass() == null) {
                        continue;
                    }
                    if (!type.isAssignableFrom(value.getClass())) {
                        final String simpleName = type.getSimpleName();
                        throw new AssertionError((Object) ("Cannot cast to list! Key " + value + " is not a " + simpleName));
                    }
                    final E e = type.cast(value);
                    output.add(e);
                }
            }
        }
        return output;
    }

    public static <E> Set<E> castSet(final Object object, final Class<E> type) {
        final Set<E> output = new HashSet<E>();
        if (object != null && object instanceof List) {
            final List<?> input = (List<?>) object;
            for (final Object value : input) {
                if (value != null) {
                    if (value.getClass() == null) {
                        continue;
                    }
                    if (!type.isAssignableFrom(value.getClass())) {
                        final String simpleName = type.getSimpleName();
                        throw new AssertionError((Object) ("Cannot cast to list! Key " + value + " is not a " + simpleName));
                    }
                    final E e = type.cast(value);
                    output.add(e);
                }
            }
        }
        return output;
    }

    public static <K, V> Map<K, V> castMap(final Object object, final Class<K> keyClass, final Class<V> valueClass) {
        final Map<K, V> output = new HashMap<K, V>();
        if (object != null && object instanceof Map) {
            final Map<?, ?> input = (Map<?, ?>) object;
            final String keyClassName = keyClass.getSimpleName();
            final String valueClassName = valueClass.getSimpleName();
            for (final Object key : input.keySet().toArray()) {
                if (key != null && !keyClass.isAssignableFrom(key.getClass())) {
                    throw new AssertionError((Object) ("Cannot cast to HashMap: " + keyClassName + ", " + keyClassName + ". Value " + valueClassName + " is not a " + keyClassName));
                }
                final Object value = input.get(key);
                if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
                    throw new AssertionError((Object) ("Cannot cast to HashMap: " + valueClassName + ", " + valueClassName + ". Key " + key + " is not a " + valueClassName));
                }
                output.put(keyClass.cast(key), valueClass.cast(value));
            }
        }
        return output;
    }
}
