package org.eclipse.jdt.ls.core.internal.plantuml.configuration;

/**
 * Visibility for classes, methods and fields.
 * <p>
 * See <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html">Access Control</a> description
 * in the official Oracle documentation.
 * In the UML Doclet, the visibility is used for two purposes:
 * <ol>
 * <li>To represent the visibility of classes, methods and fields in the internal model for rendered diagrams.
 * <li>To 'ask' the configuration whether a particular diagram should render classes, methods or fields with
 * a particular visibility.
 * </ol>
 *
 * @author Sjoerd Talsma
 */
public enum Visibility {
    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PRIVATE private} modifier.
     */
    PRIVATE,

    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PROTECTED protected} modifier.
     */
    PROTECTED,

    /**
     * The visibility corresponding with the Java default {@link java.lang.reflect.Modifier}.
     */
    PACKAGE_PRIVATE,

    /**
     * The visibility corresponding with the Java {@link java.lang.reflect.Modifier#PUBLIC public} modifier.
     */
    PUBLIC
}
