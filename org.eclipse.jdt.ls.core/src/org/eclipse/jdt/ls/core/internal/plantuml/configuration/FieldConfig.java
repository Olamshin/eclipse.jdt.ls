package org.eclipse.jdt.ls.core.internal.plantuml.configuration;

/**
 * Configuration how Fields are rendered in the UML.
 *
 * @author Sjoerd Talsma
 */
public interface FieldConfig {

    /**
     * Set how field types are rendered in the UML diagram.
     *
     * @return How field types are rendered.
     */
    TypeDisplay typeDisplay();

    /**
     * Whether to include fields with the specified {@link Visibility} in the UML diagram.
     *
     * @param fieldVisibility The visibility of the evaluated field.
     * @return {@code true} if fields with the requested visibility must be included in the UML diagram
     * or {@code false} otherwise.
     */
    boolean include(Visibility fieldVisibility);

}
