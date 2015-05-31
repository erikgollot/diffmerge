package org.m4is.diffmerge.diff;

public class DeletedObject implements IDiffEntity {
	Object object;
	Object businessReference;
	
	public DeletedObject(Object o,Object businessReference) {
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
