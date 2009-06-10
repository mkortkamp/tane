package com.stateofflow.eclipse.tane.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.search.SearchMatch;

public class CompilationUnitSearchMatchGrouper {
    public Map<ICompilationUnit, Set<SearchMatch>> getMatchesByCompilationUnit(final Set<SearchMatch> matches) {
        final Map<ICompilationUnit, Set<SearchMatch>> result = new HashMap<ICompilationUnit, Set<SearchMatch>>();
        for (final SearchMatch match : matches) {
            addMatch(result, match, ((IMember) match.getElement()).getCompilationUnit());
        }
        return result;
    }

    private void addMatch(final Map<ICompilationUnit, Set<SearchMatch>> result, final SearchMatch match, final ICompilationUnit compilationUnit) {
        if (compilationUnit == null) {
            return;
        }
        if (!result.containsKey(compilationUnit)) {
            result.put(compilationUnit, new HashSet<SearchMatch>());
        }
        result.get(compilationUnit).add(match);
    }
}
