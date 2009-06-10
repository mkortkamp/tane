package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.stateofflow.eclipse.tane.util.Range;

public class UnhandledCheckedExceptionAnalyser implements Analyser<ITypeBinding> {
	public Set<ITypeBinding> analyse(Range range, ASTNode node) {
		return getCheckedExceptions(new UnhandledExceptionAnalyser().analyse(range, node), node.getAST());
	}
	
	public Set<ITypeBinding> getCheckedExceptions(Set<ITypeBinding> unhandledExceptions, AST ast) {
		Set<ITypeBinding> checkedExceptions = new HashSet<ITypeBinding>();
		for (ITypeBinding exception : unhandledExceptions) {
			addIfChecked(ast, checkedExceptions, exception);
		}
		return checkedExceptions;
	}

	private void addIfChecked(AST ast, Set<ITypeBinding> checkedExceptions, ITypeBinding exception) {
		if (isCheckedException(exception, ast)) {
			checkedExceptions.add(exception);
		}
	}
	
	private boolean isCheckedException(ITypeBinding exception, AST ast) {
		return !exception.isSubTypeCompatible(resolveWellKnownType(RuntimeException.class, ast))
			&& exception.isSubTypeCompatible(resolveWellKnownType(Throwable.class, ast));
	}

	private ITypeBinding resolveWellKnownType(Class<?> type, AST ast) {
		return ast.resolveWellKnownType(type.getName());
	}
}
