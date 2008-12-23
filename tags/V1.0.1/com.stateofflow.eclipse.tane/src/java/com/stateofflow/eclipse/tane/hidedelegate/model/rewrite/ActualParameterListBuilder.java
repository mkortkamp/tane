package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.MethodInvocation;

class ActualParameterListBuilder extends ASTMatcher {
    private final AST ast;
    private int parameterNumber;

    public ActualParameterListBuilder(final AST ast) {
        this.ast = ast;
    }

    @Override
    public boolean match(final MethodInvocation originalInvocation, final Object duplicate) {
        final MethodInvocation duplicateInvocation = (MethodInvocation) duplicate;
        safeSubtreeMatch(originalInvocation.getExpression(), duplicateInvocation.getExpression());
        rebuildParameters(duplicateInvocation);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void rebuildParameters(final MethodInvocation duplicate) {
        final int numberOfArguments = duplicate.arguments().size();
        duplicate.arguments().clear();
        for (int i = 0; i < numberOfArguments; i++, parameterNumber++) {
            duplicate.arguments().add(ast.newSimpleName("p" + parameterNumber));
        }
    }
}