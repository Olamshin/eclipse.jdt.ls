package org.eclipse.jdt.ls.core.internal.plantuml.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.ls.core.internal.plantuml.PlantUmlConfig;
import org.eclipse.jdt.ls.core.internal.plantuml.configuration.Visibility;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.ClassDiagram;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Field;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Method;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Namespace;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Parameters;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.Type;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.TypeName;

public class PlantUMLGenerator extends ASTVisitor {
    private StringBuilder diagram;
    private String currentClass;
    private ClassDiagram classDiagram;

    public ClassDiagram getClassDiagram() {
        return classDiagram;
    }

    public PlantUMLGenerator() {
        diagram = new StringBuilder();
        currentClass = null;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        Type convertToUMLType = convertToUMLType(node);
        classDiagram = new ClassDiagram(new PlantUmlConfig(), convertToUMLType);
        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        diagram.append("}\n");
        currentClass = null;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        // Extract field information and append to the diagram
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // Extract method information and append to the diagram  
        return true;
    }

    // Implement other visit methods as needed

    public String getPlantUMLDiagram() {
        return "@startuml\n" + diagram.toString() + "@enduml";
    }

    private Type convertToUMLType(TypeDeclaration typeDeclaration) {
        // Create a new Type object
        Type type = new Type(getPackage(typeDeclaration), getClassification(typeDeclaration), getTypeName(typeDeclaration));

        // Set the visibility based on the type's modifiers
        // type.setVisibility(getVisibility(typeDeclaration.getModifiers()));

        // Add fields to the type
        for (FieldDeclaration fieldDeclaration : typeDeclaration.getFields()) {
            Field field = convertToUMLField(type, fieldDeclaration);
            type.addChild(field);
        }

        // Add methods to the type
        for (MethodDeclaration methodDeclaration : typeDeclaration.getMethods()) {
            Method method = convertToUMLMethod(type, methodDeclaration);
            type.addChild(method);
        }

        return type;
    }

    private Namespace getPackage(TypeDeclaration typeDeclaration) {
        CompilationUnit compilationUnit = (CompilationUnit) typeDeclaration.getRoot();

        // Get the package name from the CompilationUnit
        PackageDeclaration packageDeclaration = compilationUnit.getPackage();
        if (packageDeclaration != null) {
            String packageName = packageDeclaration.getName().getFullyQualifiedName();
            return new Namespace(null, packageName, null);
        } else {
            return null;
        }
    }

    private Type.Classification getClassification(TypeDeclaration typeDeclaration) {
        if (typeDeclaration.isInterface()) {
            return Type.Classification.INTERFACE;
        } else if (Modifier.isAbstract(typeDeclaration.getModifiers())) {
            return Type.Classification.ABSTRACT_CLASS;
        } else {
            return Type.Classification.CLASS;
        }
    }

    private TypeName getTypeName(TypeDeclaration typeDeclaration) {
        String name = typeDeclaration.getName().getIdentifier();
        // TODO: Handle generic types and type parameters
        return new TypeName(null, name, name);
    }

    private Visibility getVisibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return Visibility.PUBLIC;
        } else if (Modifier.isProtected(modifiers)) {
            return Visibility.PROTECTED;
        } else if (Modifier.isPrivate(modifiers)) {
            return Visibility.PRIVATE;
        } else {
            return Visibility.PACKAGE_PRIVATE;
        }
    }

    private Field convertToUMLField(Type containingType, FieldDeclaration fieldDeclaration) {
        // TODO: Implement field conversion logic
        // Extract field name, type, and modifiers from the FieldDeclaration
        // Create a new Field object and set its properties
        // Return the Field object
        // Extract field modifiers
        int modifiers = fieldDeclaration.getModifiers();

        // Extract field type
        TypeName fieldType = getTypeName(fieldDeclaration.getType());

        // Create a new Field object for each variable declaration fragment
        List<Field> fields = new ArrayList<>();
        for (Object fragmentObject : fieldDeclaration.fragments()) {
            if (fragmentObject instanceof VariableDeclarationFragment) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragmentObject;
                String fieldName = fragment.getName().getIdentifier();

                Field field = new Field(containingType, fieldName, fieldType);
                field.setVisibility(getVisibility(modifiers));
                field.isStatic = Modifier.isStatic(modifiers);
                // field.isFinal = Modifier.isFinal(modifiers);
                // field.isTransient = Modifier.isTransient(modifiers);
                // field.isVolatile = Modifier.isVolatile(modifiers);

                fields.add(field);
            }
        }

        // Return the first field (assuming there is only one field per declaration)
        return fields.isEmpty() ? null : fields.get(0);
    }

    private Method convertToUMLMethod(Type containingType, MethodDeclaration methodDeclaration) {
        // TODO: Implement method conversion logic
        // Extract method name, return type, parameters, and modifiers from the MethodDeclaration
        // Create a new Method object and set its properties
        // Return the Method object
        // Extract method name
        String methodName = methodDeclaration.getName().getIdentifier();

        // Extract return type
        TypeName returnType = getTypeName(methodDeclaration.getReturnType2());

        // Create a new Method object
        Method method = new Method(containingType, methodName, returnType);

        // Set the visibility based on the method's modifiers
        method.setVisibility(getVisibility(methodDeclaration.getModifiers()));

        // Set the method as abstract if it has the abstract modifier
        method.isAbstract = Modifier.isAbstract(methodDeclaration.getModifiers());

        // Set the method as static if it has the static modifier
        method.isStatic = Modifier.isStatic(methodDeclaration.getModifiers());

        // Add parameters to the method
        for (Object parameterObject : methodDeclaration.parameters()) {
            if (parameterObject instanceof SingleVariableDeclaration) {
                SingleVariableDeclaration parameterDeclaration = (SingleVariableDeclaration) parameterObject;
                String parameterName = parameterDeclaration.getName().getIdentifier();
                TypeName parameterType = getTypeName(parameterDeclaration.getType());
                method.addChild(new Parameters(null).add(parameterName, parameterType));
            }
        }

        return method;
    }

    private TypeName getTypeName(org.eclipse.jdt.core.dom.Type type) {
        return new TypeNameVisitor().visit(type);
    }

}
