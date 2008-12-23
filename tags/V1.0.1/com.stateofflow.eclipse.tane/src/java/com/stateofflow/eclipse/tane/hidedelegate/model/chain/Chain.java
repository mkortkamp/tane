package com.stateofflow.eclipse.tane.hidedelegate.model.chain;

import static java.util.Arrays.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchMatch;

import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNode;
import com.stateofflow.eclipse.tane.hidedelegate.model.chain.node.ChainNodeFactory;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.ASTTruncater;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.Rewrite;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.map.Refactor;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.map.RewriteMap;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.map.RewriteMapBuilder;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.map.Rewriter;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.visibility.VisibilityChangeException;
import com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.visibility.VisibilityRewrite;
import com.stateofflow.eclipse.tane.util.CompilationUnitSearchMatchGrouper;
import com.stateofflow.eclipse.tane.util.MemberFinder;
import com.stateofflow.eclipse.tane.util.TypeSetMinimizer;

public class Chain implements Iterable<ChainNode>, Rewriter {
    private final Expression origin;
    private final Expression root;

    public Chain(final Expression origin, final Expression root) {
        assertNodeValid(origin, "origin");
        assertNodeValid(root, "root");
        this.origin = origin;
        this.root = root;
    }

    private void assertNodeValid(final ASTNode node, final String nodeName) {
        Assert.isTrue(node.getNodeType() == ASTNode.FIELD_ACCESS || node.getNodeType() == ASTNode.METHOD_INVOCATION, nodeName + " : " + node.getNodeType());
    }

    public Expression copyExpression(final Rewrite rewrite) {
        final Expression copy = rewrite.copySubtree(root);
        new ASTTruncater(origin).safeSubtreeMatch(root, copy);
        rewrite.buildActualParameterList(root, copy);
        return copy;
    }

    public Chain createImage(final ASTNode imageOrigin) {
        ASTNode current = imageOrigin;
        ASTNode imageRoot = null;
        for (final ChainNode node : this) {
            imageRoot = current;
            if (current != imageOrigin && !node.matches(current)) {
                return null;
            }
            current = current.getParent();
        }
        return new Chain((Expression) imageOrigin, (Expression) imageRoot);
    }

    private ChainNode createNode(final ASTNode node) {
        return new ChainNodeFactory().createNode(node);
    }

    MethodInvocation createReplacementMethodInvocation(final String methodName) {
        final AST ast = root.getAST();
        final MethodInvocation replacement = ast.newMethodInvocation();
        replacement.setName(ast.newSimpleName(methodName));
        createNode(origin).copyExpression(replacement);
        return replacement;
    }

    public IType getDeclaringTypeOfOrigin() {
        return (IType) createNode(origin).getDeclaringClassOfMember().getJavaElement();
    }

    public ITypeBinding getReturnType() {
        return createNode(root).getTypeOfMember();
    }

    private ICompilationUnit getOriginCompilationUnit() {
        return getDeclaringTypeOfOrigin().getCompilationUnit();
    }

    private IMember getOriginJavaElement() {
        return createNode(origin).getJavaElementOfMember();
    }

    public int getOriginModifiers() {
        return createNode(origin).getModifiersForMember();
    }

    boolean isVoidExpressionType() {
        final ITypeBinding returnType = getReturnType();
        return returnType.isPrimitive() && returnType.getName().equals("void");
    }

    public Iterator<ChainNode> iterator() {
        return new ChainIterator(origin, root);
    }

    void rewrite(final MethodInvocation replacement, final Rewrite rewrite) {
        rewrite.replace(root, replacement);
    }

    public void writeNewMethod(final RewriteMap rewrites, final String methodName) throws CoreException {
        final Rewrite rewrite = rewrites.get(getOriginCompilationUnit());
        final MethodDeclaration newMethodDeclaration = new NewMethodDeclarationBuilder(rewrite, this, methodName).build();
        rewrite.appendMethodDeclaration(newMethodDeclaration, getDeclaringTypeOfOrigin());
    }

    private int size() {
        int count = 0;
        for (final ChainNode node : this) {
            count++;
        }
        return count;
    }

    private Map<ICompilationUnit, Set<SearchMatch>> getReferencesByCompilationUnit(final MemberFinder memberFinder, final SubMonitor progressMonitor) throws CoreException {
        return getCompilationUnitGroups(memberFinder.getMatches(getOriginJavaElement(), IJavaSearchConstants.REFERENCES, progressMonitor));
    }

    private Map<ICompilationUnit, Set<SearchMatch>> getPotentialVisibilityUpdatesByCompilationUnit(final MemberFinder memberFinder, final SubMonitor progressMonitor) throws CoreException {
        progressMonitor.beginTask("Finding declarations", size());
        final Set<SearchMatch> matches = new HashSet<SearchMatch>();
        for (final ChainNode node : this) {
            matches.addAll(memberFinder.getMatches(node.getJavaElementOfMember(), IJavaSearchConstants.DECLARATIONS, progressMonitor.newChild(1)));
        }
        return getCompilationUnitGroups(matches);
    }

    private Map<ICompilationUnit, Set<SearchMatch>> getCompilationUnitGroups(final Set<SearchMatch> matches) {
        return new CompilationUnitSearchMatchGrouper().getMatchesByCompilationUnit(matches);
    }

    public RewriteMap createRewriteMap(final MemberFinder referencesMemberFinder, final MemberFinder visibilityUpdatesMemberFinder, final RewriteMapBuilder rewriteMapBuilder, final String methodName, final SubMonitor monitor) throws CoreException {
        monitor.beginTask("Creating rewrite map", 3);
        final Map<ICompilationUnit, Set<SearchMatch>> chainOriginReferencesByCompilationUnit = getReferencesByCompilationUnit(referencesMemberFinder, monitor.newChild(1));
        final Map<ICompilationUnit, Set<SearchMatch>> potentialVisibilityUpdatesByCompilationUnit = getPotentialVisibilityUpdatesByCompilationUnit(visibilityUpdatesMemberFinder, monitor.newChild(1));
        return rewriteMapBuilder.build(chainOriginReferencesByCompilationUnit, potentialVisibilityUpdatesByCompilationUnit, new Refactor(methodName, this), monitor.newChild(1));
    }

    public void rewriteInvocation(final Rewrite rewrite, final ASTNode origin, final String newMethodName) throws CoreException {
        final Chain rewriteChain = createImage(origin);
        if (rewriteChain != null) {
            rewriteChain.rewriteInvocation(rewrite, newMethodName);
        }
    }

    private void rewriteInvocation(final Rewrite rewrite, final String methodName) throws CoreException {
        new InvocationRewriter().rewrite(this, rewrite, methodName);
    }

    public void rewriteVisibility(final Rewrite rewrite, final BodyDeclaration bodyDeclaration) throws JavaModelException, VisibilityChangeException {
        rewrite.rewriteVisibility(new VisibilityRewrite(getDeclaringTypeOfOrigin(), bodyDeclaration));
    }

    public Set<ITypeBinding> getThrownExceptions() {
        return new TypeSetMinimizer().getMinimalSet(getAllThrownExceptions());
    }

    private Set<ITypeBinding> getAllThrownExceptions() {
        final HashSet<ITypeBinding> exceptions = new HashSet<ITypeBinding>();
        for (final ChainNode node : this) {
            exceptions.addAll(asList(node.getExceptionTypes()));
        }
        return exceptions;
    }
}
