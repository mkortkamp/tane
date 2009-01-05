package com.stateofflow.eclipse.tane.util;

import static com.stateofflow.eclipse.tane.util.ASTUtils.findNode;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jface.text.TextSelection;

import com.stateofflow.eclipse.tane.Activator;
import com.stateofflow.eclipse.tane.flowanalysis.FreeVariableAnalyser;
import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class Selection implements Validatable {
    private final TextSelection selection;
    private final ICompilationUnit compilationUnit;
    private CompilationUnit parsedCompilationUnit;

    public Selection(final ICompilationUnit compilationUnit, final TextSelection selection) {
        this.compilationUnit = compilationUnit;
        this.selection = selection;
    }

    public ASTNode getParentOfNodeAtStartOfSelection() {
        return findNode(getParsedCompilationUnit(), selection.getOffset()).getParent();
    }

    private CompilationUnit getParsedCompilationUnit() {
        if (parsedCompilationUnit == null) {
            parsedCompilationUnit = new Parser().parse(compilationUnit);
        }
        return parsedCompilationUnit;
    }

    public ASTNode getNodeEncompassingWholeSelection() {
        return findNode(getParsedCompilationUnit(), selection.getOffset(), selection.getLength());
    }

    public boolean isStructureKnown() throws JavaModelException {
        return compilationUnit.isStructureKnown();
    }

    public boolean isSomethingSelected() {
        return selection != null && selection.getLength() > 0;
    }

    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
    
    public void validate(Validator validator) {
    	try {
			validator.validate(isStructureKnown(), "The structure of the compilation unit is not known");
		} catch (JavaModelException e) {
			Activator.log("Caught a JavaModelException while trying to validate", e);
			validator.validate(false, "An error occurred while trying to validate. Please see the error log.");
		}
    	validator.validate(isSomethingSelected(), "Please select something to refactor");
    }

	public Set<IVariableBinding> getFreeVariables() {
		FreeVariableAnalyser analyser = new FreeVariableAnalyser(selection.getOffset(), selection.getLength());
		getNodeEncompassingWholeSelection().getParent().accept(analyser);
		return analyser.getFreeVariables();
	}
}
