package com.stateofflow.eclipse.tane.hidedelegate.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.stateofflow.eclipse.tane.hidedelegate.model.HideDelegateRefactoring;
import com.stateofflow.eclipse.tane.hidedelegate.model.Scope;
import com.stateofflow.eclipse.tane.util.JavaIdentifierValidator;

class MethodDetailsCollectionPage extends UserInputWizardPage {
    private StringFieldEditor methodNameEditor;

    public MethodDetailsCollectionPage() {
        super("Method Details");
    }

    public void createControl(final Composite parent) {
        final Composite composite = createGridComposite(parent, 1, SWT.NULL);
        setControl(composite);

        initializeMethodNameEditor(composite);
        initializeScope(composite);
        validateAndSetUpRefactoring();
    }

    private Composite createGridComposite(final Composite parent, final int columns, final int style) {
        final Composite composite = new Composite(parent, style);

        final GridLayout layout = new GridLayout(columns, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return composite;
    }

    private StringFieldEditor createStringFieldEditor(final Composite composite, final String label) {
        final StringFieldEditor editor = new StringFieldEditor("", label + ":", composite);
        editor.setPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent event) {
                validateAndSetUpRefactoring();
            }
        });
        return editor;
    }

    @Override
    public HideDelegateRefactoring getRefactoring() {
        return (HideDelegateRefactoring) super.getRefactoring();
    }

    private void initializeMethodNameEditor(final Composite composite) {
        methodNameEditor = createStringFieldEditor(createGridComposite(composite, 1, SWT.NONE), "Method Name");
        methodNameEditor.setStringValue(getRefactoring().getChain().getSuggestedMethodName());
    }

    private void initializeScope(final Composite composite) {
        setScope(Scope.WORKSPACE);
        final Group group = new Group(composite, SWT.NONE);
        group.setText("Scope");
        final GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        group.setLayoutData(gd);

        final GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = true;
        layout.numColumns = 3;
        group.setLayout(layout);

        createScopeRadioButton(group, "Workspace", true, Scope.WORKSPACE);
        createScopeRadioButton(group, "Project", false, Scope.PROJECT);
        createScopeRadioButton(group, "Compilation Unit", false, Scope.COMPILATION_UNIT);
    }

    private Button createScopeRadioButton(final Group group, final String text, final boolean selection, final Scope scope) {
        final Button button = new Button(group, SWT.RADIO);
        button.setText(text);
        button.setSelection(selection);
        button.setEnabled(true);
        button.setData(scope);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (button.getSelection()) {
                    setScope((Scope) button.getData());
                }
            }
        });
        return button;
    }

    private void updatePage(final RefactoringStatus status) {
        setPageComplete(!status.hasError());
        final int severity = status.getSeverity();
        setMessage(severity, status.getMessageMatchingSeverity(severity));
    }

    private void setMessage(final int severity, final String message) {
        if (severity >= RefactoringStatus.INFO) {
            setMessage(message, severity);
        } else {
            setMessage("", IMessageProvider.NONE);
        }
    }

    private void validateAndSetUpMethodName(final RefactoringStatus status) {
        if (!isValidIdentifer()) {
            status.addFatalError("Invalid method name.");
        } else {
            getRefactoring().setMethodName(getMethodName());
        }
    }

    private String getMethodName() {
        return methodNameEditor.getStringValue();
    }

    private boolean isValidIdentifer() {
        return new JavaIdentifierValidator().validate(getMethodName());
    }

    private void validateAndSetUpRefactoring() {
        final RefactoringStatus status = new RefactoringStatus();
        validateAndSetUpMethodName(status);
        updatePage(status);
    }

    private void setScope(final Scope scope) {
        getRefactoring().setScope(scope);
    }
}
