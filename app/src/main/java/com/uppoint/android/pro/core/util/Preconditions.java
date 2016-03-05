package com.uppoint.android.pro.core.util;

/**
 */
public final class Preconditions {

    private Preconditions() {
        // deny instantiation
    }

    public static void nonNull(Object reference, String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
    }

    public static void stateNonNull(Object reference, String message) {
        if (reference == null) {
            throw new IllegalStateException(message);
        }
    }

    public static void instanceOf(Object reference, Class<?> clazz, String message) {
        nonNull(reference, "Reference can't be null");
        nonNull(clazz, "Class can't be null");

        if (!clazz.isAssignableFrom(reference.getClass())) {
            throw new IllegalArgumentException(message);
        }
    }

}
