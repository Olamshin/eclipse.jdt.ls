package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import org.eclipse.jdt.ls.core.internal.plantuml.configuration.MethodConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.TypeDisplay;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;

/**
 * @author Sjoerd Talsma
 */
public class Parameters extends UMLNode {

    private boolean varargs = false;

    public Parameters(UMLNode parent) {
        super(parent);
    }

    @Override
    public void addChild(UMLNode child) {
        if (child instanceof Parameter) super.addChild(child);
    }

    public Parameters add(String name, TypeName type) {
        addChild(new Parameter(name, type));
        return this;
    }

    public Parameters varargs(boolean varargs) {
        this.varargs = varargs;
        return this;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        return writeChildrenTo(output);
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append('(');
        String sep = "";
        for (UMLNode param : getChildren()) {
            param.writeTo(output.append(sep));
            sep = ", ";
        }
        output.append(')');
        return output;
    }

    void replaceParameterizedType(TypeName from, TypeName to) {
        if (from != null) {
            getChildren().stream()
                    .filter(Parameter.class::isInstance).map(Parameter.class::cast)
                    .filter(p -> from.equals(p.type))
                    .forEach(p -> p.type = to);
        }
    }

    public class Parameter extends UMLNode {
        private final String name;
        private TypeName type;

        private Parameter(String name, TypeName type) {
            super(Parameters.this);
            this.name = name;
            this.type = type;
        }

        @Override
        public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
            String sep = "";
            MethodConfig methodConfig = getConfiguration().methods();
            if (name != null && MethodConfig.ParamNames.BEFORE_TYPE.equals(methodConfig.paramNames())) {
                output.append(name);
                sep = ": ";
            }
            if (type != null && !TypeDisplay.NONE.equals(methodConfig.paramTypes())) {
                String typeUml = type.toUml(methodConfig.paramTypes(), null);
                if (varargs && typeUml.endsWith("[]")) typeUml = typeUml.substring(0, typeUml.length() - 2) + "...";
                output.append(sep).append(typeUml);
                sep = " ";
            }
            if (name != null && MethodConfig.ParamNames.AFTER_TYPE.equals(methodConfig.paramNames())) {
                output.append(sep).append(name);
            }
            return output;
        }
    }
}
