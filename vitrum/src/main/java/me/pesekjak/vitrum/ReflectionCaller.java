package me.pesekjak.vitrum;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for simple use of Java reflection library.
 */
class ReflectionCaller {

    /**
     * Invokes a method.
     *
     * @param source internal name of the source class
     * @param name name of the method
     * @param descriptor descriptor of the method
     * @param instance instance
     * @param arguments arguments
     * @return method output
     */
    static @Nullable Object callMethod(String source, String name, String descriptor, Object instance, Object[] arguments) throws Exception {
        Class<?> clazz = Class.forName(source.replace('/', '.'));
        List<Class<?>> types = getArgumentTypes(splitMethodDesc(descriptor));
        Method method = clazz.getDeclaredMethod(name, types.toArray(new Class[0]));
        method.setAccessible(true);
        return method.invoke(instance, arguments);
    }

    /**
     * Invokes a constructor.
     *
     * @param source internal name of the source class
     * @param descriptor descriptor of the method
     * @param arguments arguments
     * @return new instance
     */
    static Object callConstructor(String source, String descriptor, Object[] arguments) throws Exception {
        Class<?> clazz = Class.forName(source.replace('/', '.'));
        List<Class<?>> types = getArgumentTypes(splitMethodDesc(descriptor));
        Constructor<?> constructor = clazz.getDeclaredConstructor(types.toArray(new Class[0]));
        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
    }

    /**
     * Returns value of a field.
     *
     * @param source internal name of the source class
     * @param name name of the field
     * @param instance instance
     * @return field value
     */
    static @Nullable Object getField(String source, String name, Object instance) throws Exception {
        Class<?> clazz = Class.forName(source.replace('/', '.'));
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Changes value of a field.
     *
     * @param source internal name of the source class
     * @param name name of the field
     * @param instance instance
     * @param value new value
     */
    static void setField(String source, String name, Object instance, Object value) throws Exception {
        Class<?> clazz = Class.forName(source.replace('/', '.'));
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * Converts method descriptor to list of parameter classes.
     *
     * @param descriptors descriptors of the method (including the return type)
     * @return parameter types of the method (without the return type)
     */
    private static List<Class<?>> getArgumentTypes(List<String> descriptors) throws ClassNotFoundException {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < descriptors.size() - 1; i++)
            types.add(descriptorToType(descriptors.get(i)));
        return types;
    }

    /**
     * Converts descriptor to type.
     *
     * @param descriptor descriptor
     * @return type
     */
    private static Class<?> descriptorToType(String descriptor) throws ClassNotFoundException {
        int arrayLength = 0;

        for (char next : descriptor.toCharArray()) {
            if (next != '[') break;
            arrayLength++;
        }

        Class<?> clazz = switch (descriptor.charAt(arrayLength)) {
            case 'Z' -> boolean.class;
            case 'C' -> char.class;
            case 'B' -> byte.class;
            case 'S' -> short.class;
            case 'I' -> int.class;
            case 'F' -> float.class;
            case 'J' -> long.class;
            case 'D' -> double.class;
            case 'L' -> Class.forName(descriptor.substring(arrayLength + 1, descriptor.length() - 1).replace('/', '.'));
            default -> throw new IllegalArgumentException("Invalid descriptor");
        };

        for (int i = 0; i < arrayLength; i++)
            clazz = clazz.arrayType();

        return clazz;
    }

    private static final Pattern DESCRIPTOR_PATTERN = Pattern.compile("\\[*L[^;]+;|\\[[ZBCSIFDJ]|[ZBCSIFDJ]");

    /**
     * Splits method descriptor into its individual type descriptor parts.
     *
     * @param desc descriptor
     * @return parts
     */
    private static List<String> splitMethodDesc(String desc) {
        int beginIndex = desc.indexOf('(');
        int endIndex = desc.lastIndexOf(')');

        if ((beginIndex == -1 && endIndex != -1) || (beginIndex != -1 && endIndex == -1))
            throw new IllegalArgumentException("Invalid descriptor");

        String x0 = beginIndex == -1
                ? desc
                : desc.substring(beginIndex + 1, endIndex);

        Matcher matcher = DESCRIPTOR_PATTERN.matcher(x0);

        ArrayList<String> listMatches = new ArrayList<>();

        while(matcher.find()) listMatches.add(matcher.group());

        if (beginIndex != -1)
            listMatches.add(desc.substring(endIndex + 1));

        return listMatches;
    }

}
