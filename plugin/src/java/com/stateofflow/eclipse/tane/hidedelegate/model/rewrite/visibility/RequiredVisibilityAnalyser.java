package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.visibility;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

class RequiredVisibilityAnalyser {
    private final ICompilationUnit compilationUnit;
    private final IType visibilityRequiredFrom;
    private final BodyDeclaration declaration;

    public RequiredVisibilityAnalyser(final ICompilationUnit compilationUnit, final IType visibilityRequiredFrom, final BodyDeclaration declaration) {
        this.compilationUnit = compilationUnit;
        this.visibilityRequiredFrom = visibilityRequiredFrom;
        this.declaration = declaration;
    }

    public ModifierKeyword getRequiredVisibility() throws JavaModelException {
        return isPrivateVisibilitySufficient() ? ModifierKeyword.PRIVATE_KEYWORD : getRequiredVisibilityGreaterThanPrivate();
    }

    private ModifierKeyword getRequiredVisibilityGreaterThanPrivate() throws JavaModelException {
        return isPackageVisibilitySufficient() ? null : getRequiredVisibilityGreaterThanPackage();
    }

    private ModifierKeyword getRequiredVisibilityGreaterThanPackage() throws JavaModelException {
        return isProtectedVisibilitySufficient() ? ModifierKeyword.PROTECTED_KEYWORD : ModifierKeyword.PUBLIC_KEYWORD;
    }

    private boolean isPrivateVisibilitySufficient() {
        return isDeclarationInType() || compilationUnit.equals(visibilityRequiredFrom.getCompilationUnit());
    }

    private boolean isPackageVisibilitySufficient() {
        return compilationUnit.getParent().equals(visibilityRequiredFrom.getCompilationUnit().getParent());
    }

    private boolean isProtectedVisibilitySufficient() throws JavaModelException {
        final ITypeHierarchy hierarchy = visibilityRequiredFrom.newSupertypeHierarchy(new NullProgressMonitor());
        final IType[] types = hierarchy.getSupertypes(visibilityRequiredFrom);
        for (final IType type : types) {
            if (type.equals(getDeclaringType(declaration))) {
                return true;
            }
        }

        return false;
    }

    private boolean isDeclarationInType() {
        return getDeclaringType(declaration).equals(visibilityRequiredFrom);
    }

    private IType getDeclaringType(final ASTNode node) {
        return node.getNodeType() == ASTNode.TYPE_DECLARATION ? (IType) ((TypeDeclaration) node).resolveBinding().getJavaElement() : getDeclaringType(node.getParent());
    }
}
