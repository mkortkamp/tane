package com.stateofflow.eclipse.tane.flowanalysis;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import com.stateofflow.eclipse.tane.util.Range;

abstract class AbstractFrameBasedAnalyser<T> extends ASTVisitor {
	protected final Stack<Set<T>> frames = new Stack<Set<T>>();
	private final Set<T> result = new HashSet<T>();

	protected final Range range;

	public AbstractFrameBasedAnalyser(Range range) {
		this.range = range;
		pushFrame();
	}
	
	public Set<T> getResult() {
		return result;
	}
	
	public boolean isEmpty() {
		return getResult().isEmpty();
	}

	protected void pushFrame() {
		frames.push(new HashSet<T>());
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

	protected void popFrame(ASTNode node) {
		if (isInRange(node)) {
			frames.pop();
		}
	}

	protected void addToCurrentFrame(T t) {
		frames.peek().add(t);
	}

	protected boolean addToResult(T t) {
		return result.add(t);
	}

}