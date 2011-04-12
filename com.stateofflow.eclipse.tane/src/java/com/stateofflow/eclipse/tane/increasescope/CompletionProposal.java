package com.stateofflow.eclipse.tane.increasescope;

import java.util.List;

import org.eclipse.jdt.core.dom.Statement;
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
    private final Statement node;
    private final List<VariableDeclarationFragment> fragments;

    public CompletionProposal(Statement node, List<VariableDeclarationFragment> fragments) {
        this.node = node;
        this.fragments = fragments;
    }

    public void apply(IDocument document) {
        openRefactoringWizard(createRefactoringWizard(document, new IncreaseScopeRefactoring(document, node, fragments)));
    }

    private void openRefactoringWizard(final RefactoringWizard wizard) {
        try {
            new RefactoringWizardOpenOperation(wizard).run(Activator.getShell(), "Unify Declarations");
        } catch (final InterruptedException e) {
            // Cancelled
        }
    }

    private RefactoringWizard createRefactoringWizard(final IDocument document, final IncreaseScopeRefactoring refactoring) {
        return new RefactoringWizard(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN) {
            @Override
            protected void addUserInputPages() {
            }
        };
    }
    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Increase Scope";
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public int getRelevance() {
        return 0;
    }
}
