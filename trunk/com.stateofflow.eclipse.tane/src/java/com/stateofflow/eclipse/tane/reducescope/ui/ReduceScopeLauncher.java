package com.stateofflow.eclipse.tane.reducescope.ui;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;

import com.stateofflow.eclipse.tane.reducescope.model.ReduceScopeRefactoring;

public class ReduceScopeLauncher {
    public void launch(IDocument document, VariableDeclarationFragment node, Shell shell) {
        final ReduceScopeRefactoring refactoring = new ReduceScopeRefactoring(document, node);
        final RefactoringWizard wizard = new RefactoringWizard(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN) {
            @Override
            protected void addUserInputPages() {
            }
        };
        try {
            new RefactoringWizardOpenOperation(wizard).run(shell, "Reduce Scope");
        } catch (final InterruptedException e) {
            // Cancelled
        }
    }
}
