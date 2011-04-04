package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;

import com.stateofflow.eclipse.tane.util.Range;

public class VariableReferenceAnalyser extends ASTVisitor implements Analyser<SimpleName> {
	private final SimpleName declaration;
	private Range range;
	private Set<SimpleName> references;

	public VariableReferenceAnalyser(SimpleName declaration) {
		this.declaration = declaration;
	}
	
	public Set<SimpleName> analyse(Range rangeOfInterest, ASTNode node) {
		this.range = rangeOfInterest;
		references = new HashSet<SimpleName>();
		node.accept(this);
		return references;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		if (range.includes(node.getStartPosition(), node.getLength()) && node.resolveBinding() == declaration.resolveBinding()) {
			references.add(node);
		}
		
		return false;
	}
}
