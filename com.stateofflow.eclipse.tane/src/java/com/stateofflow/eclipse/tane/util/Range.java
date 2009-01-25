package com.stateofflow.eclipse.tane.util;

public class Range {
	private final int start;
	private final int length;

	public Range(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public boolean includes(int anotherStart, int anotherLength) {
		return start <= anotherStart && start + length >= anotherStart + anotherLength;
	}
}
