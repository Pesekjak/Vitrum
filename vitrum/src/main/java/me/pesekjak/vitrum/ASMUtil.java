package me.pesekjak.vitrum;

import org.objectweb.asm.Type;

/**
 * Utils related to operations with ASM types.
 */
final class ASMUtil {

    private ASMUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns type from class dot path.
     *
     * @param dotPath dot path
     * @return type
     */
    public static Type getType(String dotPath) {
        return Type.getType(getDescriptor(dotPath));
    }

    /**
     * Returns descriptor of given dot path.
     *
     * @param dotPath dot path
     * @return descriptor for given dot path
     */
    public static String getDescriptor(String dotPath) {
        return "L" + dotPath.replace(".", "/") + ";";
    }

    /**
     * @param type type to check
     * @return whether the given type is primitive
     */
    public static boolean isPrimitive(Type type) {
        return !isComplex(type);
    }

    /**
     * @param type type to check
     * @return whether the given type is complex
     */
    public static boolean isComplex(Type type) {
        return type.getDescriptor().endsWith(";");
    }

    /**
     * @param type type to check
     * @return whether the given type is an array type
     */
    public static boolean isArray(Type type) {
        return type.getDescriptor().startsWith("[");
    }

}
