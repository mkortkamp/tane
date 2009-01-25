package com.stateofflow.eclipse.tane;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "com.stateofflow.eclipse.tane";
    private static Activator plugin;

    public static Activator getDefault() {
        return plugin;
    }

    public Activator() {
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

	public static void log(String message, Exception e) {
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, e));
	}

	public static Shell getShell() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
