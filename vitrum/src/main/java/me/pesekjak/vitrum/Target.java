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
     * @since 1.0.0
     */
    String handler();

    /**
     * Internal name of the owner class holding targeted method.
     * <p>
     * For example: {@code foo/hello/world/Bar}
     *
     * @return method owner class
     * @since 1.0.0
     */
    String source();

    /**
     * Name of the targeted method.
     *
     * @return method name
     * @since 1.0.0
     */
    String name();

    /**
     * Descriptor of the targeted method.
     * <p>
     * For example: {@code (ILjava/lang/Integer;D)F}
     *
     * @return descriptor of the method
     * @since 1.0.0
     */
    String descriptor();

    /**
     * Whether the method is static or not.
     * <p>
     * For static methods it is not necessary to provide the instance when calling the
     * window interface method.
     *
     * @return whether the targeted method is static
     * @since 1.0.0
     */
    boolean isStatic() default false;

    /**
     * Whether the handler should use reflection instead of casual way of accessing
     * the class members.
     * <p>
     * Compare to casual way, this allows to access private class members, but can
     * be slower.
     * <p>
     * If set to true, source class has to declare the specified class member.
     *
     * @return whether to use Java reflection API to access the class members
     */
    boolean reflective() default false;

    /**
     * Action performed by the window interface method.
     *
     * @return action
     * @since 1.1.0
     */
    Action action() default Action.CALL_METHOD;

    /**
     * Represents what action should happen for the Target.
     *
     * @since 1.1.0
     */
    enum Action {

        /**
         * Calls the method of specified class with given name and descriptor
         * and returns its value (or null if the method does not return any value).
         * <p>
         * Arguments given to the window interface method will be passed as arguments
         * to the target method.
         * <p>
         * If the method call is static, {@link #isStatic()} has to be set to true,
         * and then no instance is expected when calling the window interface method,
         * otherwise it is expected to be not null.
         */
        CALL_METHOD,

        /**
         * Calls the constructor of specified class with given descriptor
         * and returns new instance of the class.
         * <p>
         * Arguments given to the window interface method will be passed as arguments
         * to the target method.
         * <p>
         * Method name is not required and can be left empty,
         * but it is good practice to set it to {@code <init>}.
         * Instance is expected to be null and method call can not be static.
         */
        CALL_CONSTRUCTOR,

        /**
         * Returns value of a public field of specified class with given name and
         * descriptor.
         * <p>
         * Arguments given to the window interface method will be ignored.
         * <p>
         * If the field is static, {@link #isStatic()} has to be set to true,
         * and then no instance is expected when calling the window interface method,
         * otherwise it is expected to be not null.
         */
        GET_FIELD,

        /**
         * Changes value of a public field of specified class with given name and
         * descriptor.
         * <p>
         * First argument given to the window interface method will be used as new value.
         * <p>
         * If the field is static, {@link #isStatic()} has to be set to true,
         * and then no instance is expected when calling the window interface method,
         * otherwise it is expected to be not null.
         */
        SET_FIELD

    }

}
