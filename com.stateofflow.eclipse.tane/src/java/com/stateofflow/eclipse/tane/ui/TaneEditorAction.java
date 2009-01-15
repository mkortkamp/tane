package com.stateofflow.eclipse.tane.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class TaneEditorAction implements IEditorActionDelegate {

	private ICompilationUnit compilationUnit;
	private ITextSelection selection;

	public final void run(IAction action) {
		if (compilationUnit == null || selection == null) {
			return;
		}
		doRun(compilationUnit, selection);
	}

	protected abstract void doRun(ICompilationUnit unit, ITextSelection textSelection);

	protected Shell getShell() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		return window.getShell();
	}

	public void selectionChanged(final IAction action, final ISelection newSelection) {
		this.selection = newSelection instanceof ITextSelection ? (ITextSelection) newSelection : null;
	}

	public void setActiveEditor(final IAction action, final IEditorPart editor) {
		compilationUnit = editor == null ? null : getCompilationUnit(editor.getEditorInput());
	}

	private ICompilationUnit getCompilationUnit(final IEditorInput editorInput) {
		return editorInput == null ? null : getCompilationUnit((IFile) editorInput.getAdapter(IFile.class));
	}

	private ICompilationUnit getCompilationUnit(final IFile file) {
		return file == null ? null : JavaCore.createCompilationUnitFrom(file);
	}

}