package org.eclipse.jdt.ls.core.internal.plantuml.configuration;

/**
 * How a type name is rendered in UML.
 *
 * <dl>
 * <dt>{@code NONE}</dt><dd>Omit the type</dd>
 * <dt>{@code SIMPLE}</dt><dd>Use the simple type name (without its containing package)</dd>
 * <dt>{@code QUALIFIED}</dt><dd>Use the qualified type name</dd>
 * <dt>{@code QUALIFIED_GENERICS}</dt><dd>Use the qualified type name, also for its generic types</dd>
 * </dl>
 *
 * @author Sjoerd Talsma
 */
public enum TypeDisplay {
    /**
     * Omit the type name.
     */
    NONE,

    /**
     * Use the simple type name without the containing package.
     */
    SIMPLE,

    /**
     * Use the qualified type name.
     */
    QUALIFIED,

    /**
     * Use the qualified type name, also for its generic type variables.
     */
    QUALIFIED_GENERICS
}
