package com.stateofflow.eclipse.tane.extractstrategy.ui;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.stateofflow.eclipse.tane.extractstrategy.model.ExtractStrategyRefactoring;

public class ExtractStrategyRefactoringWizard extends RefactoringWizard {

	public ExtractStrategyRefactoringWizard(final ExtractStrategyRefactoring refactoring) {
		super(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE | RefactoringWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN);
	}

	@Override
	protected void addUserInputPages() {
		addPage(new ExtractStrategyPage());
	}

}
