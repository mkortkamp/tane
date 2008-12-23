package com.stateofflow.eclipse.tane.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public class ProgressMonitorUtils {
    public static void checkCanceled(final IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
    }
}
