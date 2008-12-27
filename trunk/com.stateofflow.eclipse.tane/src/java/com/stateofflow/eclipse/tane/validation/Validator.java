package com.stateofflow.eclipse.tane.validation;

import org.eclipse.core.runtime.CoreException;

public interface Validator {
	boolean validate(final boolean value, final String message);
	boolean validate(Validatable validatable) throws CoreException;
	boolean isOK();
}
