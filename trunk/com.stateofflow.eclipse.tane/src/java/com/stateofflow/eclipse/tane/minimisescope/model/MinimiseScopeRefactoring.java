package com.stateofflow.eclipse.tane.minimisescope.model;

import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findFirst;
import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findLowestCommonAncestor;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.SimpleName;
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

import com.stateofflow.eclipse.tane.flowanalysis.VariableReferenceAnalyser;
import com.stateofflow.eclipse.tane.util.Range;

public class MinimiseScopeRefactoring extends Refactoring {
    private final VariableDeclarationFragment declarationFragment;
    private VariableDeclarationStatement declarationStatement;
    private Set<SimpleName> references;
    private Change change;
    private final IDocument document;

    public MinimiseScopeRefactoring(IDocument document, VariableDeclarationFragment declarationFragment) {
        this.document = document;
        this.declarationFragment = declarationFragment;
    }

    @Override
    public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        return change;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        final ASTNode commonAncestorOfReferences = findLowestCommonAncestor(references);
        ASTNode containingStatement = findContainingStatement(commonAncestorOfReferences);
        final ASTNode firstReference = findStatementContainedBy(containingStatement, findContainingStatement(findFirst(references)));
        if (containingStatement == firstReference && containingStatement.getParent().getNodeType() == ASTNode.BLOCK) {
            containingStatement = containingStatement.getParent();
        }
        refactor(containingStatement, firstReference);
        RefactoringStatus refactoringStatus = new RefactoringStatus();
        return refactoringStatus;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        declarationStatement = (VariableDeclarationStatement) declarationFragment.getParent();
        references = findReferences();
        RefactoringStatus status = new RefactoringStatus();
        if (references.isEmpty()) {
            status.addError("The selected variable has no references");
        }
        return status;
    }

    @Override
    public String getName() {
        return "Minimise Scope";
    }

    private Set<SimpleName> findReferences() {
        final ASTNode enclosingBlock = declarationStatement.getParent();
        return new VariableReferenceAnalyser(declarationFragment.getName()).analyse(new Range(declarationFragment.getStartPosition() + declarationFragment.getLength(), enclosingBlock.getStartPosition() + enclosingBlock.getLength()), enclosingBlock);
    }

    private ASTNode findStatementContainedBy(ASTNode container, ASTNode statementNode) {
        if (statementNode == container) {
            return statementNode;
        }

        final ASTNode nodeContainingParent = findContainingStatement(statementNode.getParent());
        return nodeContainingParent == container ? statementNode : findStatementContainedBy(container, nodeContainingParent);
    }

    private void refactor(final ASTNode statementContainingAllReferences, ASTNode firstReference) {
        createChange(createRewrite(statementContainingAllReferences, firstReference));
    }

    private void createChange(ASTRewrite rewrite) {
        DocumentChange documentChange = new DocumentChange("Minimise Scope", document);
        documentChange.setEdit(new MultiTextEdit());
        documentChange.addEdit(rewrite.rewriteAST(document, null));
        change = documentChange;
    }

    private ASTRewrite createRewrite(final ASTNode statementContainingAllReferences, ASTNode firstReference) {
        TextEditGroup editGroup = new TextEditGroup("");
        ASTRewrite rewrite = ASTRewrite.create(getAST());
        ASTNode newDeclaration = removeExistingFragment(editGroup, rewrite);

        if (statementContainingAllReferences.getNodeType() != ASTNode.BLOCK) {
            rewriteInsideNewBlock(rewrite, statementContainingAllReferences, newDeclaration, editGroup);
        } else {
            insertDeclaration(rewrite, statementContainingAllReferences, firstReference, newDeclaration, editGroup);
        }
        return rewrite;
    }

    private VariableDeclarationStatement removeExistingFragment(TextEditGroup editGroup, ASTRewrite rewrite) {
        return declarationStatement.fragments().size() == 1 ? removeExistingDeclaration(editGroup, rewrite) : removeExistingFragmentAndCreateNewDeclaration(editGroup, rewrite);
    }

    @SuppressWarnings("unchecked")
    private VariableDeclarationStatement removeExistingFragmentAndCreateNewDeclaration(TextEditGroup editGroup, ASTRewrite rewrite) {
        final VariableDeclarationFragment newFragment = (VariableDeclarationFragment) rewrite.createMoveTarget(declarationFragment);
        final VariableDeclarationStatement newDeclaration = getAST().newVariableDeclarationStatement(newFragment);
        newDeclaration.modifiers().addAll(declarationStatement.modifiers());
        newDeclaration.setType((Type) rewrite.createCopyTarget(declarationStatement.getType()));
        rewrite.remove(declarationFragment, editGroup);
        return newDeclaration;
    }

    private VariableDeclarationStatement removeExistingDeclaration(TextEditGroup editGroup, ASTRewrite rewrite) {
        final VariableDeclarationStatement newDeclaration = (VariableDeclarationStatement) rewrite.createMoveTarget(declarationStatement);
        rewrite.remove(declarationStatement, editGroup);
        return newDeclaration;
    }

    private void insertDeclaration(ASTRewrite rewrite, final ASTNode container, ASTNode insertionPoint, ASTNode newDeclaration, TextEditGroup editGroup) {
        rewrite.getListRewrite(container, Block.STATEMENTS_PROPERTY).insertBefore(newDeclaration, insertionPoint, editGroup);
    }

    @SuppressWarnings("unchecked")
    private void rewriteInsideNewBlock(ASTRewrite rewrite, final ASTNode originalStatement, ASTNode newDeclaration, TextEditGroup editGroup) {
        Block block = getAST().newBlock();
        block.statements().add(newDeclaration);
        block.statements().add(rewrite.createMoveTarget(originalStatement));
        rewrite.replace(originalStatement, block, editGroup);
    }

    private AST getAST() {
        return declarationStatement.getAST();
    }

    private ASTNode findContainingStatement(ASTNode node) {
        switch (node.getNodeType()) {
            case ASTNode.ASSERT_STATEMENT :
            case ASTNode.BLOCK :
            case ASTNode.DO_STATEMENT :
            case ASTNode.ENHANCED_FOR_STATEMENT :
            case ASTNode.EXPRESSION_STATEMENT :
            case ASTNode.FOR_STATEMENT :
            case ASTNode.IF_STATEMENT :
            case ASTNode.RETURN_STATEMENT :
            case ASTNode.SWITCH_STATEMENT :
            case ASTNode.THROW_STATEMENT :
            case ASTNode.VARIABLE_DECLARATION_STATEMENT :
            case ASTNode.WHILE_STATEMENT :
                return node;
            default :
                return findContainingStatement(node.getParent());
        }
    }
}
