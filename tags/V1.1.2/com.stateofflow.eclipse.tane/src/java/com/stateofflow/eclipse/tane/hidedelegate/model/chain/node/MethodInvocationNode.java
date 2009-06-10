package com.stateofflow.eclipse.tane.hidedelegate.model.chain.node;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;

class MethodInvocationNode extends AbstractChainNode {
	private final MethodInvocation node;

	public MethodInvocationNode(final MethodInvocation node) {
		this.node = node;
	}

	@SuppressWarnings("unchecked")
	public void copyInvocationArguments(final MethodInvocation destination) {
		final Iterator<Expression> iter = node.arguments().iterator();
		while (iter.hasNext()) {
			destination.arguments().add(ASTNode.copySubtree(destination.getAST(), iter.next()));
		}
	}

	private SingleVariableDeclaration copyParameter(final MethodDeclaration destination, final Rewrite rewrite, final ITypeBinding type) {
		final AST ast = destination.getAST();
		final SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
		parameter.setType(rewrite.addImportReturningType(type));
		parameter.setName(ast.newSimpleName("p" + destination.parameters().size()));
		return parameter;
	}

	@SuppressWarnings("unchecked")
	public void copyParameters(final MethodDeclaration newMethodDeclaration, final Rewrite rewrite) {
		for (final ITypeBinding parameterType : getDeclarationOfMember().getParameterTypes()) {
			newMethodDeclaration.parameters().add(copyParameter(newMethodDeclaration, rewrite, parameterType));
		}
	}

	public IBinding getDeclaringClassOfMember() {
		return getDeclarationOfMember().getDeclaringClass();
	}

	public ITypeBinding[] getExceptionTypes() {
		return getDeclarationOfMember().getExceptionTypes();
	}

	@Override
	protected Expression getLeftHandSide() {
		return node.getExpression();
	}

	public ITypeBinding getTypeOfMember() {
		return node.resolveTypeBinding();
	}

	@Override
	protected IMethodBinding getDeclarationOfMember() {
		return node.resolveMethodBinding();
	}

	public boolean matches(final ASTNode astNode) {
		return astNode.getNodeType() == ASTNode.METHOD_INVOCATION
				&& getDeclarationOfMember().isEqualTo(((MethodInvocation) astNode).resolveMethodBinding());
	}
	
    public String getSuggestedMethodName() {
		return getDeclarationOfMember().getName();
	}
}
