package org.eclipse.jdt.ls.core.internal.plantuml.configuration;

/**
 * Configuration for UML method rendering.
 *
 * @author Sjoerd Talsma
 */
public interface MethodConfig {
    /**
     * How method parameters are rendered.
     *
     * <p>
     * {@code NONE} will omit the name for the parameter names,
     * {@code BEFORE_TYPE} will render the name first, followed by the type, while
     * {@code AFTER_TYPE} renders the type first followed by the parameter name (java-style).
     */
    enum ParamNames {
        /**
         * Omit parameter names from methods altogether.
         */
        NONE,

        /**
         * Render the method parameter name first before its type.
         */
        BEFORE_TYPE,

        /**
         * Render the method parameter type first, followed by its name.
         */
        AFTER_TYPE
    }

    /**
     * How method parameter names are rendered.
     *
     * @return How method parameter names are rendered.
     */
    ParamNames paramNames();

    /**
     * How parameter types are rendered.
     *
     * @return How parameter types are rendered.
     */
    TypeDisplay paramTypes();

    /**
     * How method return types are rendered.
     *
     * @return How method return types are rendered.
     */
    TypeDisplay returnType();

    /**
     * Whether a method with given visibility must be included in the UML diagram.
     *
     * @param methodVisibility The method visibility.
     * @return {@code true} if the method must be included in the UML diagram
     * based on its visibility, or {@code false} if it must be omitted.
     */
    boolean include(Visibility methodVisibility);

    /**
     * Whether JavaBean property accessor methods
     * such as {@code getXyz()}, {@code isXyz()}, {@code setXyz(Xyz xyz)}
     * should be rendered as Fields in UML.
     *
     * @return {@code true} if JavaBean accessor methods should be rendered as Fields
     * in the UML diagram, {@code false} to render them as normal methods.
     */
    boolean javaBeanPropertiesAsFields();
}
