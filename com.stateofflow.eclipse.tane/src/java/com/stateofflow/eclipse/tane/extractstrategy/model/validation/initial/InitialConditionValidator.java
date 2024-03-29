package com.stateofflow.eclipse.tane.extractstrategy.model.validation.initial;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;

import com.stateofflow.eclipse.tane.util.ast.ASTSelection;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;
import com.stateofflow.eclipse.tane.validation.validators.NoFreeVariableValidatable;
import com.stateofflow.eclipse.tane.validation.validators.NoUnhandledExceptionValidatable;

public class InitialConditionValidator {
	private final RefactoringStatusValidator validator;
	private final ASTSelection selection;

	public InitialConditionValidator(ASTSelection selection, RefactoringStatusValidator validator) {
		this.selection = selection;
		this.validator = validator;
	}

	public boolean validate() throws CoreException, OperationCanceledException {
		return validator.validate(selection) //
			&& validator.validate(new NoFreeVariableValidatable(selection)) //
			&& validator.validate(new NoUnhandledExceptionValidatable(selection));
	}
}
