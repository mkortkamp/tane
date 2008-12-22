package com.stateofflow.eclipse.tane.hidedelegate.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.Chain;
import com.stateofflow.eclipse.tane.util.Selection;

class InitialConditionValidator {
    private final Selection selection;

    public InitialConditionValidator(final Selection selection) {
        this.selection = selection;
    }

    public RefactoringStatus checkInitialConditions() throws CoreException, OperationCanceledException {
        final RefactoringStatus status = new RefactoringStatus();
        validateCompilationUnitStructure(status);
        validateMethodInvocation(status);
        return status;
    }

    private boolean isEncapsulateable(final ASTNode node, final ASTNode root) {
        return node.getNodeType() == ASTNode.METHOD_INVOCATION || node == root && node.getNodeType() == ASTNode.FIELD_ACCESS;
    }

    private void validate(final RefactoringStatus status, final boolean value, final String message) {
        if (!value) {
            status.addFatalError(message);
        }
    }

    private void validateCompilationUnitStructure(final RefactoringStatus status) throws JavaModelException {
        validate(status, selection.isStructureKnown(), "The structure of the compilation unit is not known");
    }

    private void validateEncapsulateable(final RefactoringStatus status, final boolean value, final ASTNode root) {
        validate(status, value, "This refactoring can only be applied to method invocation chains with an optional field access as the final item in the chain. Got " + root);
    }

    private void validateMethodInvocation(final RefactoringStatus status) throws JavaModelException {
        validateSomethingIsSelected(status);
        if (status.isOK()) {
            validateSelectedItemAreDotExpressionItems(status);
        }
        if (status.isOK()) {
            validateSelectedMethodInvocationIsRefactorable(status);
        }
    }

    private void validateSelectedItemAreDotExpressionItems(final RefactoringStatus status) {
        final ASTNode root = selection.getNodeEncompassingWholeSelection();
        validateEncapsulateable(status, root != null, root);

        ASTNode node = selection.getParentOfNodeAtStartOfSelection();
        validateEncapsulateable(status, isEncapsulateable(node, root), root);
        while (node != root) {
            node = node.getParent();
            validateEncapsulateable(status, isEncapsulateable(node, root), root);
        }
    }

    private void validateSelectedMethodInvocationIsRefactorable(final RefactoringStatus status) throws JavaModelException {
        final Chain chain = new Chain((Expression) selection.getParentOfNodeAtStartOfSelection(), (Expression) selection.getNodeEncompassingWholeSelection());
        final IType declaringClass = chain.getDeclaringTypeOfOrigin();
        validate(status, !declaringClass.isInterface(), "Cannot encapsulate on an interface: " + declaringClass.getFullyQualifiedName());
        validate(status, !declaringClass.isBinary(), "Target source is not available: " + declaringClass.getFullyQualifiedName());
    }

    private void validateSomethingIsSelected(final RefactoringStatus status) {
        validate(status, selection.isSomethingSelected(), "Something encapsulateable must be selected");
    }
}
