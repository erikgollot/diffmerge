package org.m4is.diffmerge.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldInfo {
	Field field;
	AssociationInfo association;
	Method readAccessor;

	public FieldInfo(Field field, AssociationInfo association,
			Method readAccessor) {
		super();
		this.field = field;
		this.association = association;
		this.readAccessor = readAccessor;
	}

	public Field getField() {
		return field;
	}

	public AssociationInfo getAssociation() {
		return association;
	}

	public Method getReadAccessor() {
		return readAccessor;
	}

	public String getOpposite() {
		if (getAssociation() == null) {
			return null;
		} else {
			return getAssociation().getOpposite();
		}
	}

}
