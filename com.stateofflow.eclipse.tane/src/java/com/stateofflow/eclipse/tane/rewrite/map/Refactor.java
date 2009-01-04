package com.stateofflow.eclipse.tane.rewrite.map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;
import com.stateofflow.eclipse.tane.rewrite.visibility.VisibilityChangeException;

public class Refactor {
    private final String methodName;
    private final Rewriter rewriter;

    public Refactor(final String methodName, final Rewriter rewriter) {
        this.methodName = methodName;
        this.rewriter = rewriter;
    }

    public void writeNewMethod(final RewriteMap rewrites) throws CoreException {
        rewriter.writeNewMethod(rewrites, methodName);
    }

    public void rewriteInvocation(final Rewrite rewrite, final ASTNode origin) throws CoreException {
        rewriter.rewriteInvocation(rewrite, origin, methodName);
    }

    public void rewriteVisibility(final Rewrite rewrite, final BodyDeclaration bodyDeclaration) throws JavaModelException, VisibilityChangeException {
        rewriter.rewriteVisibility(rewrite, bodyDeclaration);
    }
}