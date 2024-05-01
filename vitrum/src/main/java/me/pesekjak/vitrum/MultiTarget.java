package me.pesekjak.vitrum;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used to define multiple targets.
 * <p>
 * Use repeatedly {@link Target} instead.
 */
@ApiStatus.Internal
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface MultiTarget {

    /**
     * Targets.
     *
     * @return targets
     */
    Target[] value();

}
