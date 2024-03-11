package org.eclipse.jdt.ls.core.internal.plantuml.uml;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.eclipse.jdt.ls.core.internal.plantuml.rendering.indent.IndentingPrintWriter;

/**
 * UML namespace
 * <p>
 * This corresponds to a 'package' in the Java world.
 *
 * @author Sjoerd Talsma
 */
public class Namespace extends UMLNode {

    private final String moduleName;
    public final String name;

    public Namespace(UMLNode parent, String name, String moduleName) {
        super(parent);
        this.name = requireNonNull(name, "Package name is <null>.").trim();
        this.moduleName = moduleName;
    }

    public Optional<String> getModuleName() {
        return Optional.ofNullable(moduleName);
    }

    /**
     * Adds the package name to the diagram.
     * Re: bug 107: If the package name is empty (i.e. the 'default' package),
     * render {@code "unnamed"} because an empty name is not valid in PlantUML.
     *
     * @param output The output to append the package name to.
     * @param <IPW>  The type of the output object.
     * @return The same output instance for method chaining.
     */
    private <IPW extends IndentingPrintWriter> IPW writeNameTo(IPW output) {
        output.append(name.isEmpty() ? "unnamed" : name).whitespace();
        return output;
    }

    @Override
    public <IPW extends IndentingPrintWriter> IPW writeTo(IPW output) {
        writeNameTo(output.append("package").whitespace()).append('{').newline();
        writeChildrenTo(output.indent());
        output.append('}').newline();
        return output;
    }

    public boolean contains(TypeName typeName) {
        return typeName != null && typeName.qualified.startsWith(this.name + ".");
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Namespace && name.equals(((Namespace) other).name));
    }
}
