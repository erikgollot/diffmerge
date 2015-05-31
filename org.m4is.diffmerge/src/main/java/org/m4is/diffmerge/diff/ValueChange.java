package org.m4is.diffmerge.diff;

import java.lang.reflect.Field;

import org.m4is.diffmerge.reflection.FieldInfo;

public abstract class ValueChange implements Diff {
	Object oldValue;
	Object newValue;
	Field field;
	String opposite;
	FieldInfo fieldInfo;

	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}

	public String getOpposite() {
		return opposite;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Field getField() {
		return field;
	}

	public ValueChange(Field field, FieldInfo fieldInfo, Object oldValue, Object newValue,
			String opposite) {
		super();
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.opposite = opposite;
		this.fieldInfo = fieldInfo;
	}

}
