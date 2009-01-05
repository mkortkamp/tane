package com.stateofflow.eclipse.tane.extractstrategy.model.validation.initial;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;

import com.stateofflow.eclipse.tane.util.Selection;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;
import com.stateofflow.eclipse.tane.validation.validators.NoFreeVariableValidatable;

public class InitialConditionValidator {
	private final RefactoringStatusValidator validator;
	private final Selection selection;

	public InitialConditionValidator(Selection selection, RefactoringStatusValidator validator) {
		this.selection = selection;
		this.validator = validator;
	}

	public boolean validate() throws CoreException, OperationCanceledException {
		return validator.validate(selection) //
			&& validator.validate(new NoFreeVariableValidatable(selection));
	}
}
