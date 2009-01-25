package com.stateofflow.eclipse.tane.hidedelegate.model.rewrite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import com.stateofflow.eclipse.tane.rewrite.Rewrite;

public class RewriteMap {
    private final Map<ICompilationUnit, Rewrite> rewrites = new HashMap<ICompilationUnit, Rewrite>();

    public void put(final ICompilationUnit compilationUnit, final Rewrite rewrite) {
        rewrites.put(compilationUnit, rewrite);
    }

    public Rewrite get(final ICompilationUnit compilationUnit) {
        return rewrites.get(compilationUnit);
    }

    public Change createChange(final String name) throws CoreException {
        final CompositeChange changes = new CompositeChange(name);
        for (final Rewrite rewrite : rewrites.values()) {
            rewrite.addChange(changes);
        }
        return changes;
    }
}
