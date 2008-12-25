package com.stateofflow.eclipse.tane.hidedelegate.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.Chain;
import com.stateofflow.eclipse.tane.util.Selection;
import com.stateofflow.eclipse.tane.validation.Validator;

class InitialConditionValidator {
    private final Selection selection;
	private final Validator validator;

    public InitialConditionValidator(final Selection selection, Validator validator) {
        this.selection = selection;
		this.validator = validator;
    }

    public void checkInitialConditions() throws CoreException, OperationCanceledException {
        validateCompilationUnitStructure();
        validateMethodInvocation();
    }

    private boolean isEncapsulateable(final ASTNode node, final ASTNode root) {
        return node.getNodeType() == ASTNode.METHOD_INVOCATION || node == root && node.getNodeType() == ASTNode.FIELD_ACCESS;
    }

    private void validate(final boolean value, final String message) {
        validator.validate(value, message);
    }

    private void validateCompilationUnitStructure() throws JavaModelException {
        validate(selection.isStructureKnown(), "The structure of the compilation unit is not known");
    }

    private void validateEncapsulateable(final boolean value, final ASTNode root) {
        validate(value, "This refactoring can only be applied to method invocation chains with an optional field access as the final item in the chain. Got " + root);
    }

    private void validateMethodInvocation() throws JavaModelException {
        validateSomethingIsSelected();
        if (validator.isOK()) {
            validateSelectedItemAreDotExpressionItems();
        }
        if (validator.isOK()) {
            validateSelectedMethodInvocationIsRefactorable();
        }
    }

    private void validateSelectedItemAreDotExpressionItems() {
        final ASTNode root = selection.getNodeEncompassingWholeSelection();
        validateEncapsulateable(root != null, root);

        ASTNode node = selection.getParentOfNodeAtStartOfSelection();
        validateEncapsulateable(isEncapsulateable(node, root), root);
        while (node != root) {
            node = node.getParent();
            validateEncapsulateable(isEncapsulateable(node, root), root);
        }
    }

    private void validateSelectedMethodInvocationIsRefactorable() throws JavaModelException {
        new Chain((Expression) selection.getParentOfNodeAtStartOfSelection(), (Expression) selection.getNodeEncompassingWholeSelection()).validate(validator);
    }

	private void validateSomethingIsSelected() {
        validate(selection.isSomethingSelected(), "Something encapsulateable must be selected");
    }
}
