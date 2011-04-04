package com.stateofflow.eclipse.tane.util.ast;

import static org.eclipse.jdt.internal.corext.dom.NodeFinder.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;

public class ASTUtils {
    public static ASTNode findNode(final CompilationUnit root, final int offset) {
        return findNode(root, offset, 0);
    }

    public static ASTNode findNode(final CompilationUnit root, final int offset, final int length) {
        return perform(root, offset, length);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T getAncestor(ASTNode node, final Class<T> parentClass) {
    	return node == null || parentClass.isInstance(node) ? (T) node : getAncestor(node.getParent(), parentClass);
    }

    public static ASTNode getDeclarationForType(final CompilationUnit root, final IType type) throws JavaModelException {
        final Name result = (Name) findNode(root, type.getNameRange().getOffset());
        return type.isAnonymous() ? getAncestor(result, AnonymousClassDeclaration.class) : getAncestor(result, AbstractTypeDeclaration.class);
    }

    public static ChildListPropertyDescriptor getBodyDeclarationPropertyForType(final CompilationUnit root, final IType type) throws JavaModelException {
        final ASTNode result = getDeclarationForType(root, type);
        if (result instanceof AbstractTypeDeclaration) {
            return ((AbstractTypeDeclaration) result).getBodyDeclarationsProperty();
        } else if (result instanceof AnonymousClassDeclaration) {
            return AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY;
        }

        Assert.isTrue(false);
        return null;
    }

	public static ASTNode findLowestCommonAncestor(ASTNode node1, ASTNode node2) {
		for (ASTNode n1 = node1 ; n1 != null ; n1 = n1.getParent()) {
			for (ASTNode n2 = node2 ; n2 != null ; n2 = n2.getParent()) {
				if (n1 == n2) {
					return n1;
				}
			}
		}
		return null;
	}

	public static ASTNode findLowestCommonAncestor(Set<? extends ASTNode> nodes) {
		ASTNode first = nodes.iterator().next();
		if (nodes.size() == 1) {
			return first;
		}
		Set<ASTNode> rest = new HashSet<ASTNode>(nodes);
		rest.remove(first);
		return findLowestCommonAncestor(first, findLowestCommonAncestor(rest));
	}

	public static ASTNode findFirst(Set<? extends ASTNode> nodes) {
		Iterator<? extends ASTNode> iterator = nodes.iterator();
		ASTNode first = iterator.next();
		while (iterator.hasNext()) {
			ASTNode contender = iterator.next();
			if (first.getStartPosition() > contender.getStartPosition()) {
				first = contender;
			}
		}
		return first;
	}
}
