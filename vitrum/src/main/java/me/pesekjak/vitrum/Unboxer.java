package me.pesekjak.vitrum;

/**
 * Utility class that unboxes the primitive types.
 */
final class Unboxer {

    private Unboxer() {
        throw new UnsupportedOperationException();
    }

    public static boolean asBoolean(Object o) {
        return (o instanceof Boolean b) ? b : false;
    }

    public static char asChar(Object o) {
        return (o instanceof Character c) ? c : (char) 0;
    }

    public static byte asByte(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.byteValue();
    }

    public static short asShort(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.shortValue();
    }

    public static int asInt(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.intValue();
    }

    public static long asLong(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.longValue();
    }

    public static float asFloat(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.floatValue();
    }

    public static double asDouble(Object o) {
        if (!(o instanceof Number n)) return 0;
        return n.doubleValue();
    }

}
