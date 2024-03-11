package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import java.util.Objects;

import org.eclipse.jdt.ls.core.internal.plantuml.configuration.TypeDisplay;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;

/**
 * UML representation of a method.
 *
 * @author Sjoerd Talsma
 */
public class Method extends TypeMember {

    /**
     * If this method is an abstract method.
     */
    public boolean isAbstract;

    /**
     * Create a new method in the containing type with a specific name and return type.
     *
     * @param containingType The containing type the member is part of.
     * @param name           The name of the method.
     * @param returnType     The name of the return type.
     */
    public Method(Type containingType, String name, TypeName returnType) {
        super(containingType, name, returnType);
    }

    private Parameters getOrCreateParameters() {
        return getChildren().stream()
                .filter(Parameters.class::isInstance).map(Parameters.class::cast)
                .findFirst()
                .orElseGet(this::createAndAddNewParameters);
    }

    private Parameters createAndAddNewParameters() {
        Parameters parameters = new Parameters(this);
        this.addChild(parameters);
        return parameters;
    }

    /**
     * Add a parameter to this method.
     *
     * @param name The name of the parameter.
     * @param type The type of the parameter.
     */
    public void addParameter(String name, TypeName type) {
        getOrCreateParameters().add(name, type);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (!getConfiguration().methods().include(getVisibility())) return output;
        if (isAbstract) output.append("{abstract}").whitespace();
        return super.writeTo(output);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return getOrCreateParameters().writeTo(output);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        TypeDisplay returnTypeDisplay = getConfiguration().methods().returnType();
        if (type != null && !TypeDisplay.NONE.equals(returnTypeDisplay)) {
            output.append(": ").append(type.toUml(returnTypeDisplay, null));
        }
        return output;
    }

    @Override
    void replaceParameterizedType(TypeName from, TypeName to) {
        super.replaceParameterizedType(from, to);
        getOrCreateParameters().replaceParameterizedType(from, to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOrCreateParameters());
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other)
                && getOrCreateParameters().equals(((Method) other).getOrCreateParameters());
    }

}