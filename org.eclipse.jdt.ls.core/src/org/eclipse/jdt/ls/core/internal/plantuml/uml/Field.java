package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Type.Classification;

/**
 * Model object for a Field in an UML class.
 *
 * @author Sjoerd Talsma
 */
public class Field extends TypeMember {

    public Field(Type containingType, String name, TypeName type) {
        super(containingType, name, type);
    }

    private boolean isEnumType() {
        return isStatic
                && getParent() instanceof Type
                && Classification.ENUM.equals(((Type) getParent()).getClassfication())
                && ((Type) getParent()).getName().equals(type);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        if (!getConfiguration().fields().include(getVisibility())) return output;
        return super.writeTo(output);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeTypeTo(IPW output) {
        return isEnumType() ? output : super.writeTypeTo(output);
    }

}
