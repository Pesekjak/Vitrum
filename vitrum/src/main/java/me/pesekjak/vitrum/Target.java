package me.pesekjak.vitrum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies an implementation of window method.
 */
@Repeatable(MultiTarget.class)
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Target {

    /**
     * Identifier of the handler for this target.
     * <p>
     * Is usually version identifier.
     *
     * @return version identifier
     */
    String handler();

    /**
     * Internal name of the owner class holding targeted method.
     * <p>
     * For example: {@code foo/hello/world/Bar}
     *
     * @return method owner class
     */
    String source();

    /**
     * Name of the targeted method.
     *
     * @return method name
     */
    String name();

    /**
     * Descriptor of the targeted method.
     * <p>
     * For example: {@code (ILjava/lang/Integer;D)F}
     *
     * @return descriptor of the method
     */
    String descriptor();

    /**
     * Whether the method is static or not.
     * <p>
     * For static methods it is not necessary to provide the instance when calling the
     * window interface method.
     *
     * @return whether the targeted method is static
     */
    boolean isStatic();

}
