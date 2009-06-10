package com.stateofflow.eclipse.tane.hidedelegate.model.validation.initial;

import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

class NonRootNodeValidateable implements Validatable {
	private final ASTNode root;
	private final ASTNode origin;

	public NonRootNodeValidateable(final ASTNode origin, final ASTNode root) {
		this.root = root;
		this.origin = origin;
	}
	
    public void validate(final Validator validator) {
        for (ASTNode node = origin; node != root; node = node.getParent()) {
			validateNonRootNode(validator, node);
		}
    }

	private void validateNonRootNode(Validator validator, ASTNode node) {
		validator.validate(node.getNodeType() == ASTNode.METHOD_INVOCATION, "This refactoring can only be applied to method invocation chains with an optional field access as the final item in the chain. Got " + root + ". Validation failed at " + node);
	}
}
