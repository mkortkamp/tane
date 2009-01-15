package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;

import com.stateofflow.eclipse.tane.hidedelegate.model.HideDelegateRefactoring;

public class HideDelegateLauncher {
    public void launch(final ICompilationUnit compilationUnit, final ITextSelection selection, Shell shell) {
        final HideDelegateRefactoring refactoring = new HideDelegateRefactoring(compilationUnit, selection);
        final RefactoringWizard wizard = new HideDelegateRefactoringWizard(refactoring);
        try {
            new RefactoringWizardOpenOperation(wizard).run(shell, "Hide Delegate");
        } catch (final InterruptedException e) {
            // Cancelled
        }
    }
}
