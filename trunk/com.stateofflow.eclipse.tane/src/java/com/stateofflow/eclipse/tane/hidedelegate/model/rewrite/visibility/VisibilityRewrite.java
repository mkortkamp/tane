package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite.visibility;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.corext.dom.ModifierRewrite;

public class VisibilityRewrite {
	private final IType referencing;
	private final BodyDeclaration bodyDeclaration;

	public VisibilityRewrite(final IType referencing, final BodyDeclaration bodyDeclaration) {
		this.referencing = referencing;
		this.bodyDeclaration = bodyDeclaration;
	}

	private void changeVisibility(final ASTRewrite astRewrite, final BodyDeclaration declaration, final ModifierKeyword modifier)
			throws VisibilityChangeException {
		if (((CompilationUnit) declaration.getRoot()).getJavaElement().isReadOnly()) {
			throw new VisibilityChangeException();
		}
		ModifierRewrite.create(astRewrite, declaration).setVisibility(modifier == null ? Modifier.NONE : modifier.toFlagValue(), null);
	}

	public void rewriteVisibility(final ICompilationUnit compilationUnit, final ASTRewrite astRewrite) throws JavaModelException,
			VisibilityChangeException {
		final int modifiers = bodyDeclaration.getModifiers();
		if (Modifier.isPublic(modifiers)) {
			return;
		}

		final ModifierKeyword requiredVisibility = getRequiredVisibility(compilationUnit, referencing, bodyDeclaration);

		if (getVisibilityOrdinal(requiredVisibility) > getVisibilityOrdinal(modifiers)) {
			changeVisibility(astRewrite, bodyDeclaration, requiredVisibility);
		}
	}

	private int getVisibilityOrdinal(final int modifiers) {
		return Modifier.isPublic(modifiers) ? 4 : Modifier.isProtected(modifiers) ? 3 : Modifier.isPrivate(modifiers) ? 1 : 2;
	}

	private int getVisibilityOrdinal(final ModifierKeyword requiredVisibility) {
		return getVisibilityOrdinal(requiredVisibility.toFlagValue());
	}

	private ModifierKeyword getRequiredVisibility(final ICompilationUnit compilationUnit, final IType visibilityRequiredFrom,
			final BodyDeclaration declaration) throws JavaModelException {
		return new RequiredVisibilityAnalyser(compilationUnit, visibilityRequiredFrom, declaration).getRequiredVisibility();
	}
}
