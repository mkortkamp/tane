package com.stateofflow.eclipse.tane.hidedelegate.model.chain;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNode;
import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNodeFactory;

class ChainIterator implements Iterator<ChainNode> {
    private final ASTNode root;
    private ASTNode current;

    public ChainIterator(final ASTNode origin, final ASTNode root) {
        this.current = origin;
        this.root = root;
    }

    public boolean hasNext() {
        return current != null;
    }

    public ChainNode next() {
        final ChainNode node = new ChainNodeFactory().createNode(current);
        current = current == root ? null : current.getParent();
        return node;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
