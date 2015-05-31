package org.m4is.diffmerge.merge;

import java.lang.reflect.Field;

public class Diff3DataPrimitiveValueChange extends Diff3ValueChange {

	public Diff3DataPrimitiveValueChange(Object baseValue, Object leftValue,
			Object rightValue, Field field, String opposite,boolean isConflict) {
		super(baseValue, leftValue, rightValue, field, opposite,isConflict);
	}

}
