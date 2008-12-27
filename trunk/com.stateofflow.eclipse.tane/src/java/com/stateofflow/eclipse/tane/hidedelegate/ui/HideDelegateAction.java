package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

import com.stateofflow.eclipse.tane.hidedelegate.model.HideDelegateRefactoring;
import com.stateofflow.eclipse.tane.ui.TaneEditorAction;

public class HideDelegateAction extends TaneEditorAction {

	@Override
	public void doRun(final ICompilationUnit compilationUnit, final TextSelection selection) {
		final HideDelegateRefactoring refactoring = new HideDelegateRefactoring(compilationUnit, selection);
		final RefactoringWizard wizard = new HideDelegateRefactoringWizard(refactoring);
		try {
			new RefactoringWizardOpenOperation(wizard).run(getShell(), "Hide Delegate");
		} catch (final InterruptedException e) {
			// Cancelled
		}
	}
}
