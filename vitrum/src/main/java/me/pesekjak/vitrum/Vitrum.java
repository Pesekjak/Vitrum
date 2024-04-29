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

        MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).defineClass(writer.toByteArray());

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
            throw new RuntimeException("Illegal method");

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

        // targeted method
        Type calledMethod = Type.getMethodType(target.descriptor());

        if (!target.isStatic()) {
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitTypeInsn(CHECKCAST, target.source());
        }

        int index = 0;
        for (Type param : calledMethod.getArgumentTypes()) {
            visitor.visitVarInsn(ALOAD, 2);
            visitor.visitLdcInsn(index);
            visitor.visitInsn(AALOAD);
            ConverterVisitor.convertTopObject(visitor, param);
            index++;
        }

        visitor.visitMethodInsn(
                target.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL,
                target.source(),
                target.name(),
                target.descriptor(),
                false
        );

        if (callingMethod.getReturnType() == Type.VOID_TYPE) {
            visitor.visitInsn(RETURN);
        } else {

            if (calledMethod.getReturnType().equals(Type.VOID_TYPE))
                visitor.visitInsn(ACONST_NULL);

            if (ASMUtil.isPrimitive(calledMethod.getReturnType()) && !ASMUtil.isArray(calledMethod.getReturnType()))
                ConverterVisitor.convertTopPrimitiveToObject(visitor, calledMethod.getReturnType());

            ConverterVisitor.convertTopObject(visitor, callingMethod.getReturnType());
            visitor.visitInsn(callingMethod.getReturnType().getOpcode(IRETURN));
        }

        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
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
