package com.stateofflow.eclipse.tane.validation.validators;

import org.eclipse.core.runtime.CoreException;

import com.stateofflow.eclipse.tane.util.ast.ASTSelection;
import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class NoFreeVariableValidatable implements Validatable {
	private final ASTSelection selection;

	public NoFreeVariableValidatable(ASTSelection selection) {
		this.selection = selection;
	}

	public void validate(Validator validator) throws CoreException {
		validator.validate(selection.getFreeVariables().isEmpty(), "There can be no free variables used by the selected code for this refactoring to work.");
	}

}
