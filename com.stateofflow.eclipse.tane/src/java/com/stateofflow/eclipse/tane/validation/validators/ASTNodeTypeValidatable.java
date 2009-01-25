package com.stateofflow.eclipse.tane.validation.validators;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class ASTNodeTypeValidatable implements Validatable {
	private final ASTNode node;
	private final String message;
	private final int[] validNodeTypes;

	public ASTNodeTypeValidatable(ASTNode node, String message, int... validNodeTypes) {
		this.node = node;
		this.message = message;
		this.validNodeTypes = validNodeTypes;
	}

	public void validate(Validator validator) throws CoreException {
		for (int type : validNodeTypes) {
			if (node.getNodeType() == type) {
				return;
			}
		}
		validator.validate(false, message);
	}
}
