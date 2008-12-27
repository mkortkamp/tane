package com.stateofflow.eclipse.tane.extractstrategy.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.stateofflow.eclipse.tane.util.Selection;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;

class InitialConditionValidator {

	private final RefactoringStatusValidator validator;
	private final Selection selection;
	private Set<IBinding> externalVariables = new HashSet<IBinding>();

	public InitialConditionValidator(Selection selection, RefactoringStatusValidator validator) {
		this.selection = selection;
		this.validator = validator;
	}

	public void checkInitialConditions() throws CoreException, OperationCanceledException {
		validateCompilationUnitStructure();
		validateSomethingIsSelected();
		if (validator.isOK()) {
			validateSelectionDoesNotContainExternalVariables();
		}
	}

	private void validateSelectionDoesNotContainExternalVariables() {
		final ASTNode root = selection.getNodeEncompassingWholeSelection();
		root.accept(new ASTVisitor() {
			private Set<IBinding> bindings = new HashSet<IBinding>();

			@Override
			public boolean visit(VariableDeclarationFragment node) {
				bindings.add(node.resolveBinding());
				return true;
			}

			@Override
			public boolean visit(SimpleName node) {
				externalVariables.add(node.resolveBinding());
				return false;
			}

		});
	}

	private void validateCompilationUnitStructure() throws JavaModelException {
		validate(selection.isStructureKnown(), "The structure of the compilation unit is not known");
	}

	private void validateSomethingIsSelected() {
		validate(selection.isSomethingSelected(), "Something encapsulateable must be selected");
	}

	private void validate(final boolean value, final String message) {
		validator.validate(value, message);
	}

	public Set<IBinding> getExternalVariables() {
		return externalVariables;
	}
}
