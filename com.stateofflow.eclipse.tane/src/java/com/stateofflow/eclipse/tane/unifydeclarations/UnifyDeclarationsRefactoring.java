package com.stateofflow.eclipse.tane.unifydeclarations;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEditGroup;

public class UnifyDeclarationsRefactoring extends Refactoring {
    private final IDocument document;
    private Statement node;
    private final List<VariableDeclarationFragment> fragments;

    public UnifyDeclarationsRefactoring(IDocument document, Statement node, List<VariableDeclarationFragment> fragments) {
        this.document = document;
        this.node = node;
        this.fragments = fragments;
    }
    
    @Override
    public String getName() {
        return "Unify Declarations";
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }
    
    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return createChange(createRewrite());
    }

    private Change createChange(ASTRewrite rewrite) {
        DocumentChange documentChange = new DocumentChange("Unify Declarations", document);
        documentChange.setEdit(new MultiTextEdit());
        documentChange.addEdit(rewrite.rewriteAST(document, null));
        return documentChange;
    }

    private ASTRewrite createRewrite() throws CoreException {
        ASTRewrite rewrite = ASTRewrite.create(getAST());
        rewrite(rewrite);
        return rewrite;
    }

    private void rewrite(ASTRewrite rewrite) {
        TextEditGroup editGroup = new TextEditGroup("");
        Block block = insertBlock(rewrite, editGroup);
        insertNewDeclaration(rewrite, block, editGroup);
        removeOrReplaceExistingDeclarations(rewrite, editGroup);
    }

    @SuppressWarnings("unchecked")
    private Block insertBlock(ASTRewrite rewrite, TextEditGroup editGroup) {
        if (node.getParent().getNodeType() == ASTNode.BLOCK) {
            return (Block) node.getParent();
        }
        Block block = getAST().newBlock();
        Statement newNode = (Statement) rewrite.createMoveTarget(node);
        block.statements().add(newNode);
        rewrite.replace(node, block, editGroup);
        node = newNode;
        return block;
    }

    private void removeOrReplaceExistingDeclarations(ASTRewrite rewrite, TextEditGroup editGroup) {
        for (VariableDeclarationFragment fragment : fragments) {
            if (fragment.getInitializer() == null) {
                removeDeclaration(rewrite, editGroup, fragment);
            } else {
                replaceDeclarationWithAssignment(rewrite, editGroup, fragment);
            }
        }
    }

    private void replaceDeclarationWithAssignment(ASTRewrite rewrite, TextEditGroup editGroup, VariableDeclarationFragment fragment) {
        Assignment assignment = getAST().newAssignment();
        assignment.setLeftHandSide((SimpleName) rewrite.createCopyTarget(fragment.getName()));
        assignment.setRightHandSide((Expression) rewrite.createCopyTarget(fragment.getInitializer()));
        VariableDeclarationStatement declaration = (VariableDeclarationStatement) fragment.getParent();
        if (declaration.fragments().size() == 1) {
            rewrite.remove(declaration, editGroup);
        } else {
            rewrite.remove(fragment, editGroup);
        }
        rewrite.getListRewrite(declaration.getParent(), Block.STATEMENTS_PROPERTY).insertBefore(getAST().newExpressionStatement(assignment), declaration, editGroup);
    }

    public void removeDeclaration(ASTRewrite rewrite, TextEditGroup editGroup, VariableDeclarationFragment fragment) {
        VariableDeclarationStatement declaration = (VariableDeclarationStatement) fragment.getParent();
        if (declaration.fragments().size() == 1) {
            rewrite.remove(declaration, editGroup);
        } else {
            rewrite.remove(fragment, editGroup);
        }
    }

    @SuppressWarnings("unchecked")
    public void insertNewDeclaration(ASTRewrite rewrite, Block block, TextEditGroup editGroup) {
        VariableDeclarationFragment fragmentToCopy = fragments.get(0);
        final VariableDeclarationFragment newFragment = getAST().newVariableDeclarationFragment();
        newFragment.setExtraDimensions(fragmentToCopy.getExtraDimensions());
        newFragment.setName(getAST().newSimpleName(fragmentToCopy.getName().toString()));
        
        VariableDeclarationStatement declarationStatement = (VariableDeclarationStatement) fragmentToCopy.getParent();
        final VariableDeclarationStatement newDeclaration = getAST().newVariableDeclarationStatement(newFragment);
        
        newDeclaration.modifiers().addAll(declarationStatement.modifiers());
        newDeclaration.setType((Type) rewrite.createCopyTarget(declarationStatement.getType()));
        
        rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY).insertBefore(newDeclaration, node, editGroup);
    }

    private AST getAST() {
        return node.getAST();
    }
}