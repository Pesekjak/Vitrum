package me.pesekjak.vitrum;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.ClassWriter.*;

/**
 * Core class of the Vitrum library.
 * <p>
 * Use {@link #createWindow(Class, String)} to create new window implementation.
 * <p>
 * The provided class needs to be an interface with methods annotated using {@link Target}.
 */
public final class Vitrum {

    /**
     * Creates new window implementation for given interface and handler.
     *
     * @param clazz interface class
     * @param handler handler identifier
     * @return window implementation
     * @param <T> window type
     * @throws IllegalAccessException if the class can not be defined
     * @since 1.0.0
     */
    public static <T> T createWindow(Class<T> clazz, String handler) throws IllegalAccessException {
        if (!clazz.isInterface()) throw new IllegalArgumentException();

        T instance;
        if ((instance = returnIfAvailable(clazz, handler)) != null) return instance;

        Type type = getTypeForHandler(clazz, handler);

        ClassWriter writer = new ClassWriter(ASM9 | COMPUTE_FRAMES | COMPUTE_MAXS);
        writer.visit(
                V16, ACC_PUBLIC | ACC_SUPER,
                type.getInternalName(),
                null,
                Type.getType(Object.class).getInternalName(),
                new String[] {Type.getType(clazz).getInternalName()}
        );
        writeConstructor(writer);
        for (Method method : clazz.getMethods())
            writeMethod(writer, method, handler);
        writer.visitEnd();

        MethodHandles.privateLookupIn(Vitrum.class, MethodHandles.lookup()).defineClass(writer.toByteArray());

        instance = returnIfAvailable(clazz, handler);

        return Objects.requireNonNull(instance, "Failed to create a new instance of %s".formatted(type.getInternalName()));
    }

    private Vitrum() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns dot path for given window implementation.
     *
     * @param clazz interface class
     * @param handler handler identifier
     * @return dot path
     */
    private static String getDotPath(Class<?> clazz, String handler) {
        String hash = clazz.hashCode() + "" + handler.hashCode();
        return Vitrum.class.getName() + "_" + hash;
    }

    /**
     * Returns type for given window implementation.
     *
     * @param clazz interface class
     * @param handler handler identifier
     * @return type
     */
    private static Type getTypeForHandler(Class<?> clazz, String handler) {
        return ASMUtil.getType(getDotPath(clazz, handler));
    }

