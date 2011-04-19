package com.stateofflow.eclipse.tane.reducescope;

import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findContainingStatement;
import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findFirst;
import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findLowestCommonAncestor;
import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findStatementContainedBy;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.stateofflow.eclipse.tane.Activator;
import com.stateofflow.eclipse.tane.flowanalysis.VariableReferenceAnalyser;
import com.stateofflow.eclipse.tane.util.Range;

public class ReduceScopeRefactoring extends Refactoring {
    private final VariableDeclarationFragment declarationFragment;
    private VariableDeclarationStatement declarationStatement;
    private Set<SimpleName> references;
    private Change change;
    private final IDocument document;

    public ReduceScopeRefactoring(IDocument document, VariableDeclarationFragment declarationFragment) {
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
        RefactoringStatus status = new RefactoringStatus();
        checkNotANoOp(firstReference, status);
        refactor(containingStatement, firstReference);
        return status;
    }

    private void checkNotANoOp(final ASTNode firstReference, RefactoringStatus status) throws CoreException {
        if (isMoveANoOp(firstReference)) {
            status.addError("The variable is already in minimal scope");
        }
    }

    private boolean isMoveANoOp(final ASTNode firstReference) throws CoreException {
        try {
            int endOfCurrentLocation = declarationStatement.getStartPosition() + declarationStatement.getLength();
            String interveningCharacters = document.get(endOfCurrentLocation, firstReference.getStartPosition() - endOfCurrentLocation);
            return interveningCharacters.matches("\\s*");
        } catch (BadLocationException e) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unexpected", e));
        }
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        checkAssignedValue(status);
        declarationStatement = (VariableDeclarationStatement) declarationFragment.getParent();
        references = findReferences();
        checkReferences(status);
        return status;
    }

    private void checkReferences(RefactoringStatus status) {
        if (references.isEmpty()) {
            status.addError("The selected variable has no references");
        }
    }

    private void checkAssignedValue(RefactoringStatus status) {
        if (!isUninitializedOrConstant(declarationFragment)) {
            status.addWarning("Assignment to a non-constant value. Changing the location of the declaration may affect behaviour.");
        }
    }
    
    private boolean isUninitializedOrConstant(VariableDeclarationFragment fragment) {
        final Expression initializer = fragment.getInitializer();
        return initializer == null || initializer.getNodeType() == ASTNode.NULL_LITERAL || initializer.resolveConstantExpressionValue() != null;
    }

    @Override
    public String getName() {
        return "Reduce Scope";
    }

    private Set<SimpleName> findReferences() {
        final ASTNode enclosingBlock = declarationStatement.getParent();
        return new VariableReferenceAnalyser(declarationFragment.getName()).analyse(new Range(declarationFragment.getStartPosition() + declarationFragment.getLength(), enclosingBlock.getStartPosition() + enclosingBlock.getLength()), enclosingBlock);
    }

    private void refactor(final ASTNode statementContainingAllReferences, ASTNode firstReference) throws CoreException {
        createChange(createRewrite(statementContainingAllReferences, firstReference));
    }

    private void createChange(ASTRewrite rewrite) {
        DocumentChange documentChange = new DocumentChange("Reduce Scope", document);
        documentChange.setEdit(new MultiTextEdit());
        documentChange.addEdit(rewrite.rewriteAST(document, null));
        change = documentChange;
    }

    private ASTRewrite createRewrite(final ASTNode statementContainingAllReferences, ASTNode firstReference) throws CoreException {
        ASTRewrite rewrite = ASTRewrite.create(getAST());
        rewrite(statementContainingAllReferences, firstReference, rewrite);
        return rewrite;
    }

    private void rewrite(final ASTNode statementContainingAllReferences, ASTNode firstReference, ASTRewrite rewrite) {
        TextEditGroup editGroup = new TextEditGroup("");
        ASTNode newDeclaration = removeExistingFragment(editGroup, rewrite);

        if (statementContainingAllReferences.getNodeType() != ASTNode.BLOCK) {
            rewriteInsideNewBlock(rewrite, statementContainingAllReferences, newDeclaration, editGroup);
        } else {
            insertDeclaration(rewrite, statementContainingAllReferences, firstReference, newDeclaration, editGroup);
        }
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
}
