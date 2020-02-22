package com.doctordark.internal.com.doctordark.base;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuavaCompat {

    // Enums
    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        checkNotNull(enumClass);
        checkNotNull(value);
        try {
            return Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }

    // MoreObjects
    public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
        return first != null ? first : Preconditions.checkNotNull(second);
    }
}
