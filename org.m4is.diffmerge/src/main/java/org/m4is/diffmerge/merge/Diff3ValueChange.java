package org.m4is.diffmerge.merge;

import java.lang.reflect.Field;

public class Diff3ValueChange {
	Object baseValue;
	Object leftValue;
	Object rightValue;
	Field field;
	String opposite;
	boolean isConflict = false;

	public Diff3ValueChange(Object baseValue, Object leftValue,
			Object rightValue, Field field, String opposite,boolean isConflict) {
		super();
		this.baseValue = baseValue;
		this.leftValue = leftValue;
		this.rightValue = rightValue;
		this.field = field;
		this.opposite = opposite;
		this.isConflict=isConflict;
	}

	public Object getBaseValue() {
		return baseValue;
	}

	public Object getLeftValue() {
		return leftValue;
	}

	public Object getRightValue() {
		return rightValue;
	}

	public Field getField() {
		return field;
	}

	public String getOpposite() {
		return opposite;
	}

	public boolean isConflict() {
		return isConflict;
	}

	public void setConflict(boolean isConflict) {
		this.isConflict = isConflict;
	}

	public void setBaseValue(Object baseValue) {
		this.baseValue = baseValue;
	}

	public void setLeftValue(Object leftValue) {
		this.leftValue = leftValue;
	}

	public void setRightValue(Object rightValue) {
		this.rightValue = rightValue;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setOpposite(String opposite) {
		this.opposite = opposite;
	}
}
