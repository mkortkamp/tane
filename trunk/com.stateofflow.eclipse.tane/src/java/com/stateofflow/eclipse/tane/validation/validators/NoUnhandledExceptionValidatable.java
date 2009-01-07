package com.stateofflow.eclipse.tane.validation.validators;

import org.eclipse.core.runtime.CoreException;

import com.stateofflow.eclipse.tane.util.ast.Selection;
import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class NoUnhandledExceptionValidatable implements Validatable {
	private final Selection selection;

	public NoUnhandledExceptionValidatable(Selection selection) {
		this.selection = selection;
	}

	public void validate(Validator validator) throws CoreException {
		validator.validate(selection.getUnhandledExceptions().isEmpty(), "There can be no unhandled checked exceptions thrown in the selected code for this refactoring to work.");
	}

}
