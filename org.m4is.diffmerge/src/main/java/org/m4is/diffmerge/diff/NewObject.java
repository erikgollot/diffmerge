package org.m4is.diffmerge.diff;

/**
 * Represents a new object
 * 
 * @author erik
 * 
 */
public class NewObject implements IDiffEntity {
	Object object;
	Object businessReference;
	
	public NewObject(Object o,Object businessReference) {
		this.object = o;
		this.businessReference=businessReference;
	}

	public Object getObject() {
		return object;
	}

	public Class<?> getClazz() {
		return object.getClass();
	}

	public Object getBusinessReference() {
		return businessReference;
	}
}
