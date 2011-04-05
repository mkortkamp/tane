package com.stateofflow.eclipse.tane.reducescope.ui.quickassist;

import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.stateofflow.eclipse.tane.Activator;
import com.stateofflow.eclipse.tane.reducescope.ui.ReduceScopeLauncher;

public class CompletionProposal implements IJavaCompletionProposal {
    private final VariableDeclarationFragment node;

    public CompletionProposal(VariableDeclarationFragment node) {
        this.node = node;
    }

    public void apply(IDocument document) {
        new ReduceScopeLauncher().launch(document, node, Activator.getShell());
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
