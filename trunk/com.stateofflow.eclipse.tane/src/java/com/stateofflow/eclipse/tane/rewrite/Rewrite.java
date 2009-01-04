package com.stateofflow.eclipse.tane.rewrite;

import static com.stateofflow.eclipse.tane.util.ASTUtils.*;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import com.stateofflow.eclipse.tane.rewrite.visibility.VisibilityChangeException;
import com.stateofflow.eclipse.tane.rewrite.visibility.VisibilityRewrite;

public class Rewrite {
    private final ICompilationUnit compilationUnit;
    private final ASTRewrite astRewrite;
    private final ImportRewrite importRewrite;
    private final CompilationUnit parsedCompilationUnit;

    public Rewrite(final ICompilationUnit compilationUnit, final CompilationUnit parsedCompilationUnit) {
        this.compilationUnit = compilationUnit;
        this.parsedCompilationUnit = parsedCompilationUnit;
        this.astRewrite = ASTRewrite.create(parsedCompilationUnit.getAST());
        this.importRewrite = ImportRewrite.create(parsedCompilationUnit, true);
    }

    public String addImportReturningString(final ITypeBinding typeBinding) {
        return importRewrite.addImport(typeBinding);
    }

    public Type addImportReturningType(final ITypeBinding typeBinding) {
        return importRewrite.addImport(typeBinding, getAST());
    }

    public void appendMethodDeclaration(final MethodDeclaration methodDeclaration, final IType type) throws JavaModelException {
        astRewrite.getListRewrite(getDeclarationForType(parsedCompilationUnit, type), getBodyDeclarationPropertyForType(parsedCompilationUnit, type)).insertLast(methodDeclaration, null);
    }

    public AST getAST() {
        return parsedCompilationUnit.getAST();
    }

    public void replace(final ASTNode node, final ASTNode replacement) {
        astRewrite.replace(node, replacement, null);
    }

    public void addChange(final CompositeChange changes) throws CoreException {
        new ChangeWriter().addChange(changes, compilationUnit, astRewrite, importRewrite);
    }

    public void rewriteVisibility(final VisibilityRewrite visibilityRewrite) throws JavaModelException, VisibilityChangeException {
        visibilityRewrite.rewriteVisibility(compilationUnit, astRewrite);
    }

    public SimpleName newSimpleName(final String identifier) {
        return getAST().newSimpleName(identifier);
    }

    public Block newBlock() {
        return getAST().newBlock();
    }

    public ExpressionStatement newExpressionStatement(final Expression p0) {
        return getAST().newExpressionStatement(p0);
    }

    public ReturnStatement newReturnStatement() {
        return getAST().newReturnStatement();
    }

    public Modifier newModifier(final ModifierKeyword keyword) {
        return getAST().newModifier(keyword);
    }

    public MethodDeclaration newMethodDeclaration() {
        return getAST().newMethodDeclaration();
    }

    @SuppressWarnings("unchecked")
    public <T extends ASTNode> T copySubtree(final T subtree) {
        return (T) ASTNode.copySubtree(getAST(), subtree);
    }

	public void copyExceptions(Set<ITypeBinding> exceptions, MethodDeclaration methodDeclaration) {
        new ExceptionSetCopier().copy(this, methodDeclaration, exceptions);
	}

	public Name newName(ITypeBinding type) {
		return getAST().newName(addImportReturningString(type));
	}
}