    /**
     * Returns new instance of the window implementation
     * if its class exists, else null.
     *
     * @param clazz interface class
     * @param handler handler identifier
     * @return new window instance
     * @param <T> window type
     */
    private static <T> @Nullable T returnIfAvailable(Class<T> clazz, String handler) {
        try {
            Class<?> found = Class.forName(getDotPath(clazz, handler), false, Vitrum.class.getClassLoader());
            return clazz.cast(found.getConstructor().newInstance());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    /**
     * Writes default constructor into the class writer.
     *
     * @param writer writer
     */
    private static void writeConstructor(ClassWriter writer) {
        MethodVisitor visitor = writer.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );
        visitor.visitCode();
        visitor.visitVarInsn(ALOAD, 0);
        visitor.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(Object.class),
                "<init>",
                "()V",
                false
        );
        visitor.visitInsn(RETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    /**
     * Implements method to the window writer.
     *
     * @param writer writer
     * @param method method to implement
     * @param handler handler identifier for the window implementation
     */
    private static void writeMethod(ClassWriter writer, Method method, String handler) {
        if (!Modifier.isAbstract(method.getModifiers())) return;

        Class<?>[] params = method.getParameterTypes();
        if (params.length != 2 || params[0] != Object.class || params[1] != Object[].class)
            throw new RuntimeException("Illegal method " + method.getName() + ", does not follow the 'Object, Object[]' arguments");

        // method of the window interface
        Type callingMethod = Type.getType(method);

        MethodVisitor visitor = writer.visitMethod(
                ACC_PUBLIC,
                method.getName(),
                callingMethod.getDescriptor(),
                null,
                null
        );

        Target target = getTarget(method, handler);

        if (target == null) {
            visitor.visitTypeInsn(NEW, Type.getInternalName(UnsupportedOperationException.class));
            visitor.visitInsn(DUP);
            visitor.visitMethodInsn(
                    INVOKESPECIAL,
                    Type.getInternalName(UnsupportedOperationException.class),
                    "<init>",
                    "()V",
                    false
            );
            visitor.visitInsn(ATHROW);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
            return;
        }

        if (target.isStatic() && target.action() == Target.Action.CALL_CONSTRUCTOR)
            throw new RuntimeException("Constructor in method " + method.getName() + " defined as static for " + handler + " handler");

        if (!target.reflective()) {
            writeNonReflectiveCode(target, visitor);
        } else {
            writeReflectiveCode(target, visitor);
        }

        Type targetType = target.action() == Target.Action.CALL_METHOD
                ? Type.getMethodType(target.descriptor())
                : Type.getType(target.descriptor());

        Type outputType = target.action() == Target.Action.CALL_METHOD
                ? targetType.getReturnType()
                : targetType;

        // constructor returns void, but on stack will be new instance
        if (target.action() == Target.Action.CALL_CONSTRUCTOR)
            outputType = Type.getType("L" + target.source() + ";");

        // Changes return types to what ReflectionCaller util returns
        if (target.reflective())
            outputType = target.action() != Target.Action.SET_FIELD
                    ? Type.getType(Object.class)
                    : Type.VOID_TYPE;

        if (callingMethod.getReturnType() == Type.VOID_TYPE) {
            visitor.visitInsn(RETURN);
        } else {

            if (outputType.equals(Type.VOID_TYPE) || target.action() == Target.Action.SET_FIELD)
                visitor.visitInsn(ACONST_NULL);

            if (target.action() != Target.Action.SET_FIELD
                    && ASMUtil.isPrimitive(outputType)
                    && !ASMUtil.isArray(outputType)
                    && !outputType.equals(Type.VOID_TYPE))
                ConverterVisitor.convertTopPrimitiveToObject(visitor, outputType);

            ConverterVisitor.convertTopObject(visitor, callingMethod.getReturnType());
            visitor.visitInsn(callingMethod.getReturnType().getOpcode(IRETURN));
        }

        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    /**
     * Writes the logic for the window methods that do not use
     * reflective calls.
     *
     * @param target target instance
     * @param visitor visitor
     */
    private static void writeNonReflectiveCode(Target target, MethodVisitor visitor) {
        Type targetType = target.action() == Target.Action.CALL_METHOD
                ? Type.getMethodType(target.descriptor())
                : Type.getType(target.descriptor());

        if (!target.isStatic() && target.action() != Target.Action.CALL_CONSTRUCTOR) {
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitTypeInsn(CHECKCAST, target.source());
        }

        switch (target.action()) {
            case CALL_METHOD -> {
                loadMethodArguments(visitor, targetType);
                visitor.visitMethodInsn(
                        target.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL,
                        target.source(),
                        target.name(),
                        target.descriptor(),
                        false
                );
            }
            case CALL_CONSTRUCTOR -> {
                visitor.visitTypeInsn(NEW, target.source());
                visitor.visitInsn(DUP);
                loadMethodArguments(visitor, targetType);
                visitor.visitMethodInsn(
                        INVOKESPECIAL,
                        target.source(),
                        "<init>",
                        target.descriptor(),
                        false
                );
            }
            case GET_FIELD -> visitor.visitFieldInsn(
                    target.isStatic() ? GETSTATIC : GETFIELD,
                    target.source(),
                    target.name(),
                    target.descriptor()
            );
            case SET_FIELD -> {
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitLdcInsn(0);
                visitor.visitInsn(AALOAD);
                ConverterVisitor.convertTopObject(visitor, targetType);
                visitor.visitFieldInsn(
                        target.isStatic() ? PUTSTATIC : PUTFIELD,
                        target.source(),
                        target.name(),
                        target.descriptor()
                );
            }
        }
    }

    /**
     * Loads arguments from a method type to the stack.
     *
     * @param visitor visitor
     * @param targetType method type
     */
    private static void loadMethodArguments(MethodVisitor visitor, Type targetType) {
        int index = 0;
        for (Type param : targetType.getArgumentTypes()) {
            visitor.visitVarInsn(ALOAD, 2);
            visitor.visitLdcInsn(index);
            visitor.visitInsn(AALOAD);
            ConverterVisitor.convertTopObject(visitor, param);
            index++;
        }
    }

    /**
     * Writes the logic for the window methods that do use
     * reflective calls.
     *
     * @param target target instance
     * @param visitor visitor
     */
    private static void writeReflectiveCode(Target target, MethodVisitor visitor) {
        switch (target.action()) {
            case CALL_METHOD -> {
                visitor.visitLdcInsn(target.source());
                visitor.visitLdcInsn(target.name());
                visitor.visitLdcInsn(target.descriptor());
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(
                        INVOKESTATIC,
                        Type.getInternalName(ReflectionCaller.class),
                        "callMethod",
                        Type.getMethodDescriptor(
                                Type.getType(Object.class),
                                Type.getType(String.class),
                                Type.getType(String.class),
                                Type.getType(String.class),
                                Type.getType(Object.class),
                                Type.getType(Object[].class)
                        ),
                        false
                );
            }
            case CALL_CONSTRUCTOR -> {
                visitor.visitLdcInsn(target.source());
                visitor.visitLdcInsn(target.descriptor());
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(
                        INVOKESTATIC,
                        Type.getInternalName(ReflectionCaller.class),
                        "callConstructor",
                        Type.getMethodDescriptor(
                                Type.getType(Object.class),
                                Type.getType(String.class),
                                Type.getType(String.class),
                                Type.getType(Object[].class)
                        ),
                        false
                );
            }
            case GET_FIELD -> {
                visitor.visitLdcInsn(target.source());
                visitor.visitLdcInsn(target.name());
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitMethodInsn(
                        INVOKESTATIC,
                        Type.getInternalName(ReflectionCaller.class),
                        "getField",
                        Type.getMethodDescriptor(
                                Type.getType(Object.class),
                                Type.getType(String.class),
                                Type.getType(String.class),
                                Type.getType(Object.class)
                        ),
                        false
                );
            }
            case SET_FIELD -> {
                visitor.visitLdcInsn(target.source());
                visitor.visitLdcInsn(target.name());
                visitor.visitVarInsn(ALOAD, 1);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitLdcInsn(0);
                visitor.visitInsn(AALOAD);
                visitor.visitMethodInsn(
                        INVOKESTATIC,
                        Type.getInternalName(ReflectionCaller.class),
                        "setField",
                        Type.getMethodDescriptor(
                                Type.VOID_TYPE,
                                Type.getType(String.class),
                                Type.getType(String.class),
                                Type.getType(Object.class),
                                Type.getType(Object.class)
                        ),
                        false
                );
            }
        }
    }

    /**
     * Returns target annotation for given method and with given handler identifier.
     *
     * @param method method
     * @param handler handler identifier
     * @return target annotation
     */
    private static @Nullable Target getTarget(Method method, String handler) {
        Target single = method.getAnnotation(Target.class);
        if (single != null) return getTarget(new Target[] {single}, handler);

        MultiTarget multi = method.getAnnotation(MultiTarget.class);
        if (multi == null) return null;
        return getTarget(multi.value(), handler);
    }

    /**
     * Returns target annotation with given handler identifier from the target array.
     *
     * @param targets target array
     * @param handler handler identifier
     * @return target annotation
     */
    private static @Nullable Target getTarget(Target[] targets, String handler) {
        for (Target target : targets)
            if (target.handler().equals(handler)) return target;
        return null;
    }

}
