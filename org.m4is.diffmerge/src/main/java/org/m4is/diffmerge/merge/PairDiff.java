package org.m4is.diffmerge.merge;

import org.m4is.diffmerge.diff.Diff;

public class PairDiff {
	Class<?> clazz;
	Diff left;
	Diff right;
	
	public PairDiff(Diff left, Diff right, Class<?> clazz) {
		super();
		this.left = left;
		this.right = right;
		this.clazz = clazz;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public Diff getLeft() {
		return left;
	}
	public Diff getRight() {
		return right;
	}
}
