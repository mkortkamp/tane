package com.stateofflow.eclipse.tane.flowanalysis;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class FreeVariableAnalyser extends AbstractFrameBasedAnalyser<IVariableBinding> {
	@Override
	public boolean visit(Block node) {
		pushFrame(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(Block node) {
		popFrame(node);
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(CatchClause node) {
		pushFrame(node);
		return super.visit(node);
	}
	
	@Override
	public void endVisit(CatchClause node) {
		popFrame(node);
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (isInRange(node)) {
			addToCurrentFrame((IVariableBinding) node.getName().resolveBinding());
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
		if (!isInFrames(variableBinding)) {
			addToResult(variableBinding);
		}
		
		return false;
	}
}