package me.pesekjak.vitrum;

import org.jetbrains.annotations.Nullable;

/**
 * Converter that checks whether it is safe to cast.
 */
final class SafeCastConverter {

    private SafeCastConverter() {
        throw new UnsupportedOperationException();
    }

    public static @Nullable Object safeCast(Object o, Class<?> clazz) {
        if (o == null) return null;
        return clazz.isAssignableFrom(o.getClass()) ? o : null;
    }

}
