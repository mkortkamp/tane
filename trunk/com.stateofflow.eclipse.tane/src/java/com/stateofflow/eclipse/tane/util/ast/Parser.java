package com.stateofflow.eclipse.tane.util.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.stateofflow.eclipse.tane.util.ProgressMonitorUtils;

public class Parser {
    private ASTParser createParser() {
        final ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setResolveBindings(true);
        return parser;
    }

    private Map<IJavaProject, Set<ICompilationUnit>> getCompilationUnitsByProject(final Set<ICompilationUnit> compilationUnits) {
        final Map<IJavaProject, Set<ICompilationUnit>> result = new HashMap<IJavaProject, Set<ICompilationUnit>>();
        for (final ICompilationUnit compilationUnit : compilationUnits) {
            if (!result.containsKey(compilationUnit.getJavaProject())) {
                result.put(compilationUnit.getJavaProject(), new HashSet<ICompilationUnit>());
            }
            result.get(compilationUnit.getJavaProject()).add(compilationUnit);
        }
        return result;
    }

    public CompilationUnit parse(final ICompilationUnit compilationUnit) {
        final ASTParser parser = createParser();
        parser.setSource(compilationUnit);
        return (CompilationUnit) parser.createAST(new NullProgressMonitor());
    }

    public void parse(final Set<ICompilationUnit> compilationUnits, final ASTRequestor requestor, final SubMonitor progressMonitor) {
        final Map<IJavaProject, Set<ICompilationUnit>> compilationUnitsByProject = getCompilationUnitsByProject(compilationUnits);
        progressMonitor.beginTask("Parsing", compilationUnitsByProject.size());
        for (final Map.Entry<IJavaProject, Set<ICompilationUnit>> entry : compilationUnitsByProject.entrySet()) {
            final ASTParser parser = createParser();
            parser.setProject(entry.getKey());
            parser.createASTs(entry.getValue().toArray(new ICompilationUnit[entry.getValue().size()]), new String[0], requestor, progressMonitor.newChild(1));
            ProgressMonitorUtils.checkCanceled(progressMonitor);
        }
    }
}
