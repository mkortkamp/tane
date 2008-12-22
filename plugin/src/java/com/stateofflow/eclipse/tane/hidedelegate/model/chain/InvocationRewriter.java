package com.stateofflow.eclipse.tane.hidedelegate.model.chain;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNode;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.Rewrite;

class InvocationRewriter {
    private void copyInvocationArguments(final Chain chain, final MethodInvocation replacement) {
        for (final ChainNode node : chain) {
            node.copyInvocationArguments(replacement);
        }
    }

    public void rewrite(final Chain chain, final Rewrite rewrite, final String methodName) throws CoreException {
        chain.rewrite(createReplacementMethodInvocation(chain, methodName), rewrite);
    }

    private MethodInvocation createReplacementMethodInvocation(final Chain chain, final String methodName) {
        final MethodInvocation replacement = chain.createReplacementMethodInvocation(methodName);
        copyInvocationArguments(chain, replacement);
        return replacement;
    }
}
