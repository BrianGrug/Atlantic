package com.doctordark.compat.com.google.common.collect;

import com.google.common.base.*;
import javax.annotation.*;

public class GuavaCompat {
    public static <T extends Enum<T>> Optional<T> getIfPresent(final Class<T> enumClass, final String value) {
        Preconditions.checkNotNull((Object) enumClass);
        Preconditions.checkNotNull((Object) value);
        try {
            return (Optional<T>) Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }

    public static <T> T firstNonNull(@Nullable final T first, @Nullable final T second) {
        return (T) ((first != null) ? first : Preconditions.checkNotNull(second));
    }
}
