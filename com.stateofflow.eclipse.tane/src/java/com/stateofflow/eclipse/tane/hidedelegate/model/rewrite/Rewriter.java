package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;
import com.stateofflow.eclipse.tane.rewrite.visibility.VisibilityChangeException;

public interface Rewriter {
    void writeNewMethod(final RewriteMap rewrites, final String methodName) throws CoreException;

    void rewriteInvocation(Rewrite rewrite, ASTNode origin, String methodName) throws CoreException;

    void rewriteVisibility(Rewrite rewrite, BodyDeclaration bodyDeclaration) throws JavaModelException, VisibilityChangeException;
}
