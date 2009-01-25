package com.stateofflow.eclipse.tane.hidedelegate.model.chain.node;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;

class FieldAccessNode extends AbstractChainNode {
	private final FieldAccess node;

	public FieldAccessNode(final FieldAccess node) {
		this.node = node;
	}

	public void copyInvocationArguments(final MethodInvocation replacement) {
	}

	public void copyParameters(final MethodDeclaration newMethodDeclaration, final Rewrite rewrite) {
	}

	public IBinding getDeclaringClassOfMember() {
		return getDeclarationOfMember().getDeclaringClass();
	}

	public ITypeBinding[] getExceptionTypes() {
		return new ITypeBinding[0];
	}

	@Override
	protected Expression getLeftHandSide() {
		return node.getExpression();
	}

	public ITypeBinding getTypeOfMember() {
		return node.resolveTypeBinding();
	}

	public boolean matches(final ASTNode astNode) {
		return astNode.getNodeType() == ASTNode.FIELD_ACCESS
				&& getDeclarationOfMember().isEqualTo(((FieldAccess) astNode).resolveFieldBinding());
	}

	@Override
	protected IVariableBinding getDeclarationOfMember() {
		return node.resolveFieldBinding();
	}
	
	public String getSuggestedMethodName() {
		String name = getDeclarationOfMember().getName();
		return (isPrimitiveBoolean() ? "is" : "get") + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private boolean isPrimitiveBoolean() {
		return isMemberOfWellKnownType("boolean");
	}

	private boolean isMemberOfWellKnownType(String typeName) {
		return node.getAST().resolveWellKnownType(typeName).equals(getTypeOfMember());
	}
}
