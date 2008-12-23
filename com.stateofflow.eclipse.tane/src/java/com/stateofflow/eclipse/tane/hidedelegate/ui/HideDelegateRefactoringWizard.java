package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.stateofflow.eclipse.tane.hidedelegate.model.HideDelegateRefactoring;

public class HideDelegateRefactoringWizard extends RefactoringWizard {
    public HideDelegateRefactoringWizard(final HideDelegateRefactoring refactoring) {
        super(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE | RefactoringWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN);
    }

    @Override
    protected void addUserInputPages() {
        addPage(new MethodDetailsCollectionPage());
    }
}
