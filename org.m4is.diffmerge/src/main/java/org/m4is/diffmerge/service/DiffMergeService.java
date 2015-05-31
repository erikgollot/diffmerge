package org.m4is.diffmerge.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.m4is.diffmerge.diff.CollectionValueChange;
import org.m4is.diffmerge.diff.DataPrimitiveValueChange;
import org.m4is.diffmerge.diff.DeletedObject;
import org.m4is.diffmerge.diff.Diff;
import org.m4is.diffmerge.diff.Differences;
import org.m4is.diffmerge.diff.EntitySwitchValueChange;
import org.m4is.diffmerge.diff.ModifiedObject;
import org.m4is.diffmerge.diff.NewObject;
import org.m4is.diffmerge.reflection.ClassDescriptor;
import org.m4is.diffmerge.reflection.FieldInfo;
import org.m4is.diffmerge.reflection.ReflectionException;
import org.m4is.diffmerge.reflection.ReflectionUtil;

public class DiffMergeService {
	ReflectionUtil reflection;

	public DiffMergeService() {
		reflection = new ReflectionUtil();
	}

	public DiffMergeService(String context) {
		reflection = new ReflectionUtil(context);
	}

	public Differences compare(Object oldVersion, Object newVersion) {
		// first, walk into both objects to find all entities that will need to
		// be compared. We create two maps, key is the businessReference of each
		// entity and a list of all keys.
		List<Object> allKeys = new ArrayList<Object>();
		EntitiesMap oldEntitiesMap = new EntitiesMap();
		EntitiesMap newEntitiesMap = new EntitiesMap();

		try {
			createEntityMap(oldVersion, oldEntitiesMap, allKeys,
					new ArrayList<Object>());
			createEntityMap(newVersion, newEntitiesMap, allKeys,
					new ArrayList<Object>());
			return new Differences(compareEntities(oldEntitiesMap,
					newEntitiesMap));
		} catch (IllegalAccessException e1) {
			throw new ReflectionException(
					"Cannot compare (illegal access) objects ["
							+ e1.getMessage() + "]");
		} catch (IllegalArgumentException e1) {
			throw new ReflectionException(
					"Cannot compare (illegal arg) objects [" + e1.getMessage()
							+ "]");
		} catch (InvocationTargetException e1) {
			throw new ReflectionException("Cannot compare (invoke) objects ["
					+ e1.getMessage() + "]");
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Cannot compare (nosuch) objects ["
					+ e.getMessage() + "]");
		} catch (SecurityException e) {
			throw new ReflectionException("Cannot compare (security) objects ["
					+ e.getMessage() + "]");
		}
	}

