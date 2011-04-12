package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.Collection;

import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.util.Range;

public interface Analyser<T> {
	Collection<T> analyse(Range range, ASTNode node);
}
