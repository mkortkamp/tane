package com.stateofflow.eclipse.tane.validation;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class RefactoringStatusValidator implements Validator {
	private final RefactoringStatus status;
	
	public RefactoringStatusValidator(RefactoringStatus status) {
		this.status = status;
	}

	public void validate(boolean value, String message) {
        if (!value) {
            status.addFatalError(message);
        }
    }
	
	public boolean isOK() {
		return status.isOK();
	}
}
