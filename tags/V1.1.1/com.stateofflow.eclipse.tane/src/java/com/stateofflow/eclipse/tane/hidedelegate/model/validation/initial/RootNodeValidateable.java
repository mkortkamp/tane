package com.stateofflow.eclipse.tane.hidedelegate.model.validation.initial;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;
import com.stateofflow.eclipse.tane.validation.validators.ASTNodeTypeValidatable;
import com.stateofflow.eclipse.tane.validation.validators.NotNullValidatable;

class RootNodeValidateable implements Validatable {
	private static final String BASIC_FAILURE_MESSAGE = "This refactoring can only be applied to method invocation chains with an optional field access as the final item in the chain.";

	private final ASTNode root;

	public RootNodeValidateable(ASTNode root) {
		this.root = root;
	}

	public void validate(Validator validator) throws CoreException {
		validateWithShortCircuit(validator);
	}

	private boolean validateWithShortCircuit(Validator validator) throws CoreException {
		return validator.validate(new NotNullValidatable(root, BASIC_FAILURE_MESSAGE)) //
			&& validator.validate(new ASTNodeTypeValidatable(root, BASIC_FAILURE_MESSAGE + " Got " + root, ASTNode.METHOD_INVOCATION, ASTNode.FIELD_ACCESS));
	}

}
