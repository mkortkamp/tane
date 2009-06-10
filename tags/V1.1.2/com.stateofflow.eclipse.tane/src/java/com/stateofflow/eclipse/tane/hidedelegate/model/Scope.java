package com.stateofflow.eclipse.tane.hidedelegate.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;

public enum Scope {
    WORKSPACE {
        @Override
        public IJavaSearchScope createSearchEngineScope(final ICompilationUnit element) {
            return SearchEngine.createWorkspaceScope();
        }
    },
    PROJECT {
        @Override
        public IJavaSearchScope createSearchEngineScope(final ICompilationUnit element) {
            return SearchEngine.createJavaSearchScope(new IJavaElement[]{element.getJavaProject()});
        }
    },
    COMPILATION_UNIT {
        @Override
        public IJavaSearchScope createSearchEngineScope(final ICompilationUnit element) {
            return SearchEngine.createJavaSearchScope(new IJavaElement[]{element.getAncestor(IJavaElement.COMPILATION_UNIT)});
        }
    };

    public abstract IJavaSearchScope createSearchEngineScope(final ICompilationUnit element);
}
