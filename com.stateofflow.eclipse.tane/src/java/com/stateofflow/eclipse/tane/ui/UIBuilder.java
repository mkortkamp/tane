package com.stateofflow.eclipse.tane.ui;

import java.util.List;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class UIBuilder {    
    public static Composite gridComposite(final Composite parent, final int columns, final int style) {
        final Composite composite = new Composite(parent, style);
        
        final GridLayout layout = new GridLayout(columns, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return composite;
    }

    public static StringFieldEditor stringEditor(final Composite composite, final String label, IPropertyChangeListener listener) {
        final StringFieldEditor editor = new StringFieldEditor("", label + ":", composite);
        editor.setPropertyChangeListener(listener);
        return editor;
    }
    
    public static Label label(final Composite composite, final String text) {
        Label label = new Label(composite, SWT.NULL);
        label.setText(text);
        return label;
    }

    public static org.eclipse.swt.widgets.List multiList(Composite composite, String label, List<String> items, SelectionListener listener) {
        return list(composite, label, items, listener, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    }

    public static org.eclipse.swt.widgets.List singleList(Composite composite, String label, List<String> items, SelectionListener listener) {
        return list(composite, label, items, listener, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    }

    public static org.eclipse.swt.widgets.List list(Composite composite, String label, List<String> items, SelectionListener listener, int style) {
        label(composite, label + ":");
        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(composite, style);
        for (String item : items) {
            list.add(item);
        }
        list.addSelectionListener(listener);
        return list;
    }
}
