package org.m4is.diffmerge.diff;

public interface IDiffEntity extends Diff {
	Object getBusinessReference();
	Class<?> getClazz();
}
