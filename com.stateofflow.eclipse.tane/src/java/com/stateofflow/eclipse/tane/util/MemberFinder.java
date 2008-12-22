package com.stateofflow.eclipse.tane.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class MemberFinder {
    private final IJavaSearchScope scope;

    public MemberFinder(final IJavaSearchScope scope) {
        this.scope = scope;
    }

    public Set<SearchMatch> getMatches(final IJavaElement element, final int limitTo, final SubMonitor progressMonitor) throws CoreException {
        progressMonitor.beginTask("Finding matches for " + element.getElementName(), 1);
        final Set<SearchMatch> matches = new HashSet<SearchMatch>();
        final SearchPattern pattern = SearchPattern.createPattern(element, limitTo, SearchPattern.R_EQUIVALENT_MATCH);
        new SearchEngine().search(pattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, new SearchRequestor() {
            @Override
            public void acceptSearchMatch(final SearchMatch match) throws CoreException {
                if (match.getAccuracy() == SearchMatch.A_ACCURATE && !match.isInsideDocComment() && match.getElement() instanceof IMember) {
                    matches.add(match);
                }
            }
        }, progressMonitor.newChild(1));
        ProgressMonitorUtils.checkCanceled(progressMonitor);
        return matches;
    }
}
