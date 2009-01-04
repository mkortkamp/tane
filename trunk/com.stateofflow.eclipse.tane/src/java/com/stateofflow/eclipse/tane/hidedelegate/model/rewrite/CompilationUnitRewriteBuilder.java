package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import static com.stateofflow.eclipse.tane.util.ASTUtils.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.search.SearchMatch;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;
import com.stateofflow.eclipse.tane.rewrite.visibility.VisibilityChangeException;

class CompilationUnitRewriteBuilder {
    private final Map<ICompilationUnit, Set<SearchMatch>> chainOriginReferencesByCompilationUnit;
    private final Map<ICompilationUnit, Set<SearchMatch>> potentialVisibilityUpdatesByCompilationUnit;
    private final Refactor refactor;

    public CompilationUnitRewriteBuilder(final Map<ICompilationUnit, Set<SearchMatch>> chainOriginReferencesByCompilationUnit, final Map<ICompilationUnit, Set<SearchMatch>> potentialVisibilityUpdatesByCompilationUnit, final Refactor refactor) {
        this.chainOriginReferencesByCompilationUnit = chainOriginReferencesByCompilationUnit;
        this.potentialVisibilityUpdatesByCompilationUnit = potentialVisibilityUpdatesByCompilationUnit;
        this.refactor = refactor;
    }

    public Rewrite createRewrite(final ICompilationUnit source, final CompilationUnit ast) throws CoreException, JavaModelException, VisibilityChangeException {
        final Rewrite rewrite = new Rewrite(source, ast);
        rewriteInvocations(ast, getMatches(source, chainOriginReferencesByCompilationUnit), rewrite);
        rewriteVisibilities(ast, getMatches(source, potentialVisibilityUpdatesByCompilationUnit), rewrite);
        return rewrite;
    }

    private Set<SearchMatch> getMatches(final ICompilationUnit compilationUnit, final Map<ICompilationUnit, Set<SearchMatch>> matchesByCompilationUnit) {
        return matchesByCompilationUnit.containsKey(compilationUnit) ? matchesByCompilationUnit.get(compilationUnit) : Collections.<SearchMatch> emptySet();
    }

    private void rewriteInvocations(final CompilationUnit ast, final Set<SearchMatch> matches, final Rewrite rewrite) throws CoreException {
        for (final SearchMatch match : matches) {
            refactor.rewriteInvocation(rewrite, findNode(ast, match.getOffset(), 0).getParent());
        }
    }

    private void rewriteVisibilities(final CompilationUnit ast, final Set<SearchMatch> matches, final Rewrite rewrite) throws JavaModelException, VisibilityChangeException {
        for (final SearchMatch match : matches) {
            refactor.rewriteVisibility(rewrite, getVisibilityModifiable(findNode(ast, match.getOffset(), match.getLength()).getParent()));
        }
    }

    private BodyDeclaration getVisibilityModifiable(final ASTNode node) {
        if (node instanceof BodyDeclaration) {
            return (BodyDeclaration) node;
        }
        if (node instanceof VariableDeclarationFragment) {
            return (BodyDeclaration) node.getParent();
        }
        throw new IllegalArgumentException("Node type not handleable: " + node.getNodeType());
    }
}
