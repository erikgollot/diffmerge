package org.m4is.diffmerge.diff;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a modified object with all its changes
 * 
 * @author erik
 * 
 */
public class ModifiedObject implements IDiffEntity {
	Object oldObject;
	Object newObject;
	Object businessReference;

	public ModifiedObject(Object oldObject, Object newObject,
			Object businessReference) {
		this.newObject = newObject;
		this.oldObject = oldObject;
		this.businessReference = businessReference;
	}

	List<Diff> modifications = new ArrayList<Diff>();

	public List<Diff> getModifications() {
		return modifications;
	}

	public void addModification(Diff diff) {
		modifications.add(diff);
	}

	public Object getOldObject() {
		return oldObject;
	}

	public Object getNewObject() {
		return newObject;
	}

	public Object getBusinessReference() {
		return businessReference;
	}

	public Class<?> getClazz() {
		if (oldObject != null) {
			return oldObject.getClass();
		} else {
			return newObject.getClass();
		}
	}

	public Diff getModificationForField(Field field) {
		if (getModifications() != null) {
			for (Diff d : getModifications()) {
				if (d instanceof ValueChange) {
					ValueChange vc = (ValueChange) d;
					if (field == vc.getField()) {
						return vc;
					}
				}
			}
		}
		return null;
	}
}
