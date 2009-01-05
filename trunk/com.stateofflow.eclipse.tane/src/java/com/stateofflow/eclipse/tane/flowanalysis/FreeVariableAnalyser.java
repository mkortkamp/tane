package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class FreeVariableAnalyser extends ASTVisitor {
	private final Stack<Set<IVariableBinding>> variableBindingFrames = new Stack<Set<IVariableBinding>>();
	private final Set<IVariableBinding> freeVariables = new HashSet<IVariableBinding>();
	private final int offset;
	private final int length;

	public FreeVariableAnalyser(int offset, int length) {
		this.offset = offset;
		this.length = length;
		pushFrame();
	}

	public Set<IVariableBinding> getFreeVariables() {
		return freeVariables;
	}
	
	public boolean isEmpty() {
		return getFreeVariables().isEmpty();
	}
	
	@Override
	public boolean visit(Block node) {
		pushFrame(node);
		return true;
	}

	@Override
	public void endVisit(Block node) {
		popFrame(node);
	}
	
	@Override
	public boolean visit(CatchClause node) {
		pushFrame(node);
		return true;
	}
	
	@Override
	public void endVisit(CatchClause node) {
		popFrame(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (isInRange(node)) {
			variableBindingFrames.peek().add((IVariableBinding) node.getName().resolveBinding());
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		if (!isInRange(node)) {
			return false;
		}
		
		final IBinding binding = node.resolveBinding();
		if (binding.getKind() != IBinding.VARIABLE) {
			return false;
		}
		
		IVariableBinding variableBinding = (IVariableBinding) binding;
		if (!isBound(variableBinding)) {
			freeVariables.add(variableBinding);
		}
		
		return false;
	}

	private boolean isBound(IVariableBinding variableBinding) {
		for (Set<IVariableBinding> frame : variableBindingFrames) {
			if (frame.contains(variableBinding)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInRange(ASTNode node) {
		return node.getStartPosition() >= offset || node.getStartPosition() + node.getLength() <= offset + length;
	}
	
	private void pushFrame(ASTNode node) {
		if (isInRange(node)) {
			pushFrame();
		}
	}
	
	private void popFrame(ASTNode node) {
		if (isInRange(node)) {
			variableBindingFrames.pop();
		}
	}
	
	private Set<IVariableBinding> pushFrame() {
		return variableBindingFrames.push(new HashSet<IVariableBinding>());
	}
}