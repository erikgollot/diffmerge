package org.m4is.diffmerge.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to describe a class that will be compared. It contains all fields that
 * need to be compared, the businessReference of the class in terms of getter or
 * method accessor.
 * 
 * @author erik
 * 
 */
public class ClassDescriptor {
	public Method getBusinessReferenceMethod() {
		return businessReferenceMethod;
	}

	public void setBusinessReferenceMethod(Method businessReferenceMethod) {
		this.businessReferenceMethod = businessReferenceMethod;
	}

	List<FieldInfo> fieldsToCheck = new ArrayList<FieldInfo>();;
	String businessReference;
	Method businessReferenceMethod;
	
	Class<?> clazz;

	public ClassDescriptor(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void addField(FieldInfo f) {
		getFieldsToCheck().add(f);
	}

	public List<FieldInfo> getFieldsToCheck() {
		return fieldsToCheck;
	}

	public String getBusinessReference() {
		return businessReference;
	}

	public void setBusinessReference(String businessReference) {
		this.businessReference = businessReference;
	}

	public boolean hasBusinessReference() {
		return (businessReferenceMethod != null);
	}
}
