package com.stateofflow.eclipse.tane.rewrite;

import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

class ExceptionSetCopier {
    public void copy(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final Set<ITypeBinding> exceptions) {
        for (final ITypeBinding exception : exceptions) {
		    addExceptionCopy(rewrite, targetMethodDeclaration, exception);
		}
    }

    @SuppressWarnings("unchecked")
    private boolean addExceptionCopy(final Rewrite rewrite, final MethodDeclaration targetMethodDeclaration, final ITypeBinding exception) {
        return targetMethodDeclaration.thrownExceptions().add(rewrite.newName(exception));
    }
}
