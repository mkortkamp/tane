package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import com.stateofflow.eclipse.tane.util.Range;

public interface Analyser<T> {
	Set<T> analyse(Range range, ASTNode node);
}
