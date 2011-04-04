package com.stateofflow.eclipse.tane.minimisescope.ui.quickassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

import com.stateofflow.eclipse.tane.util.ast.ASTUtils;

public class QuickAssistProcessor implements IQuickAssistProcessor {
    public IJavaCompletionProposal[] getAssists(final IInvocationContext context, final IProblemLocation[] locations) throws CoreException {
        final ASTNode node = ASTUtils.findNode(context.getASTRoot(), context.getSelectionOffset());
        return isAMinimisableLocalDeclaration(node) ? getAssistsForVariableDeclaration(context, (VariableDeclarationFragment) node.getParent()) : null;
    }

    private boolean isAMinimisableLocalDeclaration(final ASTNode node) {
        if (!isALocalVariableDeclaration(node)) {
            return false;
        }
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.getParent();
        VariableDeclarationStatement statement = (VariableDeclarationStatement) fragment.getParent();
        return hasOnlyOneFragment(statement) && isValueNullOrConstant(fragment);
    }

    private boolean isValueNullOrConstant(VariableDeclarationFragment fragment) {
        return fragment.getInitializer() == null || fragment.getInitializer().resolveConstantExpressionValue() != null;
    }

    private boolean hasOnlyOneFragment(VariableDeclarationStatement node) {
        return node.fragments().size() == 1;
    }

    private boolean isALocalVariableDeclaration(final ASTNode node) {
        return isASimpleName(node) && isParentAVariableDeclarationFragment(node) && isGrandparentAVariableDeclaration(node);
    }

    private boolean isASimpleName(final ASTNode node) {
        return node.getNodeType() == ASTNode.SIMPLE_NAME;
    }

    private boolean isParentAVariableDeclarationFragment(final ASTNode node) {
        return node.getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT;
    }

    private boolean isGrandparentAVariableDeclaration(ASTNode node) {
        return node.getParent().getParent().getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT;
    }

    private IJavaCompletionProposal[] getAssistsForVariableDeclaration(@SuppressWarnings("unused") IInvocationContext context, VariableDeclarationFragment node) {
        return new IJavaCompletionProposal[]{new CompletionProposal(node)};
    }

    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }
}
