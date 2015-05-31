package org.m4is.diffmerge.service;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class EntitiesMap {
	Map<Class<?>, Map<Object, Object>> maps = new Hashtable<Class<?>, Map<Object, Object>>();

	public void addObject(Object reference, Object entity) {
		Map<Object, Object> objects = maps.get(entity.getClass());
		if (objects == null) {
			objects = new Hashtable<Object, Object>();
			maps.put(entity.getClass(), objects);
		}
		objects.put(reference, entity);
	}

	public Set<EntityKey> keySet() {
		Set<EntityKey> keys = new HashSet<EntityKey>();
		for (Class<?> c : maps.keySet()) {
			Map<Object, Object> entities = maps.get(c);
			for (Object ref : entities.keySet()) {
				EntityKey k = new EntityKey(ref, c);
				keys.add(k);
			}
		}
		return keys;
	}
	
	public int numElements() {
		int i = 0;
		for (Class<?> c : maps.keySet()) {
			Map<Object, Object> entities = maps.get(c);
			i+=entities.keySet().size();
		}
		return i;
	}

	public Object getEntity(EntityKey key) {
		return maps.get(key.getClazz()).get(key.getKey());
	}
}
