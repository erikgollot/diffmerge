package org.m4is.diffmerge.service;

public class EntityKey {
	Object key;

	public EntityKey(Object key, Class<?> clazz) {
		super();
		this.key = key;
		this.clazz = clazz;
	}

	public Object getKey() {
		return key;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	Class<?> clazz;
}
