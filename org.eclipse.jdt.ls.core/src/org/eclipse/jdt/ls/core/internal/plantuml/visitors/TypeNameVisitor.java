package org.eclipse.jdt.ls.core.internal.plantuml.visitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.ls.core.internal.plantuml.uml.TypeName;

public class TypeNameVisitor extends ASTVisitor {
    TypeName finalTypeName;

    public TypeNameVisitor() {
    }

    public TypeName visit(Type type) {
        if (type == null) {
            return null;
        }
        type.accept(this);
        return finalTypeName;
    }

    @Override
    public boolean visit(PrimitiveType primitiveType) {
        finalTypeName = new TypeName(null, null, primitiveType.getPrimitiveTypeCode().toString());
        return false;
    }

    @Override
    public boolean visit(SimpleType simpleType) {
        String qualifiedName = simpleType.getName().getFullyQualifiedName();
        int lastDot = qualifiedName.lastIndexOf('.');
        String packageName = lastDot > 0 ? qualifiedName.substring(0, lastDot) : null;
        String simpleName = lastDot > 0 ? qualifiedName.substring(lastDot + 1) : qualifiedName;
        finalTypeName = new TypeName(packageName, simpleName, qualifiedName);
        return false;
    }

    @Override
    public boolean visit(ParameterizedType parameterizedType) {
        Type baseType = parameterizedType.getType();
        TypeName baseTypeName = visit(baseType);

        List<Type> typeArguments = parameterizedType.typeArguments();
        TypeName[] typeArgumentNames = typeArguments.stream().map(this::visit).toArray(TypeName[]::new);

        finalTypeName = new TypeName(baseTypeName.packagename, baseTypeName.simple, baseTypeName.qualified, typeArgumentNames);

        return false;
    }

    @Override
    public boolean visit(ArrayType arrayType) {
        TypeName componentType = visit(arrayType.getElementType());
        finalTypeName = TypeName.Array.of(componentType);
        return false;
    }

    @Override
    public boolean visit(WildcardType wildcardType) {
        if (wildcardType.getBound() != null) {
            TypeName boundType = visit(wildcardType.getBound());
            if (wildcardType.isUpperBound()) {
                finalTypeName = TypeName.Variable.extendsBound("?", boundType);
            } else {
                finalTypeName = TypeName.Variable.superBound("?", boundType);
            }
        }
        finalTypeName = new TypeName(null, "?", "?");
        return false;
    }
}