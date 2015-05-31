package org.m4is.diffmerge.diff;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.m4is.diffmerge.reflection.ClassDescriptor;
import org.m4is.diffmerge.reflection.FieldInfo;
import org.m4is.diffmerge.reflection.ReflectionUtil;

public class EntitySwitchValueChange extends ValueChange {

	public EntitySwitchValueChange(Field field, FieldInfo fieldInfo,
			Object oldValue, Object newValue, String opposite) {
		super(field, fieldInfo, oldValue, newValue, opposite);
	}

	public Class<?> getClazz() {
		return getNewValue().getClass();
	}

	public Object getNewBusinessReference(ReflectionUtil reflection) {
		ClassDescriptor desc = reflection.getDescriptor(newValue);
		try {
			return desc.getBusinessReferenceMethod().invoke(newValue);
		} catch (IllegalAccessException e) {
			// Should never happen here
		} catch (IllegalArgumentException e) {
			// Should never happen here
		} catch (InvocationTargetException e) {
			// Should never happen here
		}
		return null;
	}
}
