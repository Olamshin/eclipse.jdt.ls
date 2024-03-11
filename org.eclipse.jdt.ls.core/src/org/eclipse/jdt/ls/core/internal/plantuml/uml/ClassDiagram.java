package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import java.io.File;

import org.eclipse.jdt.ls.core.internal.plantuml.Configuration;
import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;

/**
 * UML diagram for a single class.
 */
public class ClassDiagram extends Diagram {

    private File pumlFile = null;

    public ClassDiagram(Configuration config, Type type) {
        super(config);
        addChild(type);
    }

    public Type getType() {
        return getChildren().stream()
                .filter(Type.class::isInstance).map(Type.class::cast)
                .findFirst().orElseThrow(() -> new IllegalStateException("No Type defined in Class diagram!"));
    }

    @Override
    public void addChild(UMLNode child) {
        super.addChild(child);
        if (child instanceof Type) ((Type) child).setIncludePackagename(true);
    }

    @Override
    protected <IPW extends IndentingPrintWriter> IPW writeChildrenTo(IPW output) {
        output.append("set namespaceSeparator none").newline()
                .append("hide empty fields").newline()
                .append("hide empty methods").newline()
                .newline();
        return super.writeChildrenTo(output);
    }

    @Override
    protected File getPlantUmlFile() {
        if (pumlFile == null) {
            final Type type = getType();
            StringBuilder result = new StringBuilder(getConfiguration().destinationDirectory());
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/') result.append('/');
            type.getModulename().ifPresent(modulename -> result.append(modulename).append('/'));
            String containingPackage = type.getPackagename();
            result.append(containingPackage.replace('.', '/')).append('/');
            if (type.getName().qualified.startsWith(containingPackage + ".")) {
                result.append(type.getName().qualified.substring(containingPackage.length() + 1));
            } else {
                result.append(type.getName().simple);
            }
            pumlFile = new File(result.append(".puml").toString());
        }
        return pumlFile;
    }

}
