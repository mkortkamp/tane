package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import com.stateofflow.eclipse.tane.util.Range;
import com.stateofflow.eclipse.tane.util.TypeSetMinimizer;

public class UnhandledExceptionAnalyser extends AbstractFrameBasedAnalyser<ITypeBinding> {
	@Override
	public Set<ITypeBinding> analyse(Range range, ASTNode node) {
		return new TypeSetMinimizer().getMinimalSet(super.analyse(range, node));
	}
	
	@Override
	public boolean visit(TryStatement node) {
		pushFrame();
		for (CatchClause catchClause : getCatchClauses(node)) {
			addToCurrentFrame(catchClause.getException().resolveBinding().getType());
		}
		return super.visit(node);
	}
	
	@Override
	public void endVisit(TryStatement node) {
		popFrame(node);
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(CatchClause node) {
		popFrame(node);
		return super.visit(node);
	}
	
	@Override
	public void endVisit(CatchClause node) {
		pushFrame(node);
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(ConstructorInvocation node) {
		addToResult(node.resolveConstructorBinding().getExceptionTypes());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		addToResult(node.resolveMethodBinding().getExceptionTypes());
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ThrowStatement node) {
		addToResult(node.getExpression().resolveTypeBinding());
		return super.visit(node);
	}

	private void addToResult(ITypeBinding... exceptions) {
		for (ITypeBinding exception : exceptions) {
			if (!isCaught(exception)) {
				addToResult(exception);
			}
		}
	}

	private boolean isCaught(ITypeBinding exception) {
		for (Set<ITypeBinding> frame : frames) {
			if (isCaught(frame, exception)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isCaught(Set<ITypeBinding> frame, ITypeBinding exception) {
		for (ITypeBinding caughtException : frame) {
			if (exception.isSubTypeCompatible(caughtException)) {
				return true;
			}
		}
		
		return false;
	}

	@SuppressWarnings({ "unchecked", "cast" })
	private List<CatchClause> getCatchClauses(TryStatement node) {
		return (List<CatchClause>) node.catchClauses();
	}
}
