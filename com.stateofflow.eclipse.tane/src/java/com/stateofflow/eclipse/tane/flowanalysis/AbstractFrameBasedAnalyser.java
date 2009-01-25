package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import com.stateofflow.eclipse.tane.util.Range;

abstract class AbstractFrameBasedAnalyser<T> extends ASTVisitor implements Analyser<T> {
	protected final Stack<Set<T>> frames = new Stack<Set<T>>();
	private final Set<T> result = new HashSet<T>();
	private Range range;

	public Set<T> analyse(Range rangeOfInterest, ASTNode node) {
		pushFrame();
		this.range = rangeOfInterest;
		node.accept(this);
		popFrame();
		return result;
	}

	protected boolean isInFrames(T variableBinding) {
		for (Set<T> frame : frames) {
			if (frame.contains(variableBinding)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isInRange(ASTNode node) {
		return range.includes(node.getStartPosition(), node.getLength());
	}

	protected void pushFrame(ASTNode node) {
		if (isInRange(node)) {
			pushFrame();
		}
	}
	
	protected void pushFrame() {
		frames.push(new HashSet<T>());
	}

	protected void popFrame(ASTNode node) {
		if (isInRange(node)) {
			popFrame();
		}
	}

	private Set<T> popFrame() {
		return frames.pop();
	}

	protected void addToCurrentFrame(T t) {
		frames.peek().add(t);
	}

	protected boolean addToResult(T t) {
		return result.add(t);
	}

}