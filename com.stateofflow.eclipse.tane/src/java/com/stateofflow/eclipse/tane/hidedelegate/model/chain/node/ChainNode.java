package com.stateofflow.eclipse.tane.hidedelegate.model.chain.node;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.Rewrite;

public interface ChainNode {
    void copyInvocationArguments(MethodInvocation replacement);

    void copyParameters(final MethodDeclaration newMethodDeclaration, final Rewrite rewrite);

    IBinding getDeclaringClassOfMember();

    IMember getJavaElementOfMember();

    ITypeBinding[] getExceptionTypes();

    int getModifiersForMember();

    ITypeBinding getTypeOfMember();

    boolean matches(ASTNode node);

    void copyExpression(MethodInvocation replacement);

    ICompilationUnit getCompilationUnit();

    IType getDeclaringTypeOfMember();
}
