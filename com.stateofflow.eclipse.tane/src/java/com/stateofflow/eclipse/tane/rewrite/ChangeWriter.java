package com.stateofflow.eclipse.tane.rewrite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

class ChangeWriter {
    public void addChange(final CompositeChange changes, final ICompilationUnit compilationUnit, final ASTRewrite astRewrite, final ImportRewrite importRewrite) throws CoreException {
        addChange(changes, compilationUnit, createEdit(astRewrite, importRewrite));
    }

    private void addChange(final CompositeChange changes, final ICompilationUnit compilationUnit, final MultiTextEdit edit) {
        if (edit.hasChildren()) {
            changes.add(createChange(compilationUnit, edit));
        }
    }

    private MultiTextEdit createEdit(final ASTRewrite astRewrite, final ImportRewrite importRewrite) throws CoreException, JavaModelException {
        final MultiTextEdit edit = new MultiTextEdit();
        addEdit(edit, importRewrite.rewriteImports(new NullProgressMonitor()));
        addEdit(edit, astRewrite.rewriteAST());
        return edit;
    }

    private TextFileChange createChange(final ICompilationUnit compilationUnit, final MultiTextEdit edit) {
        final TextFileChange change = new TextFileChange("", (IFile) compilationUnit.getResource());
        change.setTextType("java");
        change.setEdit(edit);
        return change;
    }

    private void addEdit(final MultiTextEdit parent, final TextEdit child) {
        if (child.hasChildren()) {
            parent.addChild(child);
        }
    }
}
