package com.stateofflow.eclipse.tane.hidedelegate.model.validation.initial;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.Expression;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.Chain;
import com.stateofflow.eclipse.tane.util.ast.Selection;
import com.stateofflow.eclipse.tane.validation.Validator;

public class InitialConditionValidator {
    public boolean validate(final Selection selection, Validator validator) throws CoreException {
    	return validator.validate(selection) //
			&& validator.validate(new RootNodeValidateable(selection.getNodeEncompassingWholeSelection())) //
			&& validator.validate(new NonRootNodeValidateable(selection.getParentOfNodeAtStartOfSelection(), selection.getNodeEncompassingWholeSelection())) //
			&& validator.validate(new Chain((Expression) selection.getParentOfNodeAtStartOfSelection(), (Expression) selection.getNodeEncompassingWholeSelection()));
    }
}
