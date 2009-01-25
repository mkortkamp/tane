package com.stateofflow.eclipse.tane.hidedelegate.ui.quickassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import com.stateofflow.eclipse.tane.hidedelegate.model.validation.initial.InitialConditionValidator;
import com.stateofflow.eclipse.tane.util.ast.ASTSelection;
import com.stateofflow.eclipse.tane.util.ast.ASTUtils;
import com.stateofflow.eclipse.tane.validation.RefactoringStatusValidator;
import com.stateofflow.eclipse.tane.validation.Validator;

public class QuickAssistProcessor implements IQuickAssistProcessor {
	public IJavaCompletionProposal[] getAssists(final IInvocationContext context, final IProblemLocation[] locations) throws CoreException {
		final ITextSelection textSelection = createTextSelection(context);
		return canRefactor(context, textSelection) ? createProposals(context, textSelection) : null;
	}

	private ITextSelection createTextSelection(final IInvocationContext context) throws CoreException {
		return context.getSelectionLength() == 0
		    ? createSelectionToEndOfExpression(context)
		    : createPreciseTextSelection(context);
	}

    private ITextSelection createPreciseTextSelection(final IInvocationContext context) {
        return new TextSelection(context.getSelectionOffset(), context.getSelectionLength());
    }

	private ITextSelection createSelectionToEndOfExpression(IInvocationContext context) throws CoreException {
		ASTNode node = findAppropriateASTNode(context).getParent();
		ITextSelection lastRefactorableSelection = null;
		ITextSelection selection = createSelectionFromContextStartToEndOfNode(context, node);
		while (canRefactor(context, selection)) {
		    lastRefactorableSelection = selection;
		    node = node.getParent();
		    selection = createSelectionFromContextStartToEndOfNode(context, node);
		}
		return lastRefactorableSelection == null ? createPreciseTextSelection(context) : lastRefactorableSelection;
	}

    private ASTNode findAppropriateASTNode(IInvocationContext context) {
        ASTNode node = ASTUtils.findNode(context.getASTRoot(), context.getSelectionOffset());
		if (node.getNodeType() == ASTNode.SIMPLE_NAME) {
		    node = node.getParent();
		}
        return node;
    }

    private TextSelection createSelectionFromContextStartToEndOfNode(IInvocationContext context, ASTNode node) {
        return new TextSelection(context.getSelectionOffset(), node.getStartPosition() + node.getLength() - context.getSelectionOffset());
    }

	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return false;
	}
	
	private boolean canRefactor(IInvocationContext context, ITextSelection textSelection) throws CoreException {
		final ASTSelection selection = new ASTSelection(context.getCompilationUnit(), textSelection);
		final Validator validator = new RefactoringStatusValidator();
		new InitialConditionValidator().validate(selection, validator);
		return validator.isOK();
	}
	
	private IJavaCompletionProposal[] createProposals(final IInvocationContext context, final ITextSelection textSelection) {
		return new IJavaCompletionProposal[] {
			new CompletionProposal(textSelection, context.getCompilationUnit())
		};
	}
}
