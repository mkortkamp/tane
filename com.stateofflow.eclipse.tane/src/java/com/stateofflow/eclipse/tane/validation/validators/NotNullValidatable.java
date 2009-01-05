package com.stateofflow.eclipse.tane.validation.validators;

import org.eclipse.core.runtime.CoreException;

import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class NotNullValidatable implements Validatable {
	private final Object object;
	private final String message;

	public NotNullValidatable(Object object, String message) {
		this.object = object;
		this.message = message;
	}
	
	public void validate(Validator validator) throws CoreException {
		validator.validate(object != null, message);
	}	
}
