package com.stateofflow.eclipse.tane.validation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class RefactoringStatusValidator implements Validator {
	private final RefactoringStatus status;
	
	public RefactoringStatusValidator(RefactoringStatus status) {
		this.status = status;
	}

	public boolean validate(boolean value, String message) {
        if (isOK() && !value) {
            status.addFatalError(message);
        }
        
        return isOK();
    }
	
	public boolean validate(Validatable validatable) throws CoreException {
		if (isOK()) {
			validatable.validate(this);
		}
		return isOK();
	}
	
	public boolean isOK() {
		return status.isOK();
	}
}
