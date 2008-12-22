package com.stateofflow.eclipse.tane.util;

import static com.stateofflow.eclipse.tane.util.ASTUtils.*;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.TextSelection;

public class Selection {
    private final ICompilationUnit compilationUnit;
    private final TextSelection selection;
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
}
