package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;


public class NewMethodBodyBuilder {
    @SuppressWarnings("unchecked")
    public Block build(final Rewrite rewrite, final Expression expression, final boolean voidReturnType) {
        final Block body = rewrite.newBlock();
        body.statements().add(createStatement(rewrite, expression, voidReturnType));
        return body;
    }

    private Statement createStatement(final Rewrite rewrite, final Expression copy, final boolean voidReturnType) {
        if (voidReturnType) {
            return rewrite.newExpressionStatement(copy);
        }

        final ReturnStatement returnStatement = rewrite.newReturnStatement();
        returnStatement.setExpression(copy);
        return returnStatement;
    }
}
