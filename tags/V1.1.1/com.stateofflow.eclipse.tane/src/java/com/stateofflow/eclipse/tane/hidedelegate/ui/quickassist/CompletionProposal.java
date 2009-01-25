package com.stateofflow.eclipse.tane.hidedelegate.ui.quickassist;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.stateofflow.eclipse.tane.Activator;
import com.stateofflow.eclipse.tane.hidedelegate.ui.HideDelegateLauncher;

final class CompletionProposal implements IJavaCompletionProposal {
	private final ITextSelection textSelection;
	private final ICompilationUnit compilationUnit;

	CompletionProposal(ITextSelection textSelection, ICompilationUnit compilationUnit) {
		this.textSelection = textSelection;
		this.compilationUnit = compilationUnit;
	}

	public int getRelevance() {
		return 0;
	}

	public void apply(IDocument document) {
	    new HideDelegateLauncher().launch(compilationUnit, textSelection, Activator.getShell());
	}

	public String getAdditionalProposalInfo() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return "Hide Delegate";
	}

	public Image getImage() {
		return null;
	}

	public Point getSelection(IDocument document) {
		return null;
	}
}