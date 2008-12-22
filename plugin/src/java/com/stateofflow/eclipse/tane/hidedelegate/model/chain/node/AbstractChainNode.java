package com.stateofflow.eclipse.tane.hidedelegate.model.chain.node;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

abstract class AbstractChainNode implements ChainNode {
    public final void copyExpression(final MethodInvocation destination) {
        if (getLeftHandSide() != null) {
            destination.setExpression((Expression) ASTNode.copySubtree(destination.getAST(), getLeftHandSide()));
        }
    }

    protected abstract Expression getLeftHandSide();

    protected abstract IBinding getDeclarationOfMember();

    public final IMember getJavaElementOfMember() {
        return (IMember) getDeclarationOfMember().getJavaElement();
    }

    public final int getModifiersForMember() {
        return getDeclarationOfMember().getModifiers();
    }

    public ICompilationUnit getCompilationUnit() {
        return getDeclaringTypeOfMember().getCompilationUnit();
    }

    public IType getDeclaringTypeOfMember() {
        return (IType) getDeclaringClassOfMember().getJavaElement();
    }

    protected String describe() {
        return getDeclaringTypeOfMember().getFullyQualifiedName() + "." + getDeclarationOfMember().getName();
    }
}
