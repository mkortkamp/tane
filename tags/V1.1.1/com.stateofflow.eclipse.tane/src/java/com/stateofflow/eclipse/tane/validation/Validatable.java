package com.stateofflow.eclipse.tane.validation;

import org.eclipse.core.runtime.CoreException;

public interface Validatable {
	void validate(Validator validator) throws CoreException;
}
