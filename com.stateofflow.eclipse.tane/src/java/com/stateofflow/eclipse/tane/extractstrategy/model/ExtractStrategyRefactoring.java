package com.stateofflow.eclipse.tane.extractstrategy.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.stateofflow.eclipse.tane.extractstrategy.model.validation.initial.InitialConditionValidator;
import com.stateofflow.eclipse.tane.util.Selection;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;

public class ExtractStrategyRefactoring extends Refactoring {
	private final Selection selection;
	private final ICompilationUnit compilationUnit;

	public ExtractStrategyRefactoring(final ICompilationUnit unit, final TextSelection textSelection) {
		this.compilationUnit = unit;
		selection = new Selection(unit, textSelection);
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RefactoringStatusValidator validator = new RefactoringStatusValidator();
		new InitialConditionValidator(selection, validator).validate();
		return validator.getStatus();
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		final CompositeChange changes = new CompositeChange(getName());

		return changes;
	}

	@Override
	public String getName() {
		return "Extract Strategy";
	}

}
