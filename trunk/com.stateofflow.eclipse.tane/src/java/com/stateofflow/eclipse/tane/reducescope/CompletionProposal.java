package com.stateofflow.eclipse.tane.reducescope;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.stateofflow.eclipse.tane.Activator;

public class CompletionProposal implements IJavaCompletionProposal {
    private final VariableDeclarationFragment node;

    public CompletionProposal(VariableDeclarationFragment node) {
        this.node = node;
    }

    public void apply(IDocument document) {
        openRefactoringWizard(createRefactoringWizard(new ReduceScopeRefactoring(document, node)));
    }

    private void openRefactoringWizard(final RefactoringWizard wizard) {
        try {
            new RefactoringWizardOpenOperation(wizard).run(Activator.getShell(), "Reduce Scope");
        } catch (final InterruptedException e) {
            // Cancelled
        }
    }

    private RefactoringWizard createRefactoringWizard(final ReduceScopeRefactoring refactoring) {
        return new RefactoringWizard(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN) {
            @Override
            protected void addUserInputPages() {
            }
        };
    }

    public String getAdditionalProposalInfo() {
        return null;
    }

    public IContextInformation getContextInformation() {
        return null;
    }

    public String getDisplayString() {
        return "Reduce Scope";
    }

    public Image getImage() {
        return null;
    }

    public Point getSelection(IDocument document) {
        return null;
    }

    public int getRelevance() {
        return 0;
    }

}
