package org.m4is.diffmerge.diff;

import java.lang.reflect.Field;

import org.m4is.diffmerge.reflection.FieldInfo;

public class DataPrimitiveValueChange extends ValueChange {

	public DataPrimitiveValueChange(Field field,FieldInfo fieldInfo, Object oldValue,
			Object newValue, String opposite) {
		super(field, fieldInfo,oldValue, newValue, opposite);
	}

	public Class<?> getClazz() {
		return newValue.getClass();
	}
}
