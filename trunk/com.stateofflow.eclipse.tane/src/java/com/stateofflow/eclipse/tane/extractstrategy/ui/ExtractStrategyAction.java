package com.stateofflow.eclipse.tane.extractstrategy.ui;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

import com.stateofflow.eclipse.tane.extractstrategy.model.ExtractStrategyRefactoring;
import com.stateofflow.eclipse.tane.ui.TaneEditorAction;

public class ExtractStrategyAction extends TaneEditorAction {

	@Override
	protected void doRun(final ICompilationUnit unit, final TextSelection textSelection) {
		final ExtractStrategyRefactoring refactoring = new ExtractStrategyRefactoring(unit, textSelection);
		final RefactoringWizard wizard = new ExtractStrategyRefactoringWizard(refactoring);
		try {
			new RefactoringWizardOpenOperation(wizard).run(getShell(), "Extract Strategy");
		} catch (final InterruptedException e) {
			// Cancelled
		}
	}

}