	private List<Diff> compareEntities(EntitiesMap oldEntitiesMap,
			EntitiesMap newEntitiesMap) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		List<Diff> ret = new ArrayList<Diff>();
		// First, search removed objects
		for (EntityKey br : oldEntitiesMap.keySet()) {
			if (newEntitiesMap.getEntity(br) == null) {
				// A removed entity
				DeletedObject deletedChange = new DeletedObject(
						oldEntitiesMap.getEntity(br),br);
				ret.add(deletedChange);
			}
		}
		// Then, search new objects and diffs
		for (EntityKey br : newEntitiesMap.keySet()) {
			Object oldObject = oldEntitiesMap.getEntity(br);
			if (oldObject == null) {
				// A new entity
				Object newObject = newEntitiesMap.getEntity(br);
				NewObject newChange = new NewObject(newObject,br);
				ret.add(newChange);
			} else {
				// Entity exists in both side, compare their structure
				Object newObject = newEntitiesMap.getEntity(br);
				ModifiedObject modificationChange = new ModifiedObject(
						oldObject, newObject, br);
				compareOldAndNew(oldObject, newObject, modificationChange);
				// If we've really some modifications, add them to list
				// otherwise...no
				if (modificationChange.getModifications() != null
						&& modificationChange.getModifications().size() > 0) {
					ret.add(modificationChange);
				}
			}
		}
		return ret;
	}

	private void compareOldAndNew(Object oldObject, Object newObject,
			ModifiedObject modificationChange) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// We've two versions of the same entity. They're different if :
		// 1- primitive attributes do not have the same values
		// 2- a collection attribute as its order modified and contained items
		// not the same
		// 3- an attribute which is a reference to another entity does not
		// reference the same entity
		// 4- an attribute which is a reference to a value object
		// 5- for collection or reference to a value object, it's more
		// complicated to says 'content has changed'because we consider that a
		// value object is different from
		// its previous version if we find a diff into the full graph starting
		// from the attribute itself, up to any reference to another entity. So
		// the difference can be at a low-level in the grap, not at first one.
		ClassDescriptor desc = getReflection().getClassDescriptor(
				oldObject.getClass());
		for (FieldInfo fieldInfo : desc.getFieldsToCheck()) {
			Object oldAttrVal = fieldInfo.getReadAccessor().invoke(oldObject);
			Object newAttrVal = fieldInfo.getReadAccessor().invoke(newObject);
			// We've a primitive type, we can compare values
			if (isPrimitiveType(fieldInfo)) {
				if (primitiveValuesAreDifferent(oldAttrVal, newAttrVal)) {
					DataPrimitiveValueChange primitiveChange = new DataPrimitiveValueChange(
							fieldInfo.getField(),fieldInfo, oldAttrVal, newAttrVal,
							fieldInfo.getOpposite());
					modificationChange.addModification(primitiveChange);
				}
			} else if (isReferenceToSingleEntity(fieldInfo)) {
				// First, check null values
				if (oldAttrVal != null || newAttrVal != null) {
					if (oldAttrVal != null && newAttrVal != null) {
						// Reference to another entity. We just check if
						// referenced
						// entities are the same i.e. have the same business
						// reference.
						ClassDescriptor oldReferencedDesc = getReflection()
								.getClassDescriptor(oldAttrVal.getClass());
						ClassDescriptor newReferencedDesc = getReflection()
								.getClassDescriptor(newAttrVal.getClass());
						// We do not check anything if class has changed
						if (oldReferencedDesc == newReferencedDesc) {
							Object oldBusinesRef = getBusinessReference(
									oldReferencedDesc, oldAttrVal);
							Object newBusinesRef = getBusinessReference(
									oldReferencedDesc, newAttrVal);
							if (primitiveValuesAreDifferent(oldBusinesRef,
									newBusinesRef)) {
								EntitySwitchValueChange switchChange = new EntitySwitchValueChange(
										fieldInfo.getField(), fieldInfo, oldAttrVal,
										newAttrVal, fieldInfo.getOpposite());
								modificationChange
										.addModification(switchChange);
							}
						} else {
							// Classes are different so objects have been
							// switched
							EntitySwitchValueChange switchChange = new EntitySwitchValueChange(
									fieldInfo.getField(), fieldInfo,oldAttrVal,
									newAttrVal, fieldInfo.getOpposite());
							modificationChange.addModification(switchChange);
						}
					} else {
						// One value is null. Not other test is needed
						EntitySwitchValueChange switchChange = new EntitySwitchValueChange(
								fieldInfo.getField(), fieldInfo,oldAttrVal, newAttrVal,
								fieldInfo.getOpposite());
						modificationChange.addModification(switchChange);
					}
				}
			} else {
				// Here we've a collection. Of primitive types or entities or
				// value objects or a single reference to a value object
				// Beware, collection can be null !
				if (oldAttrVal == null && newAttrVal == null) {
					// Nothing to do
				} else if (isReferenceToSingleValueObject(fieldInfo)) {
					ModifiedObject subModification = new ModifiedObject(
							oldAttrVal, newAttrVal, getBusinessReferenceIfAny(
									oldAttrVal, newAttrVal));
					compareOldAndNew(newAttrVal, oldAttrVal, subModification);
					if (subModification.getModifications().size() > 0) {
						modificationChange.addModification(subModification);
					}
				} else {
					CollectionValueChange collChange = new CollectionValueChange(
							fieldInfo.getField(), fieldInfo,oldAttrVal, newAttrVal,
							fieldInfo.getOpposite());

					// We will check :
					// 1- added items
					// 2- removed items
					// 3- order
					if (oldAttrVal == null && newAttrVal != null) {
						// Add all items of newAttrVal as added items
						Collection<?> newCollection = (Collection<?>) newAttrVal;
						for (Object o : newCollection) {
							collChange.addAddedItem(o);
						}
					}
					if (oldAttrVal != null && newAttrVal == null) {
						// Add all items of oldAttrVal as removed items
						Collection<?> oldCollection = (Collection<?>) oldAttrVal;
						for (Object o : oldCollection) {
							collChange.addRemovedItem(o);
						}
					}
					// Here we can check order and content (added/removed)
					if (oldAttrVal != null && newAttrVal != null) {
						Collection<?> newCollection = (Collection<?>) newAttrVal;
						Collection<?> oldCollection = (Collection<?>) oldAttrVal;

						// Order : we check order using the shorter size.
						int min = newCollection.size() < oldCollection.size() ? newCollection
								.size() : oldCollection.size();
						Iterator<?> itOld = ((Collection<?>) oldCollection)
								.iterator();
						Iterator<?> itNew = ((Collection<?>) newCollection)
								.iterator();
						// check order first
						compareCollectionsOrder(collChange, min, itOld, itNew);
						// Check content
						// need to find added and removed object whatever the
						// order is
						compareCollectionsContent(collChange, oldCollection,
								newCollection);
					}
					if (collChange.isSomethingDifferent()) {
						modificationChange.addModification(collChange);
					}
				}
			}
		}
	}

	private void compareCollectionsContent(CollectionValueChange collChange,
			Collection<?> oldCollection, Collection<?> newCollection) {
		// Both collections are not null but they can be collections of
		// primitives or value objects or entities
		// First, check type of object inside collection
		// Take first item of the non empty collection
		if (oldCollection.size() == 0 && newCollection.size() == 0)
			return;

		Collection<?> forCheck = oldCollection.size() > 0 ? oldCollection
				: newCollection;
		Object firstItem = forCheck.iterator().next();
		if (isStandardType(firstItem.getClass())) {
			// StandardTypes
			// Search removed
			for (Object o : oldCollection) {
				if (!newCollection.contains(o)) {
					collChange.addRemovedItem(o);
				}
			}
			// Search added
			for (Object o : newCollection) {
				if (!oldCollection.contains(o)) {
					collChange.addAddedItem(o);
				}
			}
		} else {
			ClassDescriptor desc = getReflection().getClassDescriptor(
					firstItem.getClass());
			if (desc.hasBusinessReference()) {
				// Entities
				Map<Object, Object> oldMap = new Hashtable<Object, Object>();
				Map<Object, Object> newMap = new Hashtable<Object, Object>();
				createEntityMapFromCollection(oldCollection, oldMap);
				createEntityMapFromCollection(newCollection, newMap);

				// Search removed
				for (Object br : oldMap.keySet()) {
					if (newMap.get(br) == null) {
						collChange.addRemovedItem(oldMap.get(br));
					}
				}
				// Search added
				for (Object br : newMap.keySet()) {
					if (oldMap.get(br) == null) {
						collChange.addAddedItem(newMap.get(br));
					}
				}

			} else {
				// ValueObject....to see later TODO
			}
		}
	}

	private void createEntityMapFromCollection(Collection<?> coll,
			Map<Object, Object> map) {
		for (Object item : coll) {
			ClassDescriptor desc = getReflection().getClassDescriptor(
					item.getClass());
			Object br = getBusinessReference(desc, item);
			map.put(br, item);
		}
	}

	private void compareCollectionsOrder(CollectionValueChange collChange,
			int min, Iterator<?> itOld, Iterator<?> itNew)
			throws IllegalAccessException, InvocationTargetException {
		for (int i = 0; i < min; i++) {
			Object oldItem = itOld.next();
			Object newItem = itNew.next();
			// Now we need to check if items are the same. Diff
			// depends on the nature of item primitive,
			// valueobject or collection or entity
			if (isStandardType(oldItem.getClass())) {
				if (primitiveValuesAreDifferent(oldItem, newItem)) {
					collChange.setOrderChanged(true);
					break;
				}
			} else {
				ClassDescriptor descItem = getReflection().getClassDescriptor(
						oldItem.getClass());
				// Two items are Entities
				if (descItem.hasBusinessReference()) {
					// We've entity
					Object brOldItem = getBusinessReference(descItem, oldItem);
					Object brNewItem = getBusinessReference(descItem, newItem);
					if (brOldItem == null && brNewItem != null
							|| (brOldItem != null && brNewItem == null)
							|| (!brOldItem.equals(brNewItem))) {
						collChange.setOrderChanged(true);
						break;
					}
				} else if (!Collection.class.isAssignableFrom(oldItem
						.getClass())) {
					// We do not support collection of
					// collection in check order
					// So here we assume that we've value
					// objects. They're different if compare
					// returns something.
					ModifiedObject subModification = new ModifiedObject(
							oldItem, newItem, getBusinessReferenceIfAny(
									oldItem, newItem));
					compareOldAndNew(oldItem, newItem, subModification);
					if (subModification.getModifications().size() > 0) {
						collChange.setOrderChanged(true);
						break;
					}
				}
			}
		}
	}

	private boolean primitiveValuesAreDifferent(Object oldAttrVal,
			Object newAttrVal) {
		return (oldAttrVal == null && newAttrVal != null)
				|| (oldAttrVal != null && newAttrVal == null)
				|| (!oldAttrVal.equals(newAttrVal));
	}

	private void createEntityMap(Object aObject, EntitiesMap map,
			List<Object> allKeys, List<Object> already)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		if (isStandardType(aObject.getClass()))
			return;
		if (already.contains(aObject))
			return;
		already.add(aObject);

		if (!isCollection(aObject)) {
			// Get object descriptor to know if it is an entity, primitive type
			// or
			// valueobject or collection
			ClassDescriptor desc = getReflection().getDescriptor(aObject);
			// Got entity, add it to map
			if (desc.hasBusinessReference()) {
				// An entity
				Object br = getBusinessReference(desc, aObject);
				map.addObject(br, aObject);
				allKeys.add(br);
			}
		}
		// Now recurse into Entities, valueobjects and collection
		// If object is a collection, walk into each items
		if (isCollection(aObject)) {
			Collection<?> collection = (Collection<?>) aObject;
			for (Object o : collection) {
				createEntityMap(o, map, allKeys, already);
			}
		} else if (!isStandardType(aObject.getClass())) {
			ClassDescriptor desc = getReflection().getDescriptor(aObject);
			// Type is not primitive, we walk into all fields (here attr can be
			// valueobject or entity
			for (FieldInfo fieldInfo : desc.getFieldsToCheck()) {
				Object attr = fieldInfo.getReadAccessor().invoke(aObject);
				if (attr != null) {
					createEntityMap(attr, map, allKeys, already);
				}
			}
		}
	}

	private boolean isCollection(Object aObject) {
		return Collection.class.isAssignableFrom(aObject.getClass());
	}

	private boolean isReferenceToSingleValueObject(FieldInfo field) {
		if (!isPrimitiveType(field) && field.getAssociation() != null
				&& !field.getAssociation().isCollection()) {
			// now check if field type has a business reference
			ClassDescriptor cd = reflection.getClassDescriptor(field.getField()
					.getType());
			if (cd.hasBusinessReference())
				return false;
			else
				return true;
		} else {
			return false;
		}
	}

	private boolean isReferenceToSingleEntity(FieldInfo field) {
		if (!isPrimitiveType(field)
				&& (field.getAssociation() == null || !field.getAssociation()
						.isCollection())) {
			// now check if field type has a business reference
			ClassDescriptor cd = reflection.getClassDescriptor(field.getField()
					.getType());
			if (cd.hasBusinessReference())
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	private boolean isPrimitiveType(FieldInfo field) {
		return isStandardType(field.getField().getType());
	}

	static Class<?> primitives[] = { Number.class, Date.class, Calendar.class,
			Character.class, String.class };

	private boolean isStandardType(Class<?> type) {

		if (type.isPrimitive() || type.isEnum()) {
			return true;
		} else {
			for (int i = 0; i < primitives.length; i++) {
				if (primitives[i].isAssignableFrom(type)) {
					return true;
				}
			}
			return false;
		}
	}

	private Object getBusinessReferenceIfAny(Object o1, Object o2) {
		Object o = o1 == null ? o2 : o1;
		if (o == null) {
			return null;
		} else {
			ClassDescriptor desc = getReflection().getClassDescriptor(
					o.getClass());
			if (desc.hasBusinessReference()) {
				if (o1 != null) {
					return getBusinessReference(desc, o1);
				} else if (o2 != null) {
					return getBusinessReference(desc, o2);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	private Object getBusinessReference(ClassDescriptor desc, Object obj) {
		try {
			Object o = desc.getBusinessReferenceMethod().invoke(obj);
			return o;
		} catch (IllegalAccessException e) {
			ReflectionException ex = new ReflectionException(
					"ERROR(access): cannot retrieve business reference of object "
							+ desc.getClass().getCanonicalName()
							+ " business reference field accessor = "
							+ desc.getBusinessReference());
			ex.setStackTrace(ex.getStackTrace());
			throw ex;
		} catch (IllegalArgumentException e) {
			ReflectionException ex = new ReflectionException(
					"ERROR(argument): cannot retrieve business reference of object "
							+ desc.getClazz().getName()
							+ ", business reference field accessor = "
							+ desc.getBusinessReference() + " (root message : "
							+ e.getMessage() + ")");

			throw ex;
		} catch (InvocationTargetException e) {
			ReflectionException ex = new ReflectionException(
					"ERROR(invoke): cannot retrieve business reference of object "
							+ desc.getClass().getCanonicalName()
							+ " business reference field accessor = "
							+ desc.getBusinessReference());

			throw ex;
		} catch (SecurityException e) {
			ReflectionException ex = new ReflectionException(
					"ERROR(security): cannot retrieve business reference of object "
							+ desc.getClass().getCanonicalName()
							+ " business reference field accessor = "
							+ desc.getBusinessReference());

			throw ex;
		}
	}

	protected ReflectionUtil getReflection() {
		return reflection;
	}
}
