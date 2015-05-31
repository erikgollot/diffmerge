package org.m4is.diffmerge.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.m4is.diffmerge.stereotype.Association;
import org.m4is.diffmerge.stereotype.BusinessReference;
import org.m4is.diffmerge.stereotype.DiffMergeIgnore;

public class ReflectionUtil {
	Map<Class<?>, List<Field>> fieldsMap = new Hashtable<Class<?>, List<Field>>();
	Map<Class<?>, List<Field>> fieldsWithoutDiffMergeMap = new Hashtable<Class<?>, List<Field>>();
	Map<Field, Method> fieldsGetterMethods = new Hashtable<Field, Method>();
	Map<Class<?>, List<Method>> methodsMap = new Hashtable<Class<?>, List<Method>>();

	Map<Class<?>, ClassDescriptor> descriptors = new Hashtable<Class<?>, ClassDescriptor>();

	/**
	 * Execution context used to filter or not some ignored fields. It works
	 * with @DiffMergeIgnore and its 'excludingContexts' adn 'inContexts'
	 * attribute. See @DiffMergeIgnore annotation for more information
	 */
	String context;

	public ReflectionUtil() {

	}

	public ReflectionUtil(String context) {
		this.context = context;
	}

	public List<Field> getAllFields(Object o) {
		return getAllFields(o.getClass());
	}

	public List<Field> getAllFieldsExcludingAnnotates(Object o,
			Class<? extends Annotation>... annotations) {
		return getAllFieldsExcludingAnnotates(o.getClass(), annotations);
	}

	public List<Field> getAllFieldsExcludingAnnotates(Class<?> clazz,
			Class<? extends Annotation>... annotations) {
		List<Field> all = getAllFields(clazz);
		// Now filter
		List<Field> toRemove = new ArrayList<Field>();
		for (Field f : all) {
			for (Class<? extends Annotation> annotation : annotations) {
				if (f.getAnnotation(annotation) != null) {
					// Need to check DiffMergeIgnore here
					if (annotation == DiffMergeIgnore.class) {
						DiffMergeIgnore dmi = (DiffMergeIgnore) f
								.getAnnotation(annotation);
						if (getContext() != null && getContext().length() != 0) {
							if (dmi.inContext().length == 1
									&& dmi.inContext()[0].length() == 0) {
								toRemove.add(f);
							} else if (dmi.inContext().length > 0
									&& isContextIn(dmi.inContext())) {
								toRemove.add(f);
							} else if (dmi.excludingContext().length > 0
									&& !isContextIn(dmi.excludingContext())) {
								toRemove.add(f);
							}
						} else {
							// No context and inContext = "", we exclude the
							// field
							// If inContext != "" it means that just in this
							// context
							// we've to ignore the field. So because context=""
							// we
							// do not exclude the field (the else that we've
							// have
							// not written after this if.
							// No need to check excludingContext here.
							if (dmi.inContext().length == 1
									&& dmi.inContext()[0].length() == 0) {
								toRemove.add(f);
							}
						}
					} else {
						// Not the DiffMerge annotation, we exclude
						toRemove.add(f);
					}
					break;
				}
			}
		}
		all.removeAll(toRemove);
		return all;
	}

