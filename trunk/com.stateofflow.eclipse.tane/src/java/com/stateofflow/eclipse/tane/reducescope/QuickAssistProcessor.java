package com.stateofflow.eclipse.tane.reducescope;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

import com.stateofflow.eclipse.tane.util.ast.ASTUtils;

public class QuickAssistProcessor implements IQuickAssistProcessor {
    public IJavaCompletionProposal[] getAssists(final IInvocationContext context, final IProblemLocation[] locations) throws CoreException {
        final ASTNode node = ASTUtils.findNode(context.getASTRoot(), context.getSelectionOffset());
        return ASTUtils.isALocalVariableDeclaration(node) ? getAssistsForVariableDeclaration((VariableDeclarationFragment) node.getParent()) : null;
    }

    private IJavaCompletionProposal[] getAssistsForVariableDeclaration(VariableDeclarationFragment node) {
        return new IJavaCompletionProposal[]{new CompletionProposal(node)};
    }

    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }
}
