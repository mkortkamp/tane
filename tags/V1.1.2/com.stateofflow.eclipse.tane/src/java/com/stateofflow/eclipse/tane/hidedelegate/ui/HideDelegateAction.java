package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;

import com.stateofflow.eclipse.tane.ui.TaneEditorAction;

public class HideDelegateAction extends TaneEditorAction {
	@Override
	public void doRun(final ICompilationUnit compilationUnit, final ITextSelection selection) {
	    new HideDelegateLauncher().launch(compilationUnit, selection, getShell());
	}
}
