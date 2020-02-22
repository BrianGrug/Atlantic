package com.doctordark.compat.com.google.common.collect;

import com.google.common.annotations.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.*;
import com.google.common.collect.*;

@GwtCompatible(emulated = true)
public abstract class FluentIterableCompat<E> implements Iterable<E> {
    private final Iterable<E> iterable;

    FluentIterableCompat(final Iterable<E> iterable) {
        this.iterable = (Iterable<E>) Preconditions.checkNotNull((Object) iterable);
    }

    @CheckReturnValue
    public static <E> FluentIterableCompat<E> from(final Iterable<E> iterable) {
        return (iterable instanceof FluentIterableCompat) ? ((FluentIterableCompat) iterable) : new FluentIterableCompat<E>(iterable) {
            @Override
            public Iterator<E> iterator() {
                return iterable.iterator();
            }
        };
    }

    @CheckReturnValue
    @Override
    public String toString() {
        return Iterables.toString((Iterable) this.iterable);
    }

    @CheckReturnValue
    public final FluentIterableCompat<E> filter(final Predicate<? super E> predicate) {
        return from((Iterable<E>) Iterables.filter((Iterable) this.iterable, (Predicate) predicate));
    }

    @CheckReturnValue
    public final <T> FluentIterableCompat<T> transform(final Function<? super E, T> function) {
        return from((Iterable<T>) Iterables.transform((Iterable) this.iterable, (Function) function));
    }

    @CheckReturnValue
    public final ImmutableList<E> toList() {
        return (ImmutableList<E>) ImmutableList.copyOf((Iterable) this.iterable);
    }
}
