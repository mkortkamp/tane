package com.stateofflow.eclipse.tane.hidedelegate.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.Chain;
import com.stateofflow.eclipse.tane.hidedelegate.model.validation.initial.InitialConditionValidator;
import com.stateofflow.eclipse.tane.rewrite.map.RewriteMap;
import com.stateofflow.eclipse.tane.rewrite.map.RewriteMapBuilder;
import com.stateofflow.eclipse.tane.util.MemberFinder;
import com.stateofflow.eclipse.tane.util.Selection;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;

public class HideDelegateRefactoring extends Refactoring {
	private String methodName;
	private final Selection selection;
	private RewriteMap rewrites;
	private Scope scope;

	public HideDelegateRefactoring(final ICompilationUnit compilationUnit, final TextSelection textSelection) {
		selection = new Selection(compilationUnit, textSelection);
	}

	@Override
	public RefactoringStatus checkFinalConditions(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
		final SubMonitor monitor = SubMonitor.convert(pm, "Checking final conditions", 1);
		try {
			final RefactoringStatus status = new RefactoringStatus();
			rewrites = getChain().createRewriteMap(new MemberFinder(createJavaSearchScope()),
					new MemberFinder(SearchEngine.createWorkspaceScope()), new RewriteMapBuilder(status), methodName, monitor.newChild(1));
			return status;
		} finally {
			pm.done();
		}
	}

	private Chain getChain() {
		return new Chain(
				(Expression) selection.getParentOfNodeAtStartOfSelection(),
				(Expression) selection.getNodeEncompassingWholeSelection());
	}

    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		RefactoringStatusValidator validator = new RefactoringStatusValidator(status);
		new InitialConditionValidator().validate(selection, validator);
		return status;
    }

	@Override
	public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return rewrites.createChange(getName());
	}

	@Override
	public String getName() {
		return "Hide Delegate";
	}

	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	private IJavaSearchScope createJavaSearchScope() {
		return scope.createSearchEngineScope(selection.getCompilationUnit());
	}

	public void setScope(final Scope scope) {
		this.scope = scope;
	}
}
