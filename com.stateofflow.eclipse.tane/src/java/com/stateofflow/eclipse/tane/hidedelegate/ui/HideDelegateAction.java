package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.stateofflow.eclipse.tane.hidedelegate.model.HideDelegateRefactoring;

public class HideDelegateAction implements IEditorActionDelegate {
	private ICompilationUnit compilationUnit;
	private TextSelection selection;

	private Shell getShell() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		return window.getShell();
	}

	public void run(final IAction action) {
		if (compilationUnit == null || selection == null) {
			return;
		}
		final HideDelegateRefactoring refactoring = new HideDelegateRefactoring(compilationUnit, selection);
		final RefactoringWizard wizard = new HideDelegateRefactoringWizard(refactoring);
		try {
			new RefactoringWizardOpenOperation(wizard).run(getShell(), "Hide Delegate");
		} catch (final InterruptedException e) {
			// Canceled
		}
	}

	public void selectionChanged(final IAction action, final ISelection newSelection) {
		this.selection = newSelection instanceof TextSelection ? (TextSelection) newSelection : null;
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
