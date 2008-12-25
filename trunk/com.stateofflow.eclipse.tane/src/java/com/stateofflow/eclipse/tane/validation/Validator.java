package com.stateofflow.eclipse.tane.validation;

public interface Validator {
	void validate(final boolean value, final String message);
	boolean isOK();
}
