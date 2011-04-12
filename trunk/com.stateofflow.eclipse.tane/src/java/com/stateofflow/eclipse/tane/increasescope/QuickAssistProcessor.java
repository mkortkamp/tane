package com.stateofflow.eclipse.tane.increasescope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;

import com.stateofflow.eclipse.tane.util.ast.ASTUtils;

public class QuickAssistProcessor implements IQuickAssistProcessor {
    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return false;
    }

    @Override
    public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
        final ASTNode node = ASTUtils.findNode(context.getASTRoot(), context.getSelectionOffset());
        return ASTUtils.isALocalVariableDeclaration(node) ? getAssists((VariableDeclarationFragment) node.getParent()) : null;
    }
    
    private IJavaCompletionProposal[] getAssists(VariableDeclarationFragment fragment) {
        if (isArray(fragment)) {
            return null;
        }
        final Statement containingConditional = (Statement) getContainingConditional(fragment);
        if (containingConditional == null) {
            return null;
        }
        if (parentHasLocalVariableDeclarationOfSameNameAfterNewDeclaration(containingConditional, fragment.getName().toString())) {
            return null;
        }
        final List<VariableDeclarationFragment> declarationsWithSameName = findVariableDeclarations(containingConditional, fragment.getName().toString());
        return allDeclarationsHaveTheSameType(declarationsWithSameName) ? getAssistsForVariableDeclarations(containingConditional, declarationsWithSameName) : null;
    }

    private boolean parentHasLocalVariableDeclarationOfSameNameAfterNewDeclaration(Statement containingConditional, String name) {
        if (containingConditional.getParent().getNodeType() != ASTNode.BLOCK) {
            return false;
        }
        Block block = (Block) containingConditional.getParent();
        for (int i = block.statements().indexOf(containingConditional) + 1 ; i < block.statements().size() ; i++) {
            if (hasDeclarationWithName((ASTNode) block.statements().get(i), name)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDeclarationWithName(final ASTNode node, final String name) {
        final boolean[] result = { false };
        node.accept(new ASTVisitor() {
            public boolean visit(VariableDeclarationFragment node) {
                result[0] |= node.getName().toString().equals(name);
                return false;
            }
        });
        return result[0];
    }

    public boolean isArray(VariableDeclarationFragment fragment) {
        return fragment.getExtraDimensions() > 0 || ((VariableDeclarationStatement) fragment.getParent()).getType().isArrayType();
    }

    private IJavaCompletionProposal[] getAssistsForVariableDeclarations(Statement containingNode, List<VariableDeclarationFragment> declarationsWithSameName) {
        return new IJavaCompletionProposal[] { new CompletionProposal(containingNode, declarationsWithSameName) };
    }

    private Statement getContainingConditional(ASTNode node) {
        ASTNode containingStatement = ASTUtils.findContainingStatement(node);
        while (!isConditional(containingStatement) && containingStatement.getParent().getNodeType() != ASTNode.METHOD_DECLARATION) {
            containingStatement = ASTUtils.findContainingStatement(containingStatement.getParent());
        }
        return isConditional(containingStatement) ? (Statement) containingStatement : null;
    }

    public boolean isConditional(ASTNode containingStatement) {
        return containingStatement.getNodeType() == ASTNode.IF_STATEMENT || containingStatement.getNodeType() == ASTNode.SWITCH_STATEMENT;
    }

    private boolean allDeclarationsHaveTheSameType(List<VariableDeclarationFragment> fragments) {
        Set<ITypeBinding> types = new HashSet<ITypeBinding>();
        for (VariableDeclarationFragment fragment : fragments) {
            types.add(fragment.resolveBinding().getType());
        }
        return types.size() == 1;
    }

    private List<VariableDeclarationFragment> findVariableDeclarations(ASTNode containingNode, final String variableName) {
        final List<VariableDeclarationFragment> fragments = new ArrayList<VariableDeclarationFragment>();
        containingNode.accept(new ASTVisitor() {
            @Override
            public boolean visit(AnonymousClassDeclaration node) {
                return false;
            }
            @Override
            public boolean visit(org.eclipse.jdt.core.dom.TypeDeclaration node) {
                return false;
            }
            public boolean visit(VariableDeclarationFragment node) {
                if (node.getName().toString().equals(variableName)) {
                    fragments.add(node);
                }
                return false;
            }
        });
        return fragments;
    }
}