	private boolean isContextIn(String[] contexts) {
		for (int i = 0; i < contexts.length; i++) {
			if (getContext().equals(contexts[i])) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Field> getAllFieldsExcludingDiffMergeIgnore(Class<?> clazz) {
		List<Field> fields = fieldsWithoutDiffMergeMap.get(clazz);
		if (fields != null) {
			return fields;
		} else {
			fields = getAllFieldsExcludingAnnotates(clazz,
					DiffMergeIgnore.class);
			fieldsWithoutDiffMergeMap.put(clazz, fields);
			return fields;
		}
	}

	public List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = fieldsMap.get(clazz);
		if (fields != null) {
			return fields;
		} else {
			fields = new ArrayList<Field>();
			getAllFields(clazz, fields);
			fieldsMap.put(clazz, fields);
			return fields;
		}
	}

	private void getAllFields(Class<?> clazz, List<Field> fields) {
		// List<Field> existingFields = fieldsMap.get(clazz);
		// if (existingFields != null) {
		// fields.addAll(existingFields);
		// } else {
		List<Field> ownedFields = Arrays.asList(clazz.getDeclaredFields());
		if (clazz.getSuperclass() != null) {
			getAllFields(clazz.getSuperclass(), fields);
		}
		fields.addAll(ownedFields);
		fieldsMap.put(clazz, fields);
		// }
	}

	public List<Method> getAllMethods(Class<?> clazz) {
		List<Method> methods = methodsMap.get(clazz);
		if (methods != null) {
			return methods;
		} else {
			methods = new ArrayList<Method>();
			getAllMethods(clazz, methods);
			methodsMap.put(clazz, methods);
			return methods;
		}
	}

	private void getAllMethods(Class<?> clazz, List<Method> methods) {
//		List<Method> existingMethods = methodsMap.get(clazz);
//		if (existingMethods != null) {
//			methods.addAll(existingMethods);
//		} else {
			List<Method> ownedMethods = Arrays.asList(clazz
					.getDeclaredMethods());
			if (clazz.getSuperclass() != null) {
				getAllMethods(clazz.getSuperclass(), methods);
			}
			methods.addAll(ownedMethods);
			methodsMap.put(clazz, methods);
		//}
	}

	public Method getGetter(Class<?> clazz, Field field) {
		Method m = fieldsGetterMethods.get(field);
		if (m != null) {
			return m;
		} else {
			String methodName;
			if (field.getType().isPrimitive()
					&& field.getType().equals(boolean.class)) {
				methodName = "is";
			} else {
				methodName = "get";
			}

			methodName += field.getName().substring(0, 1).toUpperCase()
					+ (field.getName().length() == 1 ? "" : field.getName()
							.substring(1));
			try {
				for (Method met : getAllMethods(clazz)) {
					if (met.getName().equals(methodName)
							&& met.getReturnType() == field.getType()) {
						met.setAccessible(true);
						fieldsGetterMethods.put(field, met);
						return met;
					}
				}
			} catch (SecurityException e) {
				return null;
			}
		}
		return null;
	}

	public boolean isCollection(Field f) {
		return Collection.class.isAssignableFrom(f.getType());
	}

	public AssociationInfo getAssociation(Field f) {
		if (isCollection(f) || f.getAnnotation(Association.class) != null) {
			// ok, we've an association. Information can be retrieved from
			// AssociationAnnotation or from jpa annotations
			AssociationInfo info = new AssociationInfo(false, false, null);

			if (isCollection(f)) {
				info.setCollection(true);
			}
			// Search is OneToMany, OneToOne, ManytoMany are applied
			Annotation ann = null;
			if ((ann = f.getAnnotation(OneToMany.class)) != null) {
				String mappedBy = ((OneToMany) ann).mappedBy();
				if (mappedBy != null && mappedBy.length() > 0) {
					info.setBidirectional(true);
					info.setOpposite(mappedBy);
				}
			} else if ((ann = f.getAnnotation(OneToOne.class)) != null) {
				String mappedBy = ((OneToOne) ann).mappedBy();
				if (mappedBy != null && mappedBy.length() > 0) {
					info.setBidirectional(true);
					info.setOpposite(mappedBy);
				}
			} else if ((ann = f.getAnnotation(ManyToMany.class)) != null) {
				String mappedBy = ((ManyToMany) ann).mappedBy();
				if (mappedBy != null && mappedBy.length() > 0) {
					info.setBidirectional(true);
					info.setOpposite(mappedBy);
				}
			} else if ((ann = f.getAnnotation(Association.class)) != null) {
				String mappedBy = ((Association) ann).opposite();
				if (mappedBy != null && mappedBy.length() > 0) {
					info.setBidirectional(true);
					info.setOpposite(mappedBy);
				}
			}
			return info;
		} else {
			return null;
		}
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public ClassDescriptor getDescriptor(Object entity) {
		return getClassDescriptor(entity.getClass());
	}

	public ClassDescriptor getClassDescriptor(Class<?> entityClass) {
		ClassDescriptor cd = null;
		if ((cd = descriptors.get(entityClass)) != null) {
			return cd;
		} else {
			cd = new ClassDescriptor(entityClass);
			descriptors.put(entityClass, cd);
			List<Field> fields = getAllFieldsExcludingDiffMergeIgnore(entityClass);
			Field businessReferenceField = null;
			for (Field f : fields) {
				// Need to exclude BusinessReference from compared fields but
				// keep it for ClassDescriptor
				if (f.getAnnotation(BusinessReference.class) != null) {
					businessReferenceField = f;
				} else {
					AssociationInfo association = getAssociation(f);
					Method readAccessor = getGetter(entityClass, f);
					if (readAccessor == null) {
						throw new ReflectionException(
								"ERROR: no getter for field [" + f.getName()
										+ "] into class "
										+ entityClass.getCanonicalName());
					}
					FieldInfo info = new FieldInfo(f, association, readAccessor);
					cd.addField(info);
				}
			}
			// Now, if we've a field marked with BusinessReference, use it as
			// the business reference
			if (businessReferenceField != null) {
				Method readAccessor = getGetter(entityClass,
						businessReferenceField);
				if (readAccessor == null) {
					throw new ReflectionException(
							"ERROR: no getter for business reference field "
									+ businessReferenceField.getName()
									+ " into class "
									+ entityClass.getCanonicalName());
				}
				cd.setBusinessReference(businessReferenceField.getName());
				cd.setBusinessReferenceMethod(readAccessor);
			} else {
				// look if a method is marked as BusinessReference, take the
				// first one
				List<Method> methods = getAllMethods(entityClass);
				for (Method m : methods) {
					if (m.getAnnotation(BusinessReference.class) != null) {
						cd.setBusinessReferenceMethod(m);
						break;
					}
				}
			}
			return cd;
		}
	}
}
