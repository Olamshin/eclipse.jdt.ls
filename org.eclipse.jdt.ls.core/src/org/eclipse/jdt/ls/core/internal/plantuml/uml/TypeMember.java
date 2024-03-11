package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;

/**
 * Model object for a Field or Method in a UML class.
 *
 * @author Sjoerd Talsma
 */
public abstract class TypeMember extends UMLNode {

    public final String name;
    public TypeName type;
    private Visibility visibility;
    public boolean isStatic;
    public boolean isDeprecated;

    protected TypeMember(Type containingType, String name, TypeName type) {
        super(containingType);
        this.name = requireNonNull(name, "Member name is <null>.").trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("Member name is empty.");
        this.type = type;
    }

    public Visibility getVisibility() {
        return visibility == null ? Visibility.PUBLIC : visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        if (type != null) {
            output.append(": ").append(type.toString());
        }
        return output;
    }

    void replaceParameterizedType(TypeName from, TypeName to) {
        if (from != null && from.equals(this.type)) {
            this.type = to;
        }
    }

    protected <IPW extends IndentingPrintWriter> IPW writeParametersTo(IPW output) {
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (isStatic) output.append("{static}").whitespace();
        output.append(umlVisibility());
        if (isDeprecated) output.append("--").append(name).append("--");
        else output.append(name);
        writeParametersTo(output);
        writeTypeTo(output);
        output.newline();
        return output;
    }

    private String umlVisibility() {
        switch (getVisibility()) {
            case PRIVATE:
                return "-";
            case PROTECTED:
                return "#";
            case PACKAGE_PRIVATE:
                return "~";
            default: // assume PUBLIC
                return "+";
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), name);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null && getClass().equals(other.getClass())
                && Objects.equals(getParent(), ((TypeMember) other).getParent())
                && name.equals(((TypeMember) other).name)
        );
    }

}