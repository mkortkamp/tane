package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;


public class ExceptionSetCopier {
    public void copy(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final Set<ITypeBinding> exceptions) {
        copyExceptions(rewrite, targetMethodDeclaration, exceptions);
    }

    @SuppressWarnings("unchecked")
    private boolean addExceptionCopy(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final ITypeBinding exception) {
        return targetMethodDeclaration.thrownExceptions().add(createExceptionCopy(rewrite, targetMethodDeclaration, exception));
    }

    private void copyExceptions(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final Set<ITypeBinding> exceptions) {
        for (final ITypeBinding exception : exceptions) {
            addExceptionCopy(rewrite, targetMethodDeclaration, exception);
        }
    }

    private Name createExceptionCopy(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final ITypeBinding exception) {
        return targetMethodDeclaration.getAST().newName(rewrite.addImportReturningString(exception));
    }
}
