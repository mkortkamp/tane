package com.stateofflow.eclipse.tane.util.ast;

import static com.stateofflow.eclipse.tane.util.ast.ASTUtils.findNode;

import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jface.text.TextSelection;

import com.stateofflow.eclipse.tane.Activator;
import com.stateofflow.eclipse.tane.flowanalysis.Analyser;
import com.stateofflow.eclipse.tane.flowanalysis.FreeVariableAnalyser;
import com.stateofflow.eclipse.tane.flowanalysis.UnhandledCheckedExceptionAnalyser;
import com.stateofflow.eclipse.tane.flowanalysis.UnhandledExceptionAnalyser;
import com.stateofflow.eclipse.tane.util.Range;
import com.stateofflow.eclipse.tane.validation.Validatable;
import com.stateofflow.eclipse.tane.validation.Validator;

public class ASTSelection implements Validatable {
    private final TextSelection selection;
    private final ICompilationUnit compilationUnit;
    private CompilationUnit parsedCompilationUnit;

    public ASTSelection(final ICompilationUnit compilationUnit, final TextSelection selection) {
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
		return analyse(new FreeVariableAnalyser());
	}
	
	public Set<ITypeBinding> getUnhandledExceptions() {
		return analyse(new UnhandledCheckedExceptionAnalyser());
	}
	
	private <T> Set<T> analyse(Analyser<T> analyser) {
		return analyser.analyse(getSelectedRange(), getNodeEncompassingWholeSelection());
	}
	
	private Range getSelectedRange() {
		return new Range(selection.getOffset(), selection.getLength());
	}
}
