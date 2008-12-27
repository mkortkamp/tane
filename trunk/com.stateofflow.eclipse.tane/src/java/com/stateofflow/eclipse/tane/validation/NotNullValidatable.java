package com.stateofflow.eclipse.tane.validation;

import org.eclipse.core.runtime.CoreException;

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